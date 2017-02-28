package com.hackill.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    SearchView searchView;
    FindBongView findBongView;
    Button button, button2;

    int i = 0;
    int j = 0;
    int k = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.search_view);
        findBongView = (FindBongView) findViewById(R.id.find_bong_view);
        button = (Button) findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                if (i % 2 == 1) {
                    searchView.startWareAnimation();
                } else {
                    searchView.stopWareAnimation();
                }

            }
        });
        button2 = (Button) findViewById(R.id.search);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                j++;
                if (j % 2 == 1) {
                    searchView.startSearchAnimation();
                } else {
                    searchView.stopSearchAnimation();
                }
            }
        });


        findViewById(R.id.start2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                k++;
                if (k % 2 == 1) {
                    findBongView.startAnimator();
                } else {
                    findBongView.stopAnimator();

                }
            }
        });
        findViewById(R.id.line1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBongView.setLine(Line.LINE_ONE);
            }
        });
        findViewById(R.id.line2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBongView.setLine(Line.LINE_TWO);

            }
        });

        findViewById(R.id.line3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CycleActivity.class));
            }
        });

    }
}
