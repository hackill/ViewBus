package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hackill.body.LevelCircleView;
import com.hackill.demo.R;

/**
 * Created by hackill on 16/3/17.
 */
public class LevelCircleActivity extends Activity {


    private static final String TAG = "LevelCircleActivity";
    LevelCircleView levelCircleView;


    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        levelCircleView = (LevelCircleView) findViewById(R.id.level);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i++;
                levelCircleView.setLevel(i);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i--;
                levelCircleView.setLevel(i);
            }
        });

        levelCircleView.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: .....");
                levelCircleView.startAnimation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        levelCircleView.stopAnimation();
    }
}
