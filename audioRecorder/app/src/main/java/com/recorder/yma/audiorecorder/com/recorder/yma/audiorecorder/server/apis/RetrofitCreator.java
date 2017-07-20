package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis;

import com.recorder.yma.audiorecorder.AWSManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ramh on 22/06/2017.
 */

public class RetrofitCreator {

    static final String BASE_URL = "https://d2s4oxiwc3.execute-api.us-west-2.amazonaws.com/prod/";

    public static MomentsServiceAPIs getMomentsAPI() {




        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MomentsServiceAPIs.class);
    }

    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                AWSManager.getInstance().getAuthToken());

                        Request newRequest = builder.build();
                        okhttp3.Response response = chain.proceed(newRequest);
                        // Log.d("RetrofitCreator", "response = " + response.body());
                        return response;
                    }
                }).build();
    }
}
