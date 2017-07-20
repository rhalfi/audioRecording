package com.recorder.yma.audiorecorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis.Moment;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis.RetrofitCreator;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.recorder.yma.audiorecorder.RecordNote.EREC_STATE.RECORDING;
import static com.recorder.yma.audiorecorder.RecordNote.EREC_STATE.STOPPED;

public class RecordNote extends AppCompatActivity {

    private static final String TAG = "RecordNote";
    private AudioRecorder mRecorder;
    private String mFilePath;
    Button mRecordButton;
    Button mUploadButton;
    enum EREC_STATE {STOPPED,RECORDING};
    EREC_STATE mRecState = STOPPED;
    MediaPlayer mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_note);
        initUI();
    }

    private void initUI() {
        mRecordButton = (Button)findViewById(R.id.recordButton);
        mUploadButton = (Button)findViewById(R.id.uploadButton);
        mRecordButton.setOnClickListener(view -> handleRecButton());
        mUploadButton.setOnClickListener(view -> uploadRec());
    }

    private void uploadRec()
    {
       // mFilePath = "/data/user/0/com.recorder.yma.audiorecorder/files/rec/1500466614564.amr";
        String fileKey = AWSManager.getInstance().uploadFile(this, new File(mFilePath));
     //   Log.d(TAG, "uploadRec file key = " + fileKey);
        Call<Void> saveNote =  RetrofitCreator.getMomentsAPI().saveNote(new Moment(fileKey,"test note mobile"));
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

    private void handleRecButton()
    {
        switch (mRecState) {
            case RECORDING:
                stopRec();
                break;
            case STOPPED:
                startRec();
        }
    }

    private void startRec() {
        File file = new File(getFilesDir() + "/rec", String.valueOf(System.currentTimeMillis()) + ".amr");
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
        mRecorder = AudioRecorder.build(this, file.getAbsolutePath());
        mRecorder.start(new AudioRecorder.OnStartListener()
        {
            @Override
            public void onStarted()
            {
                Log.i(TAG, " startRecording");
                mRecState = RECORDING;
                mRecordButton.setText("Stop");
            }

            @Override
            public void onException(@NonNull Exception e)
            {
                Log.w(TAG, " startRecording - Exception:" + e);
                e.printStackTrace();
            }
        });
    }
    private void stopRec() {
        Log.d(TAG, " stopRec");
        mRecorder.stop();
        mRecState = STOPPED;
        mRecordButton.setText("Record");
        mUploadButton.setEnabled(true);
    }
}
