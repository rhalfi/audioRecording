package com.recorder.yma.audiorecorder.login;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramh on 27/07/2017.
 */
@Module
public class LoginPresenterModule {
    private final LoginContract.View mView;

    public LoginPresenterModule(LoginContract.View view) {
        mView = view;
    }

    @Provides
    LoginContract.View provideLoginContractView() {
        return mView;
    }
}
