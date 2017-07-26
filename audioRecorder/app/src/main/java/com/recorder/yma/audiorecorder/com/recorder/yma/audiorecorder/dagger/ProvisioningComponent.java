package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger;

import com.recorder.yma.audiorecorder.AWSFileModule;
import com.recorder.yma.audiorecorder.RecordingsActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ramh on 25/07/2017.
 */
@Singleton
@Component(modules={AWSFileModule.class })
public interface ProvisioningComponent {

        void inject(RecordingsActivity activity);

}
