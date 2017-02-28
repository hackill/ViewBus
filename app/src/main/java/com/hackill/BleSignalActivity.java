package com.hackill;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.hackill.demo.FitIndicatorView;
import com.hackill.demo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hackill on 16/3/17.
 */
public class BleSignalActivity extends Activity {


    int progress = 20;
    float progressX = 0.2f;


    FitIndicatorView fitIndicatorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_search);

        fitIndicatorView = (FitIndicatorView) findViewById(R.id.fit_indicator_view);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FitIndicatorView.DataStruct dataStruct = new FitIndicatorView.DataStruct();
                List<FitIndicatorView.DataArea> dataAreaList = new ArrayList<>();
                List<String> displayList = new ArrayList<>();
                List<Integer> levelList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

                for (int i = 0; i < 5; i++) {
                    FitIndicatorView.DataArea dataArea = new FitIndicatorView.DataArea();
                    String dispaly = null;
                    if (i == 0) {
                        dataArea.setEndBorder(true);
                        dataArea.setEnd(18.5f);
                        dispaly = "偏低";
                    } else if (i == 1) {
                        dataArea.setStart(18.6f);
                        dataArea.setEnd(24);
                        dispaly = "正常";
                    } else if (i == 2) {
                        dataArea.setStart(24.1f);
                        dataArea.setEnd(28f);
                        dispaly = "偏胖";
                    } else if (i == 3) {
                        dataArea.setStart(28.1f);
                        dataArea.setEnd(35f);
                        dispaly = "肥胖";
                    } else if (i == 4) {
                        dataArea.setStart(35.1f);
                        dataArea.setStartBorder(true);
                        dispaly = "中毒肥胖";
                    }
                    dataAreaList.add(dataArea);
                    displayList.add(dispaly);
                }
                dataStruct.setDataAreaList(dataAreaList);
                dataStruct.setDisplayList(displayList);
                dataStruct.setStatusList(levelList);
                fitIndicatorView.setDataStruct(dataStruct);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FitIndicatorView.DataStruct dataStruct = new FitIndicatorView.DataStruct();
                List<FitIndicatorView.DataArea> dataAreaList = new ArrayList<>();
                List<String> displayList = new ArrayList<>();
                List<Integer> levelList = new ArrayList<>(Arrays.asList(1, 2, 6));

                for (int i = 0; i < 3; i++) {
                    FitIndicatorView.DataArea dataArea = new FitIndicatorView.DataArea();
                    String dispaly = null;
                    if (i == 0) {
                        dataArea.setEndBorder(true);
                        dataArea.setEnd(71.9f);
                        dispaly = "偏低";
                    } else if (i == 1) {
                        dataArea.setStart(72f);
                        dataArea.setEnd(82f);
                        dispaly = "正常";
                    } else if (i == 2) {
                        dataArea.setStart(82.1f);
                        dataArea.setStartBorder(true);
                        dispaly = "偏高";
                    }
                    dataAreaList.add(dataArea);
                    displayList.add(dispaly);
                }
                dataStruct.setData(70f);

                dataStruct.setDataAreaList(dataAreaList);
                dataStruct.setDisplayList(displayList);
                dataStruct.setStatusList(levelList);
                fitIndicatorView.setDataStruct(dataStruct);
            }
        });

        findViewById(R.id.button2_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
