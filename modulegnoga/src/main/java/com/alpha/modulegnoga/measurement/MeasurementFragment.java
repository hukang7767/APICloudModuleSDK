package com.alpha.modulegnoga.measurement;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.modulegnoga.APIModuleGnoga;
import com.alpha.modulegnoga.R;
import com.alpha.modulegnoga.device.AppDeviceManager;
import com.alpha.modulegnoga.utils.LoadingDialog;
import com.cnoga.singular.mobile.sdk.constants.DeviceConstant;
import com.cnoga.singular.mobile.sdk.constants.MeasurementConstants;
import com.cnoga.singular.mobile.sdk.device.CnogaDeviceManager;
import com.cnoga.singular.mobile.sdk.device.DeviceStatus;
import com.cnoga.singular.mobile.sdk.measurement.CnogaMeasurementManager;
import com.cnoga.singular.mobile.sdk.measurement.IMeasurementListener;
import com.cnoga.singular.mobile.sdk.measurement.WaveChartFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Measurement fragment
 */
public class MeasurementFragment extends Fragment
        implements OnPageChangeListener, MeasurementGroupCards.OnParamClickListener,
        IMeasurementListener {

    private static final String TAG = "MeasurementFragment";

    // message what
    private static final int ON_DATA_AVAILABLE = 1001;

    private static final int ON_MEASURE_FINISH = 1002;

    private static final int ON_UPLOAD_FINISH = 1003;

    /**
     * 用于标记开始测量25s之后，如果没有收到数据，就提示重新连接
     */
    private static final int MEASURE_NO_DATA_RECONNECT = 1005;

    /**
     * 用于标记开始测量最后一次获取数据8s之后，如果没有收到数据（即数据没有上报），就提示重新连接
     */
    private static final int MEASURE_GOT_DATA_RECONNECT = 1006;

    private static final int MEASURE_LESS_DATA = 1007;

    private static final int MEASURE_PARSE_DATA_ERROR = 1008;

    private static final int MEASURE_NETWORK_UNAVAILABLE = 1009;

    public static final int[] PARAM_NAME = new int[]{
            R.string.heart_rate, R.string.spo2,
            R.string.blood_pressure, R.string.blood_pressure, R.string.cbg, R.string.hgb,
            R.string.po2, R.string.pco2, R.string.hct, R.string.bv,
            R.string.co, R.string.map, R.string.ph, R.string.co2,
            R.string.rbc, R.string.sv, R.string.hba1c, R.string.o2,
            R.string.wbc, R.string.plt, R.string.k, R.string.na,
            R.string.ca, R.string.cl, R.string.tBili, R.string.alb,
            R.string.hpi, R.string.blvct, R.string.hco3, R.string.svo2,
            R.string.param4, R.string.param5, R.string.blood_pressure};

    public static final int[] PARAM_ABBR = new int[]{
            R.string.hemna_hr, R.string.hemna_spo2,
            R.string.chart_type_sys, R.string.chart_type_dia, R.string.bm_cbg, R.string.hemto_hgb,
            R.string.bg_po2, R.string.bg_pco2, R.string.hemto_hct, R.string.hemna_bv,
            R.string.hemna_co, R.string.hemna_map, R.string.bg_ph, R.string.bm_co2,
            R.string.hemto_rbc, R.string.hemna_sv, R.string.bm_hba1c, R.string.bg_o2,
            R.string.bm_wbc, R.string.bm_plt, R.string.bm_k, R.string.bm_na,
            R.string.bm_ca, R.string.bm_cl, R.string.bm_bili, R.string.bm_alb,
            R.string.bm_hpi, R.string.bm_blvct, R.string.bm_hco3, R.string.bm_svo2,
            R.string.param4, R.string.param5, R.string.hemna_bp};

    public static final int[] PARAM_UNIT = new int[]{
            R.string.unit_beats_min, R.string.unit_per,
            R.string.unit_mmhg, R.string.unit_mmhg, R.string.unit_mg_dl, R.string.unit_g_dl,
            R.string.unit_mmhg, R.string.unit_mmhg, R.string.unit_per, R.string.unit_per,
            R.string.unit_l_min, R.string.unit_mmhg, R.string.unit_not_sure, R.string.unit_mmol_l,
            R.string.unit_m_ul, R.string.unit_ml_beat, R.string.unit_per, R.string.unit_ml_dl,
            R.string.unit_109_l, R.string.unit_109_l, R.string.unit_meq_l, R.string.unit_meq_l,
            R.string.unit_mg_dl, R.string.unit_meq_l, R.string.unit_mg_dl, R.string.unit_mg_dl,
            R.string.unit_per, R.string.unit_cm_sec, R.string.unit_mmol_l, R.string.unit_per,
            R.string.unit_not_sure, R.string.unit_not_sure, R.string.unit_mmhg};

    private WaveChartFragment mECGChartFragment;

    private WaveChartFragment mBPChartFragment;

    private WaveChartFragment mPPWChartFragment;

    private MeasurementGroupCards mMeasurementGroupCards;

    private WaveChartFragmentPagerAdapter mWaveChartFragmentPagerAdapter;

    private TextView mLineChartTitleTV;

    private ViewPager mChartViewPager;

    private View mMeasurementView;

    private CnogaMeasurementManager mCnogaMeasurementManager;

    private String[] lineChartTitles;

    private ArrayList<Fragment> mChartFragmentList;

    private boolean isMeasuring = false;

    private LoadingDialog mLoadingDialog;

    private MyHandler mHandler = new MyHandler(MeasurementFragment.this);

    private static class MyHandler extends Handler {
        WeakReference<MeasurementFragment> mFragment;

        MyHandler(MeasurementFragment fragment) {
            mFragment = new WeakReference<MeasurementFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MeasurementFragment fragment = mFragment.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case ON_MEASURE_FINISH:
                    fragment.isMeasuring = false;
                    removeMessages(ON_MEASURE_FINISH);
                    break;
                case ON_UPLOAD_FINISH:
                    removeMessages(ON_UPLOAD_FINISH);
                    break;
                case ON_DATA_AVAILABLE:
                    fragment.onOneDataAvailable(msg.arg1);
                    break;
                case MEASURE_NO_DATA_RECONNECT:
                    fragment.isMeasuring = false;
                    fragment.showMeasurementInterruptDialog(MeasurementConstants.MEASUREMENT_ERROR_NO_DATA);
                    break;
                case MEASURE_GOT_DATA_RECONNECT:
                    fragment.isMeasuring = false;
                    fragment.showMeasurementInterruptDialog(MeasurementConstants.MEASUREMENT_ERROR_DATA_INTERRUPTION);
                    break;
                case MEASURE_LESS_DATA:
                    fragment.showMeasurementInterruptDialog(MeasurementConstants.MEASUREMENT_ERROR_LESS_DATA);
                    break;
                case MEASURE_PARSE_DATA_ERROR:
                    fragment.showMeasurementInterruptDialog(MeasurementConstants.MEASUREMENT_ERROR_PARSE_DATA);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMeasurementView = inflater.inflate(R.layout.fragment_measurement, container, false);
        initialize(mMeasurementView);
        return mMeasurementView;
    }

    private void initialize(View view) {
        lineChartTitles = new String[]{getResources().getString(R.string.chart_title_ecg),
                getResources().getString(R.string.chart_title_bp),
                getResources().getString(R.string.chart_title_ppw)};
        mLineChartTitleTV = (TextView) view.findViewById(R.id.activity_measurement_tv_chart_name);

        mCnogaMeasurementManager = CnogaMeasurementManager.getInstance(getContext());
        mCnogaMeasurementManager.setMeasurementListener(this);

        mMeasurementGroupCards = (MeasurementGroupCards) view.findViewById(R.id.activity_measurement_el_item);
        mMeasurementGroupCards.setOnParamClickListener(this);

        if (mECGChartFragment == null) {
            mECGChartFragment = new WaveChartFragment();
            // mECGChartFragment.setWaveLineColor(Color.RED);
            // mECGChartFragment.setWaveLineWidth(2.5F);
            mECGChartFragment.setWaveType(MeasurementConstants.CHART_TYPE_ECG);
        }
        if (mBPChartFragment == null) {
            mBPChartFragment = new WaveChartFragment();
            mBPChartFragment.setWaveType(MeasurementConstants.CHART_TYPE_BP);
        }
        if (mPPWChartFragment == null) {
            mPPWChartFragment = new WaveChartFragment();
            mPPWChartFragment.setWaveType(MeasurementConstants.CHART_TYPE_PPW);
        }
        mECGChartFragment.setWaveViewMode(MeasurementConstants.MODE_MEASUREMENT);
        mBPChartFragment.setWaveViewMode(MeasurementConstants.MODE_MEASUREMENT);
        mPPWChartFragment.setWaveViewMode(MeasurementConstants.MODE_MEASUREMENT);

        if (mChartFragmentList == null) {
            mChartFragmentList = new ArrayList<>();
            mChartFragmentList.add(mECGChartFragment);
            mChartFragmentList.add(mBPChartFragment);
            mChartFragmentList.add(mPPWChartFragment);
        }
        mWaveChartFragmentPagerAdapter = new WaveChartFragmentPagerAdapter(getFragmentManager(),
                mChartFragmentList);
        mChartViewPager = (ViewPager) view.findViewById(R.id.activity_measurement_vp_chart_change);
        mChartViewPager.setOffscreenPageLimit(2);
        mChartViewPager.setOnPageChangeListener(this);
        mChartViewPager.setAdapter(mWaveChartFragmentPagerAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CnogaDeviceManager.getInstance(getContext()).isDeviceConnected() && !isMeasuring) {
            if (!mMeasurementGroupCards.isInitialized()) {
                byte[] capabilities = AppDeviceManager.getInstance(getContext()).getCapabilities();
                if (capabilities != null) {
                    mMeasurementGroupCards.init(capabilities);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            destroyVariables();
        } catch (Throwable t) {
            Log.w(TAG, "destroyVariables " + t.getMessage());
            // t.printStackTrace();
        }
    }

    public boolean isMeasuring() {
        return isMeasuring;
    }

    private void onOneDataAvailable(int count) {
        if (!mMeasurementGroupCards.isInitialized()) {
            byte[] capabilities = AppDeviceManager.getInstance(getContext()).getCapabilities();
            if (capabilities != null) {
                mMeasurementGroupCards.init(capabilities);
            }
        }
    }


    /**
     * 当测量结束（或中断），离开测量界面时，初始话界面上的测量数据．
     */
    private void initializeMeaData() {
        mMeasurementGroupCards.notifyDataSetChanged();
    }

    /**
     * clear variables
     */
    private void destroyVariables() throws Throwable {
        mCnogaMeasurementManager.unRegMeasurementListener(this);
        if (mChartFragmentList != null) {
            mChartFragmentList.clear();
            mChartFragmentList = null;
        }
        mWaveChartFragmentPagerAdapter.clear();
        mChartViewPager.removeAllViews();
        mChartViewPager.removeAllViewsInLayout();
        mMeasurementGroupCards = null;
        if (getActivity() != null) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.detach(mECGChartFragment);
            fragmentTransaction.remove(mECGChartFragment);
            fragmentTransaction.detach(mBPChartFragment);
            fragmentTransaction.remove(mBPChartFragment);
            fragmentTransaction.detach(mPPWChartFragment);
            fragmentTransaction.remove(mPPWChartFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
        mECGChartFragment = null;
        mBPChartFragment = null;
        mPPWChartFragment = null;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        mLineChartTitleTV.setText(lineChartTitles[position]);
    }

    @Override
    public void onParamClick(int type) {
        //click button more time
        Intent intent = new Intent();
        intent.putExtra(MeasurementParamActivity.PARAM_TYPE, type);
        intent.setClass(getContext(), MeasurementParamActivity.class);
        // access only  on measuring circumstance.
        if (isMeasuring) {
            startActivity(intent);
        }
    }

    @Override
    public void onDataAvailable(int count) {
        Message msg = mHandler.obtainMessage();
        msg.what = ON_DATA_AVAILABLE;
        msg.arg1 = count;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onConnectionStatusChanged(int connection) {
        if (connection == DeviceConstant.DEVICE_STATUS_CONNECTED) {
            if (isResumed()) {
                makeToast(getContext(), getString(R.string.connection_success));
            }
        } else {
            if (isMeasuring) {
                makeToast(getContext(), getString(R.string.measure_ble_disconnect_content));
                isMeasuring = false;
            } else {
                if (connection == DeviceConstant.DEVICE_STATUS_CONNECT_FAIL && isResumed()) {
                    makeToast(getContext(), getString(R.string.connection_failed));
                } else if (connection == DeviceConstant.DEVICE_STATUS_DISCONNECTED) {
                    makeToast(getContext(), getString(R.string.connection_disconnected));
                }
            }
        }
    }

    @Override
    public void onDeviceStatusChanged(DeviceStatus status) {
        switch (status.getMedialStatus()) {
            case DeviceStatus.MEDICAL_STATUS_IDLE:
                Log.e(TAG, " 接收到测量停止标志");
                isMeasuring = false;
                // data store end
                break;
            case DeviceStatus.MEDICAL_STATUS_MEASURING:
                Log.e(TAG, " 接收到测量开始标志");
                //只要开始测量，就把跳转到Record的限制给解禁．
                isMeasuring = true;
                // get the device type
                if (!mMeasurementGroupCards.isInitialized()) {
                    byte[] capabilities = AppDeviceManager.getInstance(getContext()).getCapabilities();
                    if (capabilities != null) {
                        mMeasurementGroupCards.init(capabilities);
                    }
                }
                break;
        }
    }

    @Override
    public void onMeasurementFinished() {
        if (getActivity() != null) {
            initializeMeaData();
            int count = mCnogaMeasurementManager.getDataCount();
            HashMap<Integer, Object> map = mCnogaMeasurementManager.getMeasurementParamData(count);
            Map<Integer, Object> treeMap = new TreeMap<>(map);
            ArrayList<Param> Parameters = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();
            for (int key : treeMap.keySet()) {
                if (((CnogaDeviceManager.getInstance(getContext()).getDeviceType() == DeviceConstant.DEVICE_TYPE_VSM)
                        && (isVsmParam(key))) || (CnogaDeviceManager.getInstance(getContext()).getDeviceType() == DeviceConstant.DEVICE_TYPE_MTX)) {
                    Param param = new Param();
                    param.setUnits(getString(PARAM_UNIT[key]));
                    param.setContent(getString(PARAM_NAME[key]));
                    param.setName(getString(PARAM_ABBR[key]));
                    param.setValue(treeMap.get(key).toString());
                    Log.i(TAG, "onMeasurementFinished: "+param.getName()+"-------"+param.getValue());
                    try {
                        jsonObject.put(param.getName(),param.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Parameters.add(param);
                }
            }
            APIModuleGnoga.mModuleContext.success(jsonObject,true);
            showMeasurement(Parameters);
        }
    }

    @Override
    public void onUploadFinished(int code, String measurementId) {
        Message msg = mHandler.obtainMessage(ON_UPLOAD_FINISH);
        msg.arg1 = code;
        msg.obj = measurementId;
        mHandler.sendMessage(msg);
    }


    public void showMeasurement(ArrayList<Param> arrayListParams) {
        final Dialog dialog = new Dialog(getContext(), R.style.full_screen_dialog);
        dialog.setContentView(R.layout.params_show);
        RecyclerView listParams = (RecyclerView) dialog.findViewById(R.id.params_list);
        MeasurementItemAdapter mAdapter;

        mAdapter = new MeasurementItemAdapter(arrayListParams);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listParams.setLayoutManager(mLayoutManager);
        listParams.setItemAnimator(new DefaultItemAnimator());
        listParams.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        listParams.setAdapter(mAdapter);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDismiss(DialogInterface dialog) {
                mHandler.sendEmptyMessage(ON_MEASURE_FINISH);
                isMeasuring = false;
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    @Override
    public void onMeasurementInterrupted(int type) {
        isMeasuring = false;
        if (type == MeasurementConstants.MEASUREMENT_ERROR_LESS_DATA) {
            mHandler.sendEmptyMessage(MEASURE_LESS_DATA);
        } else if (type == MeasurementConstants.MEASUREMENT_ERROR_DATA_INTERRUPTION) {
            mHandler.sendEmptyMessage(MEASURE_GOT_DATA_RECONNECT);
        } else if (type == MeasurementConstants.MEASUREMENT_ERROR_NO_DATA) {
            mHandler.sendEmptyMessage(MEASURE_NO_DATA_RECONNECT);
        } else if (type == MeasurementConstants.MEASUREMENT_ERROR_PARSE_DATA) {
            mHandler.sendEmptyMessage(MEASURE_PARSE_DATA_ERROR);
        }
    }

    /**
     * 当测量过程中，出现通讯异常时，以对话框的形式提示用户尝试重新连接设备．
     *
     * @param type,出现异常情况的类型
     */
    private void showMeasurementInterruptDialog(int type) {
        makeToast(getActivity(), "ConnectionInterrupt type:" + type);
    }

    private void makeToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isMeasuring = false;
        mCnogaMeasurementManager.clearMeasurement();
    }

    private boolean isVsmParam(int position) {
        switch (position) {
            case MeasurementConstants.PARAM_TYPE_OXYGEN_SATURATION:
            case MeasurementConstants.PARAM_TYPE_SYSTOLIC_BLOOD_PRESSURE:
            case MeasurementConstants.PARAM_TYPE_DIASTOLIC_BLOOD_PRESSURE:
            case MeasurementConstants.PARAM_TYPE_HEART_RATE:
                return true;
            default:
                return false;
        }
    }

    ;


}