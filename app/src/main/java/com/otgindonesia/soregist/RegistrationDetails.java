package com.otgindonesia.soregist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.otgindonesia.soregist.Data.TicketDataModel;
import com.otgindonesia.soregist.Data.TicketModel;
import com.otgindonesia.soregist.Utilities.RestClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationDetails extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = this.getClass().getSimpleName();
    ImageView mImageView;
    TextView et_registeredName, et_uplineName;
    EditText /*et_registeredName, et_uplineName,*/ et_name;
    Uri outPutFileUri;
    Button btn_takePhoto, btn_cancel, btn_register;
    TicketModel ticketModel;
    String imageUri;
    RestClient.RestAPI client;
    File file;
    String imageFilePath;
    //File compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        client = RestClient.getClient();
        Gson gson = new Gson();
        String strTicket = getIntent().getStringExtra("ticket");
        ticketModel = gson.fromJson(strTicket, TicketModel.class);
        ticketModel.setQrcode(getIntent().getStringExtra("qrcode"));

        //Toast.makeText(this, ticketModel.getStatus(), Toast.LENGTH_SHORT).show();

        imageUri = "";
        mImageView = findViewById(R.id.iv_profile_picture);

        et_registeredName = findViewById(R.id.et_registered_name);
        et_uplineName = findViewById(R.id.et_upline_name);
        et_name = findViewById(R.id.et_name);

        btn_takePhoto = findViewById(R.id.btn_takePhoto);
        btn_takePhoto.setOnClickListener(this);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);

        /**
         * Initialize data
         */

        et_registeredName.setText(ticketModel.getTicketData().getGivenName());
        et_uplineName.setText(ticketModel.getTicketData().getUplineName());

        if(isRegistered(ticketModel.getTicketData())){
            viewOnly();
            Picasso.get().load(ticketModel.getTicketData().getPhoto()).into(mImageView);

            et_name.setText(ticketModel.getTicketData().getName());
            et_name.setEnabled(false);
        }

    }

    private boolean isRegistered(TicketDataModel data){
        if(data.getName() == null && data.getPhoto() == null){
            return false;
        }else{
            return true;
        }
    }

    private void viewOnly(){
        btn_takePhoto.setVisibility(Button.GONE);
        btn_register.setVisibility(Button.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_takePhoto){
            takePictureIntent();
        }else if(view == btn_cancel){
            finish();
        }else if(view == btn_register){
            if(imageUri!=""){
                final ProgressDialog nDialog;
                nDialog = new ProgressDialog(RegistrationDetails.this);
                nDialog.setMessage(getString(R.string.loading));
                nDialog.setTitle(getString(R.string.register_ticket));
                nDialog.setIndeterminate(false);
                nDialog.setCancelable(true);
                nDialog.show();

                File compressedfile = file;
                try{
                    compressedfile = new Compressor(this).compressToFile(file);
                }catch (IOException e){
                    Log.v(TAG, getString(R.string.compress_failed));
                }
                //File file = new File(imageUri);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/formdata"), compressedfile);

                MultipartBody.Part photo =
                        MultipartBody.Part.createFormData("photo", compressedfile.getName(), requestFile);

                RequestBody qrcode =
                        RequestBody.create(MediaType.parse("multipart/form-data"), ticketModel.getQrcode());

                RequestBody name =
                        RequestBody.create(MediaType.parse("multipart/form-data"), et_name.getText().toString());

                final Call<TicketModel> responseBodyCall = client.submitTicket(qrcode,name,photo);

                responseBodyCall.enqueue(new Callback<TicketModel>() {
                    @Override
                    public void onResponse(Call<TicketModel> call, Response<TicketModel> response) {
                        TicketModel newTicketStatus = response.body();
                        //Toast.makeText(RegistrationDetails.this, newTicketStatus.getStatus(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationDetails.this, RegistrationSuccess.class);

                        nDialog.dismiss();

                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<TicketModel> call, Throwable t) {
                        Toast.makeText(RegistrationDetails.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePictureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try{
            file = createImageFile();
        }catch (IOException e){
            Log.v(TAG, "error creating file");
        }

        if(file!=null) {
            //outPutFileUri = Uri.fromFile(file);
            outPutFileUri = FileProvider.getUriForFile(RegistrationDetails.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutFileUri);
            startActivityForResult(intent, 0);
        }
    }

    private File createImageFile() throws IOException{
        String fileName = "Ticket_" + ticketModel.getTicketData().getGivenName() + "_" + getCurrentTimestamp();
        //file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName + ".jpg");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName, ".jpg", storageDir
        );

        return image;
    }

    private String getCurrentTimestamp(){
        DateFormat df = new SimpleDateFormat("ddMMyyyy_hhmmss");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String uri = outPutFileUri.toString();
        Log.e("uri-:", uri);
        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, outPutFileUri.toString(), Toast.LENGTH_LONG).show();

        Picasso.get().load(outPutFileUri).into(mImageView);
        imageUri = outPutFileUri.toString();
    }


}
