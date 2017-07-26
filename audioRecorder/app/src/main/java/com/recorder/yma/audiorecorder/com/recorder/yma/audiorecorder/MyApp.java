package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder;

import android.app.Application;

import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.NetComponent;

/**
 * Created by ramh on 25/07/2017.
 */

public class MyApp extends Application {
    private static MyApp app;
    private NetComponent mNetComponent;
    public static MyApp getApp() {
        return app;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        // Dagger%COMPONENT_NAME%



        // If a Dagger 2 component does not have any constructor arguments for any of its modules,
        // then we can use .create() as a shortcut instead:
        //  mNetComponent = com.codepath.dagger.components.DaggerNetComponent.create();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    public void setmNetComponent(NetComponent netComponent) {
        mNetComponent = netComponent;
    }
}