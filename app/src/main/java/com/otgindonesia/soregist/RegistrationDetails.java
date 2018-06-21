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
import com.otgindonesia.soregist.Data.TicketModel;
import com.squareup.picasso.Picasso;

import java.io.File;

public class RegistrationDetails extends AppCompatActivity implements View.OnClickListener {


    private final String TAG = this.getClass().getSimpleName();
    ImageView mImageView;
    EditText et_registeredName, et_uplineName;
    Uri outPutFileUri;
    Button btn_takePhoto, btn_cancel, btn_register;
    TicketModel ticketModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        Gson gson = new Gson();
        String strTicket = getIntent().getStringExtra("ticket");
        ticketModel = gson.fromJson(strTicket, TicketModel.class);

        mImageView = findViewById(R.id.iv_profile_picture);

        et_registeredName = findViewById(R.id.et_registered_name);
        et_uplineName = findViewById(R.id.et_upline_name);

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
    }

    @Override
    public void onClick(View view) {
        if(view == btn_takePhoto){
            takePictureIntent();
        }else if(view == btn_cancel){

        }else if(view == btn_register){

        }
    }

    private void takePictureIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "Photo.jpg");
        outPutFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutFileUri);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String uri = outPutFileUri.toString();
        Log.e("uri-:", uri);
        Toast.makeText(this, outPutFileUri.toString(), Toast.LENGTH_LONG).show();

        Picasso.get().load(outPutFileUri).into(mImageView);

//        try {
////            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutFileUri);
////            Drawable d = new BitmapDrawable(getResources(), bitmap);
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }
}
