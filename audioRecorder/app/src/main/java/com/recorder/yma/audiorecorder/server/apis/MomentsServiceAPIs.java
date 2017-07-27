package com.recorder.yma.audiorecorder.server.apis;

import com.recorder.yma.audiorecorder.data.Moment;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by ramh on 21/06/2017.
 */

public interface MomentsServiceAPIs {
    @GET("moments")
    Single<List<Moment>> getMoments();

    @POST("moments")
    Call<Void> saveNote(@Body Moment moment);


}
