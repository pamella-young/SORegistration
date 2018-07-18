package com.otgindonesia.soregist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegistrationSuccess extends AppCompatActivity implements View.OnClickListener{

    private Button btn_start_again;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);

        btn_start_again = findViewById(R.id.btn_start_again);
        btn_start_again.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == btn_start_again){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
