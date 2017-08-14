package com.recorder.yma.audiorecorder.recordings;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.recorder.yma.audiorecorder.MyApp;
import com.recorder.yma.audiorecorder.R;
import com.recorder.yma.audiorecorder.RecyclerItemClickListener;
import com.recorder.yma.audiorecorder.data.Moment;
import com.recorder.yma.audiorecorder.record.note.RecordNoteActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecordingsActivity extends AppCompatActivity implements RecordingsListContract.View{
    private static String TAG = "RecordingsActivity";
    public static final String AWS_TOKEN = "AWS_TOKEN";
    private RecyclerView recyclerView;
    @Inject
    RecordingsListPresentor recordingsListPresentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DaggerRecordingsListComponent.builder().netComponent(MyApp.getApp().getNetComponent())
                .recordingsListPresenterModule(new RecordingsListPresenterModule(this)).build().inject(this);




        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "new rec:" );
                Intent intent = new Intent(RecordingsActivity.this, RecordNoteActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(RecordingsActivity.this));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d(TAG,"onItemClick " + position);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        recordingsListPresentor.start();
    }

    @Override
    public void updateRecordings(List<Moment> moments) {
        Log.v("RecordingsActivity", "onSuccess:");
        for(Moment moment: moments){
            Log.d("RecordingsActivity", "onResponse:" + moment.getContent());
        }
        List<Object> data = new ArrayList<>();
        data.addAll(moments);
        MomentsListViewAdapter adapter = new MomentsListViewAdapter(data);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setPresenter(RecordingsListContract.Presenter presenter) {

    }
}
