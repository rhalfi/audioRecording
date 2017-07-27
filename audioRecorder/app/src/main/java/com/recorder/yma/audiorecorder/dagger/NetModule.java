package com.recorder.yma.audiorecorder.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recorder.yma.audiorecorder.server.apis.FileStorageAPIS;
import com.recorder.yma.audiorecorder.server.apis.MomentsServiceAPIs;
import com.recorder.yma.audiorecorder.server.apis.ProvisionAPIs;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ramh on 25/07/2017.
 */

@Module
public class NetModule {

    String mBaseUrl;
    String mToken;

    // Constructor needs one parameter to instantiate.
    public NetModule(String baseUrl, String token) {
        this.mBaseUrl = baseUrl;
        mToken = token;
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }


    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggin = new HttpLoggingInterceptor();
        return loggin;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logging, Cache cache) {
        OkHttpClient client =  new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                mToken);

                        Request newRequest = builder.build();
                        okhttp3.Response response = chain.proceed(newRequest);
                        // Log.d("RetrofitCreator", "response = " + response.body());
                        return response;
                    }
                }).build();
        return client;
    }




    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {

        return  new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    MomentsServiceAPIs provideMomentsServiceAPIs(Retrofit retrofit) {

        return retrofit.create(MomentsServiceAPIs.class);
    }

    @Provides
    @Singleton
    ProvisionAPIs provideProvisionAPIs() {

        return new ProvisionAPIs();
    }

    @Provides
    @Singleton
    FileStorageAPIS provideFileStorageAPIS(Application context) {

        return new FileStorageAPIS(mToken, context);
    }
}