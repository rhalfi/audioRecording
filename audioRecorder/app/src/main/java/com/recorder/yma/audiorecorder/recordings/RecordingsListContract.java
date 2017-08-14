package com.recorder.yma.audiorecorder.recordings;

import com.recorder.yma.audiorecorder.BasePresenter;
import com.recorder.yma.audiorecorder.BaseView;
import com.recorder.yma.audiorecorder.data.Moment;

import java.util.List;

/**
 * Created by ramh on 14/08/2017.
 */

public class RecordingsListContract {

    interface View extends BaseView<RecordingsListContract.Presenter> {

      //  void setLoadingIndicator(boolean active);

        void updateRecordings(List<Moment> moments);



    }

    interface Presenter extends BasePresenter {

    }
}
