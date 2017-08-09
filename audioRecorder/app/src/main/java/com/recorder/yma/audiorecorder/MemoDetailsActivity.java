package com.recorder.yma.audiorecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.recorder.yma.audiorecorder.record.note.RecordNoteActivity;


public class MemoDetailsActivity extends AppCompatActivity {


    private final static String TAG = "MemoDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "new rec:" );
                Intent intent = new Intent(MemoDetailsActivity.this, RecordNoteActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView contentTextView = (TextView)findViewById(R.id.contentText);
        TextView attachmentTextView = (TextView)findViewById(R.id.attachmentTextView);
    }

}
