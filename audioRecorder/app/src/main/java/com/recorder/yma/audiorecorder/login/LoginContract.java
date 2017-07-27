package com.recorder.yma.audiorecorder.login;

import com.recorder.yma.audiorecorder.BasePresenter;
import com.recorder.yma.audiorecorder.BaseView;

/**
 * Created by ramh on 27/07/2017.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showLoginFailed();
        void onLoginSucceded();

    }

    interface Presenter extends BasePresenter {

        void login(String user ,String psw);
    }
}
