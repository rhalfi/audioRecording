package com.recorder.yma.audiorecorder.recordings;

import com.recorder.yma.audiorecorder.dagger.NetComponent;
import com.recorder.yma.audiorecorder.util.FragmentScoped;

import dagger.Component;

/**
 * Created by ramh on 14/08/2017.
 */
@FragmentScoped
@Component( dependencies = {NetComponent.class},modules = {RecordingsListPresenterModule.class})
public interface RecordingsListComponent {
    void inject(RecordingsActivity activity);
}
