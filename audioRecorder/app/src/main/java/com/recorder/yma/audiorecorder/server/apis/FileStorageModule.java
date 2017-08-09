package com.recorder.yma.audiorecorder.server.apis;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.recorder.yma.audiorecorder.record.note.AudioRecorderManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramh on 08/08/2017.
 */
@Module
public class FileStorageModule {

    Activity mActivity;
    public FileStorageModule(Activity activity){
        mActivity = activity;
    }
    @Provides
    Activity provideActivity(){
        return mActivity;
    }


    @Provides
    FileStorageAPIs provideFileStorageAPIs(Application context, Activity activity, ProvisionAPIs provisionAPIs, SharedPreferences sharedPreferences) {

       // return new AmazonFileStorageAPIS(provisionAPIs.geTToken(), context,activity);
        return new GoogleDriveFileStorageAPIs(context,activity,sharedPreferences);
    }

    @Provides
    AudioRecorderManager provideAudioRecorderManager(Application context, FileStorageAPIs fileStorageAPIS, MomentsServiceAPIs momentsServiceAPIs) {

        return new AudioRecorderManager(context,fileStorageAPIS,momentsServiceAPIs);
    }
}
