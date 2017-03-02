package com.hackill;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hackill.body.LevelCircleView;
import com.hackill.demo.R;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by hackill on 16/3/17.
 */
public class LevelCircleActivity extends Activity {


    private static final String TAG = "LevelCircleActivity";
    LevelCircleView levelCircleView;
    RelativeLayout one;
    RelativeLayout two;
    LinearLayout btn;

    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        levelCircleView = (LevelCircleView) findViewById(R.id.level);
        one = (RelativeLayout) findViewById(R.id.one);
        two = (RelativeLayout) findViewById(R.id.two);
        btn = (LinearLayout) findViewById(R.id.function);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTwo();
//                i++;
//                levelCircleView.setLevel(i);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dismissTwo();
//                i--;
//                levelCircleView.setLevel(i);
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


    private void showTwo() {
        one.post(new Runnable() {
            @Override
            public void run() {
                getOneAnimator().start();
            }
        });
    }

    private void dismissTwo() {
        two.post(new Runnable() {
            @Override
            public void run() {
                getTwoAnimator().start();
            }
        });
    }


    private Animator getOneAnimator() {

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        int startX = point.x / 2;
        int startY = 0;

        int minSIze = point.x / 8;

        final Animator mRevealAnimator = ViewAnimationUtils.createCircularReveal(two, startX, startY, minSIze, point.y * 1.2f);
        mRevealAnimator.setDuration(1000);
        mRevealAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                two.setVisibility(View.VISIBLE);
                two.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                btn.bringToFront();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mRevealAnimator;
    }


    private Animator getTwoAnimator() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        int startX = point.x / 2;
        int startY = 0;

        int minSIze = point.x / 8;

        final Animator mRevealAnimator = ViewAnimationUtils.createCircularReveal(two, startX, startY, point.y * 1.2f, minSIze);
        mRevealAnimator.setDuration(1000);
        mRevealAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                two.setVisibility(View.VISIBLE);
                two.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                one.bringToFront();
                btn.bringToFront();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mRevealAnimator;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        levelCircleView.stopAnimation();
    }
}
