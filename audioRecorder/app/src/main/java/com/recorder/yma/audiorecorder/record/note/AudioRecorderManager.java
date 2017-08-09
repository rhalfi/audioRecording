package com.recorder.yma.audiorecorder.record.note;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.recorder.yma.audiorecorder.data.Moment;
import com.recorder.yma.audiorecorder.server.apis.FileStorageAPIs;
import com.recorder.yma.audiorecorder.server.apis.MomentsServiceAPIs;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ramh on 08/08/2017.
 */
public class AudioRecorderManager {

    private final static String TAG = AudioRecorderManager.class.getSimpleName();
    private com.recorder.yma.audiorecorder.AudioRecorder mRecorder;
    private String mFilePath;
    private Application mContext;
    private FileStorageAPIs mFileStorageAPIs;
    private MomentsServiceAPIs mMomentsServiceAPIs;


    public AudioRecorderManager(Application application, FileStorageAPIs fileStorageAPIs, MomentsServiceAPIs momentsServiceAPIs){
        mContext = application;
        mFileStorageAPIs = fileStorageAPIs;
        mMomentsServiceAPIs = momentsServiceAPIs;

    }

    public void startRec() {
        File file = new File(mContext.getFilesDir() + "/rec", String.valueOf(System.currentTimeMillis()) + ".amr");
        // make sure the directory exist
        File folder = file.getParentFile();
        if (!folder.exists())
        {
            folder.mkdirs();
        }

        if(file == null){
            Log.e(TAG,"startRecording: " + " filed to create file for recording");
            return;
        }
        mFilePath = file.getAbsolutePath();
        Log.d(TAG,"RichMessageHelper:startRecording: " + mFilePath);
        mRecorder = com.recorder.yma.audiorecorder.AudioRecorder.build(mContext, file.getAbsolutePath());
        mRecorder.start(new com.recorder.yma.audiorecorder.AudioRecorder.OnStartListener()
        {
            @Override
            public void onStarted()
            {
                Log.i(TAG, " startRecording");
                //mRecState = RECORDING;
            }

            @Override
            public void onException(@NonNull Exception e)
            {
                Log.w(TAG, " startRecording - Exception:" + e);
                e.printStackTrace();
            }
        });
    }
    public void stopRec() {
        Log.d(TAG, " stopRec");
        mRecorder.stop();
      //  mRecState = STOPPED;
    }

    public void uploadRec()
    {//mFileStorageAPIs.uploadFile(null);if(true )return;
        // mFilePath = "/data/user/0/com.recorder.yma.audiorecorder/files/rec/1500466614564.amr";
        String fileKey = mFileStorageAPIs.uploadFile( new File(mFilePath));

        if(fileKey == null){
            Log.d(TAG, "note saving since upload is async and we dont know link ");
            return;
        }
        //   Log.d(TAG, "uploadRec file key = " + fileKey);
        Call<Void> saveNote =  mMomentsServiceAPIs.saveNote(new Moment(fileKey,"test note mobile"));
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

    }

    public void initFileStorageConnection() {
        mFileStorageAPIs.init();
    }


    public void destroyFileStorageConnection() {
        mFileStorageAPIs.discconect();
    }
}
