package com.otgindonesia.soregist.Utilities;

import com.otgindonesia.soregist.Data.TicketModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class RestClient {

    private static RestAPI service;

    public static RestAPI getClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder().header("Content-Type", "application/json");
                        Request request = builder.build();

                        return chain.proceed(request);
                    }
                }).readTimeout(30, TimeUnit.SECONDS).build();

        if (service == null)

        {
            Retrofit client = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://dev.successolympics.otgindonesia.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = client.create(RestAPI.class);
        }
        return service;
    }

    public interface RestAPI {

        @POST("validateTicket")
        Call<TicketModel> getTicket(@Query("qrcode") String qrcode);

    }
}
