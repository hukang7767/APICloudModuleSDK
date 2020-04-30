package com.alpha.modulegnoga.measurement;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SubscriptSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alpha.modulegnoga.R;
import com.alpha.modulegnoga.common.BaseActivity;
import com.cnoga.singular.mobile.sdk.constants.DeviceConstant;
import com.cnoga.singular.mobile.sdk.constants.MeasurementConstants;
import com.cnoga.singular.mobile.sdk.constants.RangeConstant;
import com.cnoga.singular.mobile.sdk.device.DeviceStatus;
import com.cnoga.singular.mobile.sdk.measurement.CnogaMeasurementManager;
import com.cnoga.singular.mobile.sdk.measurement.IMeasurementListener;
import com.cnoga.singular.mobile.sdk.measurement.ParamLineChartView;

import java.lang.ref.WeakReference;

public class MeasurementParamActivity extends BaseActivity implements IMeasurementListener,
        View.OnClickListener {

    public static final String PARAM_TYPE = "parameterTypes";

    public static final String VIEW_MODE = "viewMode";

    private static final String TAG = "ParamDetailActivity";

    private static final int[] PARAM_NAME = new int[]{
            R.string.heart_rate, R.string.spo2,
            R.string.blood_pressure, R.string.blood_pressure, R.string.cbg, R.string.hgb,
            R.string.po2, R.string.pco2, R.string.hct, R.string.bv,
            R.string.co, R.string.map, R.string.ph, R.string.co2,
            R.string.rbc, R.string.sv, R.string.hba1c, R.string.o2
    };

    private static final int MEASUREMENT_STOP = 1001;

    private static final int ON_DATA_AVAILABLE = 1003;

    private CnogaMeasurementManager mCnogaMeasurementManager;

    private ParamLineChartView mParamLineChartView;

    private ParamDetailInfoView mParamDetailInfoView;

    private ImageView mBackArrow;

    private int mParameterType = MeasurementConstants.PARAM_TYPE_UNKNOWN;

    private int mViewMode = MeasurementConstants.MODE_MEASUREMENT;

    private boolean isMeasuring = false;

    private MyHandler mHandler = new MyHandler(MeasurementParamActivity.this);

    private static class MyHandler extends Handler {
        WeakReference<MeasurementParamActivity> mActivity;

        MyHandler(MeasurementParamActivity activity) {
            mActivity = new WeakReference<MeasurementParamActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MeasurementParamActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            switch (msg.what) {
                case MEASUREMENT_STOP:
                    activity.isMeasuring = false;
                    activity.finish();
                    break;
                case ON_DATA_AVAILABLE:
                    if (!activity.isMeasuring) {
                        activity.isMeasuring = true;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_param);
        initial();
    }

    /**
     * general initial
     */
    private void initial() {
        mCnogaMeasurementManager = CnogaMeasurementManager.getInstance(this);
        if (mViewMode == MeasurementConstants.MODE_MEASUREMENT) {
            mCnogaMeasurementManager.setMeasurementListener(this);
        }
        mParameterType = getIntent().getIntExtra(PARAM_TYPE, -1);
        mViewMode = getIntent().getIntExtra(VIEW_MODE, MeasurementConstants.MODE_MEASUREMENT);
        mParamLineChartView = (ParamLineChartView) findViewById(R.id.param_measurement_line_chart);
        // mParamLineChartView.setLineColor(Color.RED, Color.GREEN);
        // mParamLineChartView.setLineWidth(2.5F);
        mParamLineChartView.setParamType(this, mParameterType, mViewMode);
        mParamDetailInfoView = (ParamDetailInfoView) findViewById(R.id.param_measurement_detail_info);
        mParamDetailInfoView.setParamType(this, mParameterType, mViewMode);

        mBackArrow = (ImageView) findViewById(R.id.title_left_icon);
        mBackArrow.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title_top_text);
        title.setText(getTitleString());
        title.setVisibility(View.VISIBLE);

    }

    private String getTitleString() {
        String title = getString(getParamTitleResId());
        if (title.contains(RangeConstant.TWO)) {
            Spannable spannable = new SpannableString(title);
            spannable.setSpan(
                    new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.symbol)),
                    title.length() - 1,
                    title.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new SubscriptSpan(),
                    title.length() - 1,
                    title.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable.toString();
        } else {
            return title;
        }
    }

    private int getParamTitleResId() {
        if (mParameterType == MeasurementConstants.PARAM_TYPE_BLOOD_PRESSURE) {
            return R.string.blood_pressure;
        }
        if (mParameterType < 0 || mParameterType >= PARAM_NAME.length) {
            return R.string.error_parameter;
        } else {
            return PARAM_NAME[mParameterType];
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCnogaMeasurementManager.unRegMeasurementListener(this);
        // mParamLineChartView.onDestroy();
        // mParamDetailInfoView.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.title_left_icon) {
            finish();

        } else {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDataAvailable(int count) {
        Message msg = mHandler.obtainMessage();
        msg.what = ON_DATA_AVAILABLE;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onConnectionStatusChanged(int connection) {
        switch (connection) {
            case DeviceConstant.DEVICE_STATUS_DISCONNECTED:
                if (isMeasuring) {
                    isMeasuring = false;
                    mHandler.sendEmptyMessage(MEASUREMENT_STOP);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDeviceStatusChanged(DeviceStatus status) {
        switch (status.getMedialStatus()) {
            case DeviceStatus.MEDICAL_STATUS_IDLE:
                mHandler.sendEmptyMessage(MEASUREMENT_STOP);
                break;
            case DeviceStatus.MEDICAL_STATUS_MEASURING:
                isMeasuring = true;
                break;
        }
    }

    @Override
    public void onMeasurementFinished() {
        mHandler.sendEmptyMessage(MEASUREMENT_STOP);
    }

    @Override
    public void onUploadFinished(int i, String s) {

    }

    @Override
    public void onMeasurementInterrupted(int type) {
        isMeasuring = false;
        mHandler.sendEmptyMessage(MEASUREMENT_STOP);
    }

}
