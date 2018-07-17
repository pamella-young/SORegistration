package com.otgindonesia.soregist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    EditText et_registeredName, et_uplineName, et_name;
    Uri outPutFileUri;
    Button btn_takePhoto, btn_cancel, btn_register;
    TicketModel ticketModel;
    String imageUri;
    RestClient.RestAPI client;
    File file;
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

        if(!isValid(ticketModel)){
            String errorMsg = ticketModel.getMessage();
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            finish();
        }

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
            Picasso.get().load("https://dev.successolympics.otgindonesia.com/uploads/basic/1530176963-Ticket_TestDummy_28062018_041601.jpg").into(mImageView);

            et_name.setText(ticketModel.getTicketData().getName());
            et_name.setEnabled(false);
        }

    }

    private boolean isValid(TicketModel ticketModel){
        if(ticketModel.getStatus()!= "ok"){
            return false;
        }else{
            return true;
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
        btn_takePhoto.setVisibility(Button.INVISIBLE);
        btn_register.setVisibility(Button.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view == btn_takePhoto){
            takePictureIntent();
        }else if(view == btn_cancel){
            finish();
        }else if(view == btn_register){
            if(imageUri!=""){
                File compressedfile = file;
                try{
                    compressedfile = new Compressor(this).compressToFile(file);
                }catch (IOException e){
                    Log.v(TAG, "failed to compress image");
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
                        Toast.makeText(RegistrationDetails.this, newTicketStatus.getStatus(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegistrationDetails.this, RegistrationSuccess.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<TicketModel> call, Throwable t) {

                    }
                });
            }else{
                Toast.makeText(this, "Photo's not taken", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePictureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "Ticket_" + ticketModel.getTicketData().getGivenName() + "_" + getCurrentTimestamp();
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName + ".jpg");
        //
        outPutFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutFileUri);
        startActivityForResult(intent, 0);
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
        Toast.makeText(this, outPutFileUri.toString(), Toast.LENGTH_LONG).show();

        Picasso.get().load(outPutFileUri).into(mImageView);
        imageUri = outPutFileUri.toString();
    }


}
