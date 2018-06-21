package com.otgindonesia.soregist.Utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String TICKET_BASE_URL = "https://dev.successolympics.otgindonesia.com/";

    private static final String API_PATH = "api";
    private static final String VALIDATE_TICKET_PATH = "validateTicket";
    private static final String SUBMIT_TICKET_PATH = "submitTiket";

    private static final String QRCODE_PARAM = "qrcode";
    private static final String NAME_PARAM = "name";
    private static final String PHOTO_PARAM = "photo";

    public static URL buildValidateTicketUrl(String qrcode){
        Uri builtUri = Uri.parse(TICKET_BASE_URL).buildUpon()
                .appendPath(API_PATH)
                .appendPath(VALIDATE_TICKET_PATH)
                .appendQueryParameter(QRCODE_PARAM, qrcode)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildSumbitTicketUrl(String qrcode, String name, String photo){
        Uri builtUri = Uri.parse(TICKET_BASE_URL).buildUpon()
                .appendPath(API_PATH)
                .appendPath(SUBMIT_TICKET_PATH)
                .appendQueryParameter(QRCODE_PARAM, qrcode)
                .appendQueryParameter(NAME_PARAM, name)
                .appendQueryParameter(PHOTO_PARAM, photo)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

}
