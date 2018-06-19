package com.otgindonesia.soregist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnManual, btnScan;
    private Class mClass;
    private EditText et_code;
    private static final int ZXING_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                //| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnManual = findViewById(R.id.btn_manualInput);
        btnManual.setOnClickListener(this);

        btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(this);

        et_code = findViewById(R.id.et_code);

    }

    public void onClick(View view){
        if(view == btnManual){
            if(!validateManualInput()){
                Toast.makeText(this, "Code can't be empty", Toast.LENGTH_SHORT).show();
            }else {
                Context context = MainActivity.this;
                Class destination = RegistrationDetails.class;
                Intent intent = new Intent(context, destination);

                startActivity(intent);
            }
        }
        else if(view == btnScan){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                mClass = ScanActivity.class;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
            }else{
                Intent intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
            }
        }
    }

    private boolean validateManualInput(){
        String manualInput = et_code.getText().toString();

        boolean check = true;

        if(manualInput.isEmpty()){
            check = false;
        }else if(manualInput.length() < 5 || manualInput.length() > 15){
            check = false;
        }

        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case ZXING_CAMERA_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(mClass != null){
                        Intent intent = new Intent(this, mClass);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(this, R.string.camera_permission, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}

