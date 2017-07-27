package com.recorder.yma.audiorecorder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.recorder.yma.audiorecorder.data.Moment;
import com.recorder.yma.audiorecorder.server.apis.FileStorageAPIS;
import com.recorder.yma.audiorecorder.server.apis.MomentsServiceAPIs;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.recorder.yma.audiorecorder.RecordNoteActivity.EREC_STATE.RECORDING;
import static com.recorder.yma.audiorecorder.RecordNoteActivity.EREC_STATE.STOPPED;

public class RecordNoteActivity extends AppCompatActivity {

    private static final String TAG = "RecordNoteActivity";
    private AudioRecorder mRecorder;
    private String mFilePath;

    @BindView(R.id.recordButton)
    Button mRecordButton;
    @BindView(R.id.uploadButton)
    Button mUploadButton;
    enum EREC_STATE {STOPPED,RECORDING};
    EREC_STATE mRecState = STOPPED;
    MediaPlayer mPlayer;
    @Inject
    MomentsServiceAPIs mMomentsServiceAPIs;
    @Inject
    FileStorageAPIS fileStorageAPIS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_note);
        MyApp.getApp().getNetComponent().inject(this);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.uploadButton)
    public void uploadRec(Button button)
    {
       // mFilePath = "/data/user/0/com.recorder.yma.audiorecorder/files/rec/1500466614564.amr";
        String fileKey = fileStorageAPIS.uploadFile(this, new File(mFilePath));
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

    @OnClick(R.id.recordButton)
    public void handleRecButton(Button button)
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
