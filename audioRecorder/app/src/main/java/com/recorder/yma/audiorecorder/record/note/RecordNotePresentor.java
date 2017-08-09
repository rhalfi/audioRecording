package com.recorder.yma.audiorecorder.record.note;

import javax.inject.Inject;

/**
 * Created by ramh on 08/08/2017.
 */

public class RecordNotePresentor implements RecordNoteContract.Presenter{

    RecordNoteContract.View mRecordNoteView;
    AudioRecorderManager mAudioRecorder;

    @Inject
    RecordNotePresentor(AudioRecorderManager audioRecorder, RecordNoteContract.View recordNoteView) {

        mRecordNoteView = recordNoteView;
        mAudioRecorder = audioRecorder;
    }

    @Override
    public void startRecording() {
        mAudioRecorder.startRec();
    }

    @Override
    public void stopRecording() {
        mAudioRecorder.stopRec();
    }

    @Override
    public void uploadRecording() {
        mAudioRecorder.uploadRec();
    }

    @Override
    public void initFileStorageConnection() {
        mAudioRecorder.initFileStorageConnection();
    }

    @Override
    public void destroyFileStorageConnection() {
        mAudioRecorder.destroyFileStorageConnection();
    }

    @Override
    public void start() {

    }
}
