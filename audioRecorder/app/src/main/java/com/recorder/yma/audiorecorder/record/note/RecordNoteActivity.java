package com.recorder.yma.audiorecorder.record.note;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.recorder.yma.audiorecorder.MyApp;
import com.recorder.yma.audiorecorder.R;
import com.recorder.yma.audiorecorder.server.apis.FileStorageModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.recorder.yma.audiorecorder.record.note.RecordNoteActivity.EREC_STATE.RECORDING;
import static com.recorder.yma.audiorecorder.record.note.RecordNoteActivity.EREC_STATE.STOPPED;

public class RecordNoteActivity extends AppCompatActivity implements RecordNoteContract.View{

    private static final String TAG = "RecordNoteActivity";


    @BindView(R.id.recordButton)
    Button mRecordButton;
    @BindView(R.id.uploadButton)
    Button mUploadButton;

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void onUploadFailed() {

    }

    @Override
    public void onUploadinSucceded() {

    }

    @Override
    public void setPresenter(RecordNoteContract.Presenter presenter) {

    }

    enum EREC_STATE {STOPPED,RECORDING};
    EREC_STATE mRecState = STOPPED;

    @Inject
    RecordNotePresentor mRecordNotePresntor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_note);

        ButterKnife.bind(this);

        // Create the presenter
       DaggerRecordNoteComponent.builder().recordNotePresenterModule (new RecordNotePresenterModule(this))
                .netComponent(MyApp.getApp().getNetComponent())
               .fileStorageModule(new FileStorageModule(this))
                .build().inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecordNotePresntor.initFileStorageConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecordNotePresntor.destroyFileStorageConnection();
    }

    @OnClick(R.id.uploadButton)
    public void uploadRec(Button button)
    {
       mRecordNotePresntor.uploadRecording();

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

        Log.d(TAG,"RichMessageHelper:startRecording: " );
        mRecState = RECORDING;
        mRecordNotePresntor.startRecording();
    }
    private void stopRec() {
        Log.d(TAG, " stopRec");
        mRecordNotePresntor.stopRecording();
        mRecState = STOPPED;
        mRecordButton.setText("Record");
        mUploadButton.setEnabled(true);
    }
}
