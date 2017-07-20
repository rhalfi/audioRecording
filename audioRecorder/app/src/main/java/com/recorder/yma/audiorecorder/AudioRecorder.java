package com.recorder.yma.audiorecorder;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;


public class AudioRecorder
{
    public static final String TAG = "AudioRecorder";

    public static enum Status
    {
        STATUS_UNKNOWN, STATUS_READY_TO_RECORD, STATUS_RECORDING, STATUS_RECORD_PAUSED
    }

    public static interface OnException
    {
        public void onException(Exception e);
    }

    public static interface OnStartListener extends OnException
    {
        public void onStarted();
    }



    public static class MediaRecorderConfig
    {
        private final int mAudioEncodingBitRate;
        private final int mAudioChannels;
        private final int mAudioSource;
        private final int mAudioEncoder;

        public static final MediaRecorderConfig DEFAULT = new MediaRecorderConfig(64 * 1024, /*
																							 * 64
																							 * Kib
																							 * per
																							 * second
																							 */
                2, /* Stereo */
                MediaRecorder.AudioSource.DEFAULT, /*
											 * Default audio source. (usually,
											 * phone microphone)
											 */
                MediaRecorder.AudioEncoder.AAC);

        /**
         * Constructor.
         *
         * @param audioEncodingBitRate
         *            Used for
         *            {@link android.media.MediaRecorder#setAudioEncodingBitRate}
         * @param audioChannels
         *            Used for
         *            {@link android.media.MediaRecorder#setAudioChannels}
         * @param audioSource
         *            Used for
         *            {@link android.media.MediaRecorder#setAudioSource}
         * @param audioEncoder
         *            Used for
         *            {@link android.media.MediaRecorder#setAudioEncoder}
         */
        public MediaRecorderConfig(int audioEncodingBitRate, int audioChannels, int audioSource, int audioEncoder)
        {
            mAudioEncodingBitRate = audioEncodingBitRate;
            mAudioChannels = audioChannels;
            mAudioSource = audioSource;
            mAudioEncoder = audioEncoder;
        }

    }

    public class StartRecordTask extends AsyncTask<OnStartListener, Void, Exception>
    {

        private OnStartListener mOnStartListener;

        @Nullable
        @Override
        protected Exception doInBackground(OnStartListener... params)
        {
            this.mOnStartListener = params[0];
            if(mMediaRecorder!=null)
            {
                mMediaRecorder.release();
                mMediaRecorder=null;
            }
            mMediaRecorder = new MediaRecorder();
            //mMediaRecorder.setAudioChannels(mMediaRecorderConfig.mAudioChannels);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(mTargetRecordFileName);
            mMediaRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AAC);

            Exception exception = null;
            try
            {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            }
            catch (Exception e)
            {
                exception = e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(@Nullable Exception e)
        {
            super.onPostExecute(e);
            if (e == null)
            {
                setStatus(AudioRecorder.Status.STATUS_RECORDING);
                mOnStartListener.onStarted();
            }
            else
            {
                setStatus(AudioRecorder.Status.STATUS_READY_TO_RECORD);
                mOnStartListener.onException(e);
            }
        }
    }




    private Status mStatus;
    private String mTargetRecordFileName;
    @Nullable
    private MediaRecorder mMediaRecorder;
    private Context context;
    private MediaRecorderConfig mMediaRecorderConfig;

    private AudioRecorder()
    {
        mStatus = Status.STATUS_UNKNOWN;
    }

    /**
     * Returns the ready-to-use AudioRecorder.
     * as {@link android.media.MediaRecorder} config.
     */
    @NonNull
    public static AudioRecorder build(final Context context, final String targetFileName)
    {
        return build(context, targetFileName, MediaRecorderConfig.DEFAULT);
    }

    /**
     * Returns the ready-to-use AudioRecorder.
     */
    @NonNull
    public static AudioRecorder build(Context context, final String targetFileName,
                                      final MediaRecorderConfig mediaRecorderConfig)
    {
        AudioRecorder rvalue = new AudioRecorder();
        rvalue.mTargetRecordFileName = targetFileName;
        rvalue.context = context;
        rvalue.mMediaRecorderConfig = mediaRecorderConfig;
        rvalue.mStatus = Status.STATUS_READY_TO_RECORD;
        return rvalue;
    }

    /**
     * Continues existing record or starts new one.
     */
    @SuppressLint("NewApi")
    public void start(final OnStartListener listener)
    {
        StartRecordTask task = new StartRecordTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, listener);
    }

    public void stop(){
        try
        {
            mMediaRecorder.stop();
            mMediaRecorder.release();
        }
        catch (Exception e)
        {
            Log.e(TAG, "failed to stop recorder");
        }
    }



    public Status getStatus()
    {
        return mStatus;
    }

    public String getRecordFileName()
    {
        return mTargetRecordFileName;
    }

    public boolean isRecording()
    {
        return mStatus == Status.STATUS_RECORDING;
    }

    public boolean isReady()
    {
        return mStatus == Status.STATUS_READY_TO_RECORD;
    }

    public boolean isPaused()
    {
        return mStatus == Status.STATUS_RECORD_PAUSED;
    }

    private void setStatus(final Status status)
    {
        mStatus = status;
    }

    @NonNull
    private String getTemporaryFileName()
    {
        return context.getCacheDir().getAbsolutePath() + File.separator + "tmprecord";
    }


}
