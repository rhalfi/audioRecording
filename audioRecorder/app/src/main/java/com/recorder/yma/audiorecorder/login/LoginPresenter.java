package com.recorder.yma.audiorecorder.login;

import android.util.Log;

import com.recorder.yma.audiorecorder.IResponseHandler;
import com.recorder.yma.audiorecorder.MyApp;
import com.recorder.yma.audiorecorder.dagger.AppModule;
import com.recorder.yma.audiorecorder.dagger.DaggerNetComponent;
import com.recorder.yma.audiorecorder.dagger.NetModule;
import com.recorder.yma.audiorecorder.server.apis.ProvisionAPIs;

import javax.inject.Inject;

/**
 * Created by ramh on 27/07/2017.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();
    LoginContract.View mLoginView;
    ProvisionAPIs mProvisionAPIs;

    @Inject
    LoginPresenter(ProvisionAPIs provisionAPIs, LoginContract.View loginView) {
        mProvisionAPIs = provisionAPIs;
        mLoginView = loginView;
    }

    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mLoginView.setPresenter(this);
    }

    @Override
    public void login(String user, String psw) {
        mProvisionAPIs.login(MyApp.getApp(), user, psw
                , new IResponseHandler() {
                    @Override
                    public void onFailed(String error) {
                        Log.e(TAG, "onFailed:" + error);
                        mLoginView.showLoginFailed();
                    }

                    @Override
                    public void onSuccess() {
                        MyApp.getApp().setmNetComponent(DaggerNetComponent.builder()
                                // list of modules that are part of this component need to be created here too
                                .appModule(new AppModule(MyApp.getApp())) // This also corresponds to the name of your module: %component_name%Module
                                .netModule(new NetModule("https://d2s4oxiwc3.execute-api.us-west-2.amazonaws.com/prod/", mProvisionAPIs.geTToken()))
                                .build());
                        mLoginView.onLoginSucceded();
                    }
                });
    }

    @Override
    public void start() {

    }
}
