package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger;

import android.app.Application;

import com.amazonaws.regions.Regions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramh on 24/07/2017.
 */
@Module
public class AppModule {

    public static final Regions MY_REGION = Regions.US_WEST_2;

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }
}
