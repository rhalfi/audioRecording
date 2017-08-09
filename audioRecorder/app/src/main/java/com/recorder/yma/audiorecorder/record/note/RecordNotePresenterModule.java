package com.recorder.yma.audiorecorder.record.note;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramh on 08/08/2017.
 */
@Module
public class RecordNotePresenterModule {
    private final RecordNoteContract.View mView;

    public RecordNotePresenterModule(RecordNoteContract.View view) {
        mView = view;
    }

    @Provides
    RecordNoteContract.View provideRecordNoteContractView() {
        return mView;
    }
}
