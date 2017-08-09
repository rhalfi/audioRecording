package com.recorder.yma.audiorecorder.server.apis;

import java.io.File;

/**
 * Created by ramh on 08/08/2017.
 */

public interface FileStorageAPIs {
    public String uploadFile(File file);

    public void init();
    public void discconect();


}
