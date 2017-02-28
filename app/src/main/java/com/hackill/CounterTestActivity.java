package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hackill.body.CounterView;
import com.hackill.demo.R;

/**
 * Created by hackill on 16/3/17.
 */
public class CounterTestActivity extends Activity {


    CounterView counterView;


    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        counterView = (CounterView) findViewById(R.id.counterView);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterView.setProgress(i, 100);
                i++;
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterView.startAutoAnimation();
            }

        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterView.stopAnimation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        counterView.stopAnimation();
    }
}
