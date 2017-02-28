package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hackill.body.BalanceView;
import com.hackill.demo.R;

/**
 * Created by hackill on 16/3/17.
 */
public class BodyTestActivity extends Activity {


    BalanceView balanceView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        balanceView = (BalanceView) findViewById(R.id.balance);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balanceView.startAverageAnimation();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balanceView.startUserAnimation();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balanceView.stopAnimation();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        balanceView.stopAnimation();
    }
}
