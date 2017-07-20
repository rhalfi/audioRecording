package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ramh on 22/06/2017.
 */

public class Controller implements Callback<List<Moment>> {

    static final String BASE_URL = "https://d2s4oxiwc3.execute-api.us-west-2.amazonaws.com/prod/";

    public void start() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MomentsServiceAPIs momentsApi = retrofit.create(MomentsServiceAPIs.class);

       // Call<List<Moment>> call = momentsApi.getMoments();
        //call.enqueue(this);

    }

    @Override
    public void onResponse(Call<List<Moment>> call, Response<List<Moment>> response) {
        if(response.isSuccessful()) {
            List<Moment> changesList = response.body();
            for(Moment moment: changesList){
                Log.d("", "onResponse:" + moment.content);
            }

        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Moment>> call, Throwable t) {
        t.printStackTrace();
    }
}