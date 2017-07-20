package com.recorder.yma.audiorecorder;

/**
 * Created by ramh on 21/06/2017.
 */

public abstract class IResponseHandler {

    abstract public void onSuccess();
    abstract public void onFailed(String error);
}
