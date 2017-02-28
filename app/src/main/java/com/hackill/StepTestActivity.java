package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hackill.body.ProgressView;
import com.hackill.body.StepView;
import com.hackill.demo.R;

/**
 * Created by hackill on 16/3/17.
 */
public class StepTestActivity extends Activity {


    StepView stepView = null;

    ProgressView progressView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        stepView = (StepView) findViewById(R.id.stepView);
        progressView = (ProgressView) findViewById(R.id.progress_view);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.startWareAnimation();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.startSuccessAnimation();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepView.stopAnimation();
            }
        });
    }


    public void onButton4Click(View v) {
        progressView.startResetProgressAnimation();
    }

    public void onButton5Click(View v) {
        progressView.setProgress(60, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stepView.stopAnimation();
        progressView.stopAnimation();
    }
}
