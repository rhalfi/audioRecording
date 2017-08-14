package com.recorder.yma.audiorecorder.server.apis;

import android.content.SharedPreferences;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.recorder.yma.audiorecorder.IResponseHandler;
import com.recorder.yma.audiorecorder.MyApp;

import javax.inject.Inject;

import static com.recorder.yma.audiorecorder.dagger.AppModule.MY_REGION;
import static com.recorder.yma.audiorecorder.login.LoginActivity.TAG;

/**
 * Created by ramh on 25/07/2017.
 */

public class ProvisionAPIs {

    private static String LOGIN_TOKEN = "LOGIN_TOKEN";
    SharedPreferences mSharedPreferences;
    @Inject
    public ProvisionAPIs(SharedPreferences preferences){
        mSharedPreferences = preferences;
    }

    private CognitoUserSession mCognitoUserSession;

    public String mToken;

    public String geTToken(){

        return  mSharedPreferences.getString(LOGIN_TOKEN,null);
    }
    public void login( final String user, final  String psw, IResponseHandler handler){

        ClientConfiguration clientConfiguration = new ClientConfiguration();
// Create a CognitoUserPool object to refer to your user pool
        AmazonCognitoIdentityProviderClient identityProviderClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), new ClientConfiguration());
        identityProviderClient.setRegion(Region.getRegion(MY_REGION));
        CognitoUserPool userPool = new CognitoUserPool(MyApp.getApp(), "us-west-2_AIpt9A4xB"
                , "5m67etac0v89nk2na84m52gujd", null, identityProviderClient);
        CognitoUser cognicoUser = userPool.getUser();



        cognicoUser.getSessionInBackground(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.d(TAG,"onSuccess:");
                mCognitoUserSession = userSession;
                mSharedPreferences.edit().putString(LOGIN_TOKEN,mCognitoUserSession.getIdToken().getJWTToken()).commit();
                handler.onSuccess();
                //        initTransferUtility(context);

            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {

                //AuthenticationDetails authenticationDetails = new AuthenticationDetails(,null);
                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG,"onFailure: " + exception.getMessage());
            }
        });
    }
}