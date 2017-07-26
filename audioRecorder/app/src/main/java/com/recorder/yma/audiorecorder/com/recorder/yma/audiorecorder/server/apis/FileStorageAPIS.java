package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.recorder.yma.audiorecorder.MainActivity.TAG;
import static com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.AppModule.MY_REGION;

/**
 * Created by ramh on 25/07/2017.
 */

public class FileStorageAPIS {
    private static final String BUCKET =  "memories.app.content";
    private static final String S3_URL =  "https://s3-us-west-2.amazonaws.com";
    private TransferUtility mTransferUtility;
    private String mIDentityID;


    public FileStorageAPIS(String token, Application context) {
        initTransferUtility(context, token);
    }

    private void initTransferUtility(Context context, String token) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        context.getApplicationContext(),
                        "us-west-2:78b07b86-89fa-4889-aa59-7af9e9e15f89", // Identity Pool ID
                        MY_REGION // Region
                );
                Map<String, String> logins = new HashMap<String, String>();
                logins.put("cognito-idp.us-west-2.amazonaws.com/us-west-2_AIpt9A4xB", token);
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

    public String uploadFile(Context context,File file) {
        String key = mIDentityID + UUID.randomUUID().toString() + file.getName();
        mTransferUtility.upload(BUCKET ,key, file, CannedAccessControlList.PublicRead).setTransferListener(new TransferListener() {
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
}
