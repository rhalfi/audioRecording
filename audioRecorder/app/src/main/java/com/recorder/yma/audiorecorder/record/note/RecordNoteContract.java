package com.recorder.yma.audiorecorder.record.note;

import com.recorder.yma.audiorecorder.BasePresenter;
import com.recorder.yma.audiorecorder.BaseView;

/**
 * Created by ramh on 08/08/2017.
 */

public class RecordNoteContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void onUploadFailed();
        void onUploadinSucceded();

    }

    interface Presenter extends BasePresenter {

        void startRecording();
        void stopRecording();
        void uploadRecording();
        void initFileStorageConnection();
        void destroyFileStorageConnection();

    }
}
