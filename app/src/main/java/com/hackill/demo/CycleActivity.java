package com.hackill.demo;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import java.util.List;

public class CycleActivity extends Activity {

    private CycleScrollView<PackageInfo> mCycleScrollView;
    private AppCycleScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);


        mCycleScrollView = ((CycleScrollView<PackageInfo>) this.findViewById(R.id.cycle_scroll_view));

        /**
         * Get APP list and sort by update time.
         */
        List<PackageInfo> list = this.getPackageManager()
                .getInstalledPackages(0);

        mAdapter = new AppCycleScrollAdapter(list, mCycleScrollView, this);
    }
}
