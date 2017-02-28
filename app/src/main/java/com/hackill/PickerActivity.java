package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hackill.demo.DatePickerScrollview;
import com.hackill.demo.R;

public class PickerActivity extends Activity {

    public final static String TAG = PickerActivity.class.getSimpleName();

    DatePickerScrollview minute_pv;

    int index = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        minute_pv = (DatePickerScrollview) findViewById(R.id.minute_pv);

        findViewById(R.id.member_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                minute_pv.setSelectItem(index--);

            }
        });
//        List<String> data = new ArrayList<String>();
//        for (int i = 61; i < 120; i++) {
//            data.add("" + i);//添加da
//        }
//
//        minute_pv.setItems(data);
//        minute_pv.setCheckListener(new TimePickerScrollView.OnItemCheckListener() {
//            @Override
//            public void onCheck(int index) {
//                Log.i(TAG, "onCheck: .... index = " + index);
//            }
//
//        });


    }
}
