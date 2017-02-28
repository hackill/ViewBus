package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hackill.body.CircleWaveLoadView;
import com.hackill.demo.BleSearchView;
import com.hackill.demo.LineLoadingView;
import com.hackill.demo.R;

/**
 * Created by hackill on 16/3/17.
 */
public class BleSearchActivity extends Activity {

//    BleSearchView bleSearchView;
//    LineLoadingView loadingView;
//
//    CircleWaveLoadView mCircleWave;


    int progress = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_search);
//        bleSearchView = (BleSearchView) findViewById(R.id.ble_search_View);
//        loadingView = (LineLoadingView) findViewById(R.id.lineLoadingView);
//        mCircleWave = (CircleWaveLoadView) findViewById(R.id.circleWare);
//
//        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                mCircleWave.startLoading();
//            }
//        });
//
//        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCircleWave.startProgressAnimator();
//                progress = 0;
//            }
//        });
//
//        findViewById(R.id.button2_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCircleWave.setProgress(progress);
//                progress += 5;
//            }
//        });
//
//
//        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCircleWave.startSuccessAnimator();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
