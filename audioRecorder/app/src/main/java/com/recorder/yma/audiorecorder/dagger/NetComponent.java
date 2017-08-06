package com.recorder.yma.audiorecorder.dagger;

import com.recorder.yma.audiorecorder.RecordNoteActivity;
import com.recorder.yma.audiorecorder.RecordingsActivity;
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
    void inject(RecordNoteActivity activity);

    ProvisionAPIs getProvisionAPIs();
    // void inject(MyService service);
}
