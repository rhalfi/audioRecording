package com.recorder.yma.audiorecorder.record.note;

/**
 * Created by ramh on 08/08/2017.
 */

import com.recorder.yma.audiorecorder.dagger.NetComponent;
import com.recorder.yma.audiorecorder.server.apis.FileStorageModule;
import com.recorder.yma.audiorecorder.util.FragmentScoped;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.recorder.yma.audiorecorder.MyApp} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link NetComponent}, which is a singleton, a
 * scope must be specified. All fragment components use a custom scope for this purpose.
 */
@FragmentScoped
@Component( dependencies = {NetComponent.class},modules = {RecordNotePresenterModule.class, FileStorageModule.class})
public interface RecordNoteComponent {
    void inject(RecordNoteActivity activity);


}
