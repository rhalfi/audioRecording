package com.recorder.yma.audiorecorder.recordings;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramh on 14/08/2017.
 */
@Module
public class RecordingsListPresenterModule {

    private final RecordingsListContract.View mView;

    public RecordingsListPresenterModule(RecordingsListContract.View view) {
        mView = view;
    }

    @Provides
    RecordingsListContract.View provideRecordingsListContractView() {
        return mView;
    }
}
