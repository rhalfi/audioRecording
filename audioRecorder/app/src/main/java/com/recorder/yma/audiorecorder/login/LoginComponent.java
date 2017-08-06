package com.recorder.yma.audiorecorder.login;

/**
 * Created by ramh on 27/07/2017.
 */

import com.recorder.yma.audiorecorder.dagger.NetComponent;
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
@Component( dependencies = NetComponent.class,modules = {LoginPresenterModule.class})
public interface LoginComponent {

    void inject(LoginActivity activity);
}
