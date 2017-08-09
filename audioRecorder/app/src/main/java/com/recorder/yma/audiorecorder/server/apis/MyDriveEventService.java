package com.recorder.yma.audiorecorder.server.apis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;
import com.recorder.yma.audiorecorder.MyApp;
import com.recorder.yma.audiorecorder.data.Moment;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.recorder.yma.audiorecorder.server.apis.GoogleDriveFileStorageAPIs.GOOGLE_DRIVE_DOWNLOAD_LINK;

/**
 * Created by ramh on 09/08/2017.
 */

public class MyDriveEventService extends DriveEventService {

    private static final String TAG = MyDriveEventService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.getApp().getNetComponent().inject(this);
    }

    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    MomentsServiceAPIs mMomentsServiceAPIs;

    @Override
    public void onChange(ChangeEvent event) {
        Log.d(TAG, "Action completed with status: " + event.getType() + "res=" + event.getDriveId().getResourceId());
        Call<Void> saveNote =  mMomentsServiceAPIs.saveNote(new Moment(GOOGLE_DRIVE_DOWNLOAD_LINK + event.getDriveId().getResourceId()
                ,(new java.util.Date()).toString()));
        saveNote.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "note saved " + response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "note saved onFailure " + t.getMessage());
            }
        });
        super.onChange(event);
    }

    @Override
    public void onCompletion(CompletionEvent event) {
        Log.d(TAG, "Action completed with status: " + event.getStatus() + "res=" + event.getDriveId().getResourceId());

        // handle completion event here.
        if (event.getStatus() == CompletionEvent.STATUS_SUCCESS) {
            String folderID = sharedPreferences.getString(GoogleDriveFileStorageAPIs.GOOGLE_DRIVE_FOLDER_ID, null);
            connectAndgetParentID(event.getDriveId());

            event.dismiss();


        }


    }

    private DriveId mDriveID;
    GoogleApiClient gac;

    private void connectAndgetParentID(DriveId dId) {
        mDriveID = dId;

        gac = new GoogleApiClient.Builder(MyApp.getApp())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(gacCallbacks)
                .build();

        gac.connect();
    }

    private GoogleApiClient.ConnectionCallbacks gacCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "onConnected");
            getParentID(mDriveID);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private void getParentID(DriveId dId) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                MetadataBuffer mdb = null;
                DriveApi.MetadataBufferResult mbRslt = dId.asDriveResource().listParents(gac).await();
                if (mbRslt.getStatus().isSuccess()) try {
                    mdb = mbRslt.getMetadataBuffer();
                    String folderResourceId;
                    if (mdb.getCount() > 0) {
                        folderResourceId = mdb.get(0).getDriveId().getResourceId();
                        sharedPreferences.edit().putString(GoogleDriveFileStorageAPIs.GOOGLE_DRIVE_FOLDER_ID, folderResourceId).commit();
                        Log.d(TAG, "onCompletion with resource id: " + folderResourceId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mdb != null) mdb.close();
                }
            }
        });
        t.start();

    }

}
