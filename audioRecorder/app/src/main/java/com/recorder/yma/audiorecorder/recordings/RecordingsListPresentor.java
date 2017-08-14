package com.recorder.yma.audiorecorder.recordings;

import android.util.Log;

import com.recorder.yma.audiorecorder.data.Moment;
import com.recorder.yma.audiorecorder.server.apis.MomentsServiceAPIs;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ramh on 14/08/2017.
 */

public class RecordingsListPresentor implements  RecordingsListContract.Presenter{

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    MomentsServiceAPIs mMomentsServiceAPIs;

    RecordingsListContract.View mRecordingListView;

    @Inject
    RecordingsListPresentor(RecordingsListContract.View recordingListView,MomentsServiceAPIs momentsServiceAPIs) {

        mRecordingListView = recordingListView;
        mMomentsServiceAPIs = momentsServiceAPIs;
    }

    @Override
    public void start() {
        initData();
    }

    private void initData(){
        compositeDisposable.add(mMomentsServiceAPIs.getMoments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getMomentsObservable()));

    }

    private DisposableSingleObserver<List<Moment>> getMomentsObservable() {
        return new DisposableSingleObserver<List<Moment>>() {
            @Override
            public void onSuccess(List<Moment> moments) {
                mRecordingListView.updateRecordings(moments);
            }

            @Override
            public void onError(Throwable e) {
                Log.v("RecordingsActivity", "onError:");
                e.printStackTrace();
                //Toast.makeText(RecordingsActivity.this, "Can not load memories", Toast.LENGTH_SHORT).show();

            }
        };
    }
}
