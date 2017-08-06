package com.recorder.yma.audiorecorder.login;

import com.recorder.yma.audiorecorder.IResponseHandler;
import com.recorder.yma.audiorecorder.server.apis.ProvisionAPIs;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Created by ramh on 06/08/2017.
 */

public class LoginPresentorTest {

    @Mock
    LoginContract.View mLoginView;
    @Mock
    ProvisionAPIs mProvisionAPIs;

    @Captor
    private ArgumentCaptor<IResponseHandler> mLoginResponseHandler;

    LoginPresenter mLoginPresenter;

    @Before
    public void setup() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mLoginPresenter = new LoginPresenter(mProvisionAPIs, mLoginView);
    }

    @Test
    public void clickOnLogin_TestLoginSuccess() {
        // When adding a new note
        mLoginPresenter.login("","");

        verify(mProvisionAPIs).login(anyString(),anyString(),mLoginResponseHandler.capture());
        mLoginResponseHandler.getValue().onSuccess();
        InOrder inOrder = Mockito.inOrder(mLoginView);
        inOrder.verify(mLoginView).setLoadingIndicator(true);
        inOrder.verify(mLoginView).setLoadingIndicator(false);
        inOrder.verify(mLoginView).onLoginSucceded();
    }

    @Test
    public void clickOnLogin_TestLoginFailed() {
        // When adding a new note
        mLoginPresenter.login("","");

        verify(mProvisionAPIs).login(anyString(),anyString(),mLoginResponseHandler.capture());
        mLoginResponseHandler.getValue().onFailed("");
        InOrder inOrder = Mockito.inOrder(mLoginView);
        inOrder.verify(mLoginView).setLoadingIndicator(true);
        inOrder.verify(mLoginView).setLoadingIndicator(false);
        inOrder.verify(mLoginView).showLoginFailed();
    }


}
