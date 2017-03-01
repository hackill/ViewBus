package com.hackill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hackill.demo.MainActivity;
import com.hackill.demo.R;

/**
 * Created by hackill on 16/3/17.
 */
public class StartActivity extends Activity {


    public final static String TAG = StartActivity.class.getSimpleName();
    AudioManager audioManage;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        textView = (TextView) findViewById(R.id.value);
        audioManage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audoJudge();
    }


    private void audoJudge() {

        int mode = audioManage.getRingerMode();

        Log.i(TAG, "audoJudge: mode = " + mode);
        /**
         *     public static final int RINGER_MODE_SILENT = 0;

         /**
         * Ringer mode that will be silent and will vibrate. (This will cause the
         * phone ringer to always vibrate, but the notification vibrate to only
         * vibrate if set.)
         *
         * @see #setRingerMode(int)
         * @see #getRingerMode()
         */
//        public static final int RINGER_MODE_VIBRATE = 1;

        /**
         * Ringer mode that may be audible and may vibrate. It will be audible if
         * the volume before changing out of this mode was audible. It will vibrate
         * if the vibrate setting is on.
         *
         * @see #setRingerMode(int)
         * @see #getRingerMode()
         */
//        public static final int RINGER_MODE_NORMAL = 2;
//         */
    }


    public void onBalance(View v) {
        startActivity(new Intent(this, BodyTestActivity.class));
    }

    public void onSearch(View v) {
        startActivity(new Intent(this, MainActivity.class));
//        audioManage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//
//        int current = audioManage.getStreamVolume(AudioManager.STREAM_RING);
//        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_MUSIC));
//        textView.setText("volume = " + current + " mode = " + getType(audioManage.getRingerMode()));

    }

    public void onStep(View v) {
        startActivity(new Intent(this, StepTestActivity.class));
//        audioManage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        int current = audioManage.getStreamVolume(AudioManager.STREAM_RING);
//        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_RING));
//        textView.setText("volume = " + current + " mode = " + getType(audioManage.getRingerMode()));

    }

    public void onCounter(View v) {
        startActivity(new Intent(this, CounterTestActivity.class));
//        audioManage.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//        int current = audioManage.getStreamVolume(AudioManager.STREAM_RING);
//        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_RING));
//        textView.setText("volume = " + current + " mode = " + getType(audioManage.getRingerMode()));

    }

    public void onBleSearch(View v) {
        startActivity(new Intent(this, BleSignalActivity.class));
    }


    public void onLevelCircle(View v) {
        startActivity(new Intent(this, LevelCircleActivity.class));
    }


    public void onUp(View v) {
        audioManage.adjustVolume(AudioManager.ADJUST_RAISE, 0);
        int current = audioManage.getStreamVolume(AudioManager.STREAM_RING);
        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_RING));
        textView.setText("volume = " + current + " mode = " + getType(audioManage.getRingerMode()));
    }


    public void onDown(View v) {
//        audioManage.adjustVolume(AudioManager.ADJUST_LOWER, 0);
//        int current = audioManage.getStreamVolume(AudioManager.STREAM_RING);
//        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_RING));
//        textView.setText("volume = " + current + " mode = " + getType(audioManage.getRingerMode()));

        audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, 10, AudioManager.FLAG_PLAY_SOUND);

    }

    public void onVoiceUp(View v) {
        audioManage.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);


        int current = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_MUSIC));
        textView.setText("media voice  = " + current + " mode = " + getType(audioManage.getRingerMode()));
    }

    public void onVoiceDown(View v) {
        audioManage.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);


        int current = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "onUp: currentVolume = " + audioManage.getStreamVolume(AudioManager.STREAM_MUSIC));
        textView.setText("media voice  = " + current + " mode = " + getType(audioManage.getRingerMode()));
    }


    public void onDate(View v) {
        startActivity(new Intent(this, PickerActivity.class));
    }


    private String getType(int type) {
        if (type == AudioManager.RINGER_MODE_NORMAL) {
            return "NORMAL";
        } else if (type == AudioManager.RINGER_MODE_SILENT) {
            return "Silent";
        } else {
            return "zhendong";
        }
    }
}
