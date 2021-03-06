package com.otgindonesia.soregist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.Result;
import com.otgindonesia.soregist.Data.TicketModel;
import com.otgindonesia.soregist.Utilities.RestClient;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private final String TAG = this.getClass().getSimpleName();

    private ZXingScannerView mScannerView;
    RestClient.RestAPI client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        client = RestClient.getClient();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {


        Log.v(TAG, "QR Code:" + result.getText());

        final Call<TicketModel> ticket = client.getTicket(result.getText());

        final String qrcode = result.getText().toString();

        ticket.enqueue(new Callback<TicketModel>() {
            @Override
            public void onResponse(Call<TicketModel> call, Response<TicketModel> response) {

                if(response.code() == 400){
                    Toast.makeText(ScanActivity.this, getString(R.string.not_found), Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Gson gson = new Gson();

                    Intent intent = new Intent(ScanActivity.this, RegistrationDetails.class);
                    intent.putExtra("ticket", gson.toJson(response.body()));
                    intent.putExtra("qrcode", qrcode);


                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<TicketModel> call, Throwable t) {
                Toast.makeText(ScanActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                //recreate();
            }
        });

    }
}
