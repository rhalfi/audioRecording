package com.recorder.yma.audiorecorder.login;

import com.recorder.yma.audiorecorder.IResponseHandler;
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
        mLoginView.setLoadingIndicator(true);
        mProvisionAPIs.login( user, psw
                , new IResponseHandler() {
                    @Override
                    public void onFailed(String error) {
                        mLoginView.setLoadingIndicator(false);
                        mLoginView.showLoginFailed();
                    }

                    @Override
                    public void onSuccess() {

                        mLoginView.setLoadingIndicator(false);
                        mLoginView.onLoginSucceded();
                    }
                });
    }

    @Override
    public void start() {

    }

    @Override
    public boolean isNeedLogin() {
        return mProvisionAPIs.geTToken() == null;
    }
}
