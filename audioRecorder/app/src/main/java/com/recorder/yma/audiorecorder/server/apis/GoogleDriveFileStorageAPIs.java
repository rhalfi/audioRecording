package com.recorder.yma.audiorecorder.server.apis;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.common.io.Files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ramh on 08/08/2017.
 */

public class GoogleDriveFileStorageAPIs implements FileStorageAPIs, ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = GoogleDriveFileStorageAPIs.class.getSimpleName();
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_OPENER = 2;
    public static final String GOOGLE_DRIVE_FOLDER_ID = "GOOGLE_DRIVE_FOLDER_ID";
    public static final String GOOGLE_DRIVE_DOWNLOAD_LINK= "https://drive.google.com/open?id=";
    private static final String GOOGLE_DRIVE_FOLDER_NAME= "recordings";

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private Activity mActivity;
    private SharedPreferences mSharedPreferences;
    private File mFileToUpload;
    private DriveFolder mDriveFolder;
    private DriveId mFolderDriveId;

    boolean fileOperation;

    public GoogleDriveFileStorageAPIs(Application context, Activity activity, SharedPreferences sharedPreferences) {
        mContext = context;
        mActivity = activity;
        mSharedPreferences = sharedPreferences;
    }

    private String getFolderID() {
        return mSharedPreferences.getString(GOOGLE_DRIVE_FOLDER_ID, null);
    }

    @Override
    public String uploadFile(File file) {

        mFileToUpload = file;
        fileOperation = true;

        if (getFolderID() == null) {
            createFolder();
        } else {
            uploadFile();
        }
        // create new contents resource

        return null;
    }

    /***
     * upload file and if needed get the folder
     */
    private void uploadFile() {
        if(mDriveFolder == null) {
            Drive.DriveApi.fetchDriveId(getGoogleApiClient(), getFolderID())
                    .setResultCallback(driveIdResult -> getFileIDCallback(driveIdResult));
            return;
        }
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsResult -> {
                    if (driveContentsResult.getStatus().isSuccess()) {
                        if (fileOperation == true) {

                            CreateFileOnGoogleDrive(driveContentsResult, mFileToUpload, mDriveFolder);

                        }
                    }
                });
    }

    /***
     * handle the get folder ID
     * @param result
     */
    private void getFileIDCallback(DriveIdResult result) {
        if (!result.getStatus().isSuccess()) {
            Log.d(TAG,"getFileIDCallback Cannot find DriveId. Are you authorized to view this file?" + getFolderID());
            return;
        }
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsResult -> handleDriveIDResult(driveContentsResult,result.getDriveId()));
    }

    /***
     * handle get the folder
     * @param result
     * @param driveID
     */
    private void handleDriveIDResult(DriveContentsResult result, DriveId driveID) {
        if (!result.getStatus().isSuccess()) {
            Log.d(TAG,"Cannot find DriveId. Are you authorized to view this file?");
            return;
        }
        mFolderDriveId = driveID;
        mDriveFolder = mFolderDriveId.asDriveFolder();

        CreateFileOnGoogleDrive(result, mFileToUpload, mDriveFolder);


    }





    private GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    /**
     * create the folder on google drive
     */
    private void createFolder() {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(GOOGLE_DRIVE_FOLDER_NAME).build();
        Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(
                getGoogleApiClient(), changeSet).setResultCallback(result -> folderCreateResult(result));
    }

    /***
     * handle folder create result
     * @param result
     */
    private void folderCreateResult(DriveFolderResult result) {
        if (!result.getStatus().isSuccess()) {
            Log.d(TAG, "Error while trying to create the folder");
            return;
        }
        Log.d(TAG, "Created a folder: " + result.getDriveFolder().getDriveId());
        mSharedPreferences.edit().putString(GOOGLE_DRIVE_FOLDER_ID, result.getDriveFolder().getDriveId().getResourceId()).commit();
        mDriveFolder = result.getDriveFolder();
        uploadFile();

        //create dummy file just to get drive resource ID
        createDummyFile(mDriveFolder);
    }

    /***
     * create dummy file on google drive so we can get callback and get the folder ID
     * @param folder
     */
    private void createDummyFile(DriveFolder folder){
        MetadataChangeSet meta = new MetadataChangeSet.Builder()
                .setTitle("EmptyFile.txt").setMimeType("text/plain")
                .build();
        folder.createFile(getGoogleApiClient(), meta, null,
                        new ExecutionOptions.Builder()
                                .setNotifyOnCompletion(true)
                                .build()
                )
                .setResultCallback(driveFileResult -> {
                    if (driveFileResult.getStatus().isSuccess()) {
                        DriveId driveId = driveFileResult.getDriveFile().getDriveId();
                        Log.d(TAG, "Created a empty file: " + driveId);
                        driveFileResult.getDriveFile().addChangeSubscription(getGoogleApiClient());
                    }
                });



    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Create a file in root folder using MetadataChangeSet object.
     *
     * @param result
     */
    private void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result, File file, DriveFolder folder) {

        if (!result.getStatus().isSuccess()) {
            Log.i(TAG, "Failed to create new contents.");
            return;
        }
        OutputStream outputStream = result.getDriveContents().getOutputStream();
        // Write the bitmap data from it.
        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        try {
            outputStream.write(Files.toByteArray(file));
            outputStream = null;
            MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                    .setMimeType(getMimeType(file.toURI().toURL().toString())).setTitle(file.getName())
                    .build();
            Log.d(TAG, "Creating new file on Drive (" + file.getName() + ")");

            folder.createFile(mGoogleApiClient,
                    metadataChangeSet, result.getDriveContents())
                    .setResultCallback(driveFileResult -> {
                        Log.d(TAG, "file on Drive (" + file.getName() + "),id=" + driveFileResult.getDriveFile().getDriveId() );
                        driveFileResult.getDriveFile().addChangeSubscription(getGoogleApiClient());
                    });

        } catch (IOException e1) {
            Log.i(TAG, "Unable to write file contents.");
            return;
        }


    }


    @Override
    public void init() {
        Log.d(TAG, "init.");
        if (mGoogleApiClient == null) {
            Log.d(TAG, "creating credentials.");
            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        Log.d(TAG, "creating credentials connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void discconect() {
        Log.d(TAG, "discconect");
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(mActivity, result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            result.startResolutionForResult(mActivity, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }
}
