package com.recorder.yma.audiorecorder;

import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ramh on 21/06/2017.
 */

public class AWSManager {
    public static String TAG = "AWSManager";
    private static final String BUCKET =  "memories.app.content";
    private static final String S3_URL =  "https://s3-us-west-2.amazonaws.com";
    private static final Regions MY_REGION = Regions.US_WEST_2;
    private static AWSManager mInstance;

    public static  synchronized  AWSManager getInstance()
    {
        if(mInstance == null) {
            mInstance = new AWSManager();
        }
        return  mInstance;
    }

    IResponseHandler mAuthenticationHandler;
    private CognitoUserSession mCognitoUserSession;
    private TransferUtility mTransferUtility;
    private String mIDentityID;

    public String getAuthToken() {
        if(mCognitoUserSession == null) {
            Log.e(TAG, "getAuthToken:no session created");
            return null;
        }
        return mCognitoUserSession.getIdToken().getJWTToken();
    }

    private void initTransferUtility(Context context) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        context.getApplicationContext(),
                        "us-west-2:78b07b86-89fa-4889-aa59-7af9e9e15f89", // Identity Pool ID
                        MY_REGION // Region
                );
                Map<String, String> logins = new HashMap<String, String>();
                logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_AIpt9A4xB", getAuthToken());
                credentialsProvider.setLogins(logins);
                credentialsProvider.refresh();
                // credentialsProvider.getIdentityId();
                AmazonS3 s3 = new AmazonS3Client(credentialsProvider );
                s3.setRegion(Region.getRegion(MY_REGION));
                mIDentityID = credentialsProvider.getIdentityId();


                mTransferUtility = new TransferUtility(s3, context.getApplicationContext());


            }
        });
        t.start();

    }

    private synchronized TransferUtility getTransferUtility(Context context) {
        if(mTransferUtility == null){

        }
        return mTransferUtility;

    }
    //https://s3-us-west-2.amazonaws.com/memories.app.content/us-west-2%3A75f473af-fcb4-4403-94b5-d5108e9ad0aa-1500543160222-us-west-2-75f473af-fcb4-4403-94b5-d5108e9ad0aa16778c62-0d41-45ff-a18b-aaafb57272e5.amr
    public String uploadFile(Context context,File file) {
        String key = mIDentityID + UUID.randomUUID().toString() + file.getName();
        getTransferUtility(context).upload(BUCKET ,key, file, CannedAccessControlList.PublicRead).setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG,"onStateChanged state="  +state);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, "onProgressChanged bytesCurrent=" + +bytesCurrent + ", bytesTotal=" + bytesTotal);

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d(TAG,"onError ex="  +ex);
            }
        });
        return S3_URL + "/"+ BUCKET + "/"  + key;
    }

    public void login(final Context context, final String user,final  String psw, IResponseHandler handler){
        mAuthenticationHandler = handler;
        ClientConfiguration clientConfiguration = new ClientConfiguration();
// Create a CognitoUserPool object to refer to your user pool
        AmazonCognitoIdentityProviderClient identityProviderClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), new ClientConfiguration());
        identityProviderClient.setRegion(Region.getRegion(MY_REGION));
        CognitoUserPool userPool = new CognitoUserPool(context, "us-west-2_AIpt9A4xB"
                , "5m67etac0v89nk2na84m52gujd", null, identityProviderClient);
        CognitoUser cognicoUser = userPool.getUser();



        cognicoUser.getSessionInBackground(new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Log.d(TAG,"onSuccess:");
                mCognitoUserSession = userSession;
                mAuthenticationHandler.onSuccess();
                initTransferUtility(context);

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

    public void getRecordings() {

    }


}



