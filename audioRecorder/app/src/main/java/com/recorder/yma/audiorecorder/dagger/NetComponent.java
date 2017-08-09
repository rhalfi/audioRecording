package com.recorder.yma.audiorecorder.dagger;

import android.app.Application;
import android.content.SharedPreferences;

import com.recorder.yma.audiorecorder.RecordingsActivity;
import com.recorder.yma.audiorecorder.server.apis.MomentsServiceAPIs;
import com.recorder.yma.audiorecorder.server.apis.MyDriveEventService;
import com.recorder.yma.audiorecorder.server.apis.ProvisionAPIs;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ramh on 25/07/2017.
 */

@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(RecordingsActivity activity);
    void inject(MyDriveEventService service);

    ProvisionAPIs getProvisionAPIs();
    Application getApplication();
    MomentsServiceAPIs getMomentsServiceAPIs();
    SharedPreferences getSharedPreferences();
    // void inject(MyService service);
}
