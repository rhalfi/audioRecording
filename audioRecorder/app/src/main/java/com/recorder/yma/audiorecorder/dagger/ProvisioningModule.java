package com.recorder.yma.audiorecorder.dagger;

import com.recorder.yma.audiorecorder.server.apis.ProvisionAPIs;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramh on 25/07/2017.
 */
@Module
public class ProvisioningModule {

    @Provides
    ProvisionAPIs provideProvisionAPIs() {

        return new ProvisionAPIs();
    }
}
