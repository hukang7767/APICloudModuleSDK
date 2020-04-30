package com.alpha.modulegnoga.measurement;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.alpha.modulegnoga.R;
import com.alpha.modulegnoga.common.BaseActivity;

public class MeasurementActivity extends BaseActivity {

    private FragmentManager mFragmentManager;

    private MeasurementFragment mMeasurementFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        mFragmentManager = getSupportFragmentManager();
        mMeasurementFragment = (MeasurementFragment) mFragmentManager
                .findFragmentById(R.id.fragment_measure);
        mFragmentManager.beginTransaction().show(mMeasurementFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (mMeasurementFragment != null && mMeasurementFragment.isMeasuring()) {
            Toast.makeText(this, "Can not exit when measuring", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
