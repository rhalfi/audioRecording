package com.recorder.yma.audiorecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.MyApp;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.AppModule;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.DaggerNetComponent;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.DaggerProvisioningComponent;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.NetModule;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.dagger.ProvisioningModule;
import com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.server.apis.ProvisionAPIs;

import java.io.File;
import java.io.FileInputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";
    @BindView(R.id.userEditText) EditText userText;
    @BindView(R.id.passwordEditText) EditText pswText;

    @Inject
    ProvisionAPIs provisionAPIs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        checkPermissionForRecord();
        setupRecording();
        DaggerProvisioningComponent.builder()
                // list of modules that are part of this component need to be created here too
                .provisioningModule(new ProvisioningModule() // This also corresponds to the name of your module: %component_name%Module
                ).build().inject(this);
    }

    @OnClick(R.id.loginButton)
    public void loginCLicked(Button button) {
        provisionAPIs.login(MainActivity.this, userText.getText().toString(), pswText.getText().toString()
                , new IResponseHandler() {
                    @Override
                    public void onFailed(String error) {
                        Log.e(TAG, "onFailed:" + error);
                    }

                    @Override
                    public void onSuccess() {
                        onLoginFinished();
                    }
                });
    }

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100;
    private void checkPermissionForRecord(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private AudioRecorder mRecorder;
    private String mFilePath;
    MediaPlayer mPlayer;
    private void setupRecording() {
        Button startRec = (Button) findViewById(R.id.startRecButton);
        Button stopRecButton = (Button) findViewById(R.id.stopButton);

        Button startPlay = (Button) findViewById(R.id.startPlayRecButton);
        Button stopPlayButton = (Button) findViewById(R.id.stopPlayButton);

        startRec.setOnClickListener(view -> startRec());
        stopRecButton.setOnClickListener(view -> stopRec());

        startPlay.setOnClickListener(view -> playAudioFile(mFilePath));
        stopPlayButton.setOnClickListener(view -> stopPlayer());
    }

    private void startRec() {
        File file = new File(getFilesDir() + "/rec", String.valueOf(System.currentTimeMillis()) + ".amr");
        // make sure the directory exist
        File folder = file.getParentFile();
        if (!folder.exists())
        {
            folder.mkdirs();
        }

        if(file == null){
            Log.e(TAG,"startRecording: " + " filed to create file for recording");
            return;
        }
        mFilePath = file.getAbsolutePath();
        Log.d(TAG,"RichMessageHelper:startRecording: " + mFilePath);
        mRecorder = AudioRecorder.build(this, file.getAbsolutePath());
        mRecorder.start(new AudioRecorder.OnStartListener()
        {
            @Override
            public void onStarted()
            {
                Log.i(TAG, " startRecording");
            }

            @Override
            public void onException(@NonNull Exception e)
            {
                Log.w(TAG, " startRecording - Exception:" + e);
                e.printStackTrace();
            }
        });
    }
    private void stopRec() {
        mRecorder.stop();
    }

    private void playAudioFile(String filePath) {


        try {
            if(mPlayer != null) {
                mPlayer.release();
            }
            mPlayer = new MediaPlayer();
            FileInputStream is = new FileInputStream(mFilePath);
            mPlayer.setDataSource(is.getFD());
            mPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception of type : " + e.toString());
            e.printStackTrace();
        }

        mPlayer.start();
    }

    private void stopPlayer(){
        mPlayer.stop();

    }

    private void onLoginFinished() {
        Log.d(TAG, "onLoginFinished:" + provisionAPIs.geTToken());
        Intent intent = new Intent(this, RecordingsActivity.class);
        MyApp.getApp().setmNetComponent(DaggerNetComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(MyApp.getApp())) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule("https://d2s4oxiwc3.execute-api.us-west-2.amazonaws.com/prod/", provisionAPIs.geTToken()))
                .build());
        startActivity(intent);
    }


}
