package com.recorder.yma.audiorecorder;

import android.app.Application;

import com.recorder.yma.audiorecorder.dagger.AppModule;
import com.recorder.yma.audiorecorder.dagger.DaggerNetComponent;
import com.recorder.yma.audiorecorder.dagger.NetComponent;
import com.recorder.yma.audiorecorder.dagger.NetModule;

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

        MyApp.getApp().setmNetComponent(DaggerNetComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(MyApp.getApp())) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule("https://d2s4oxiwc3.execute-api.us-west-2.amazonaws.com/prod/"))
                .build());

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