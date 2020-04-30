package com.alpha.modulegnoga.device;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.modulegnoga.R;
import com.alpha.modulegnoga.common.BaseActivity;
import com.alpha.modulegnoga.common.CancelableAlertDialog;
import com.alpha.modulegnoga.common.RecyclerViewDivider;
import com.alpha.modulegnoga.measurement.MeasurementActivity;
import com.alpha.modulegnoga.utils.DeviceVersionDialog;
import com.cnoga.singular.mobile.sdk.common.utils.Loglog;
import com.cnoga.singular.mobile.sdk.common.utils.ThreadPool;
import com.cnoga.singular.mobile.sdk.constants.DeviceConstant;
import com.cnoga.singular.mobile.sdk.device.CnogaDeviceManager;
import com.cnoga.singular.mobile.sdk.device.IOnLeScanListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public class DeviceActivity extends BaseActivity
        implements View.OnClickListener, IOnLeScanListener, AppDeviceManager.AppDeviceManagerListener,
        DeviceAdapter.OnRecyclerViewListener {

    private static String TAG = "DeviceActivity";

    private static final int REQUEST_ENABLE_BT = 1001;

    private static final int HANDLE_CONNECTION_TIMEOUT = 1004;

    private static final int GET_PAIRING_CODE_SUCCESS = 1005;

    private static final int GET_PAIRING_CODE_FAILURE = 1006;

    private static final long CONNECTION_TIMEOUT_INTERVAL = 12 * 1000;

    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1005;


    private String mConnectAddress;

    public Button mScanBtn, deviceBleBtn, openBleBtn;

    ProgressBar progressBar, deviceTypeProgressBar, pairingCodeProgressBar, isConnectedProgressBar;

    CancelableAlertDialog mDisconnectDialog;

    RecyclerView mAvailableLv;

    private DeviceAdapter mAvailableAdapter;

    CnogaDevice connectedDevice;

    private ArrayList<CnogaDevice> mAvailableList;

    private DeviceHandler mHandler = new DeviceHandler(DeviceActivity.this);

    private static AppDeviceManager mAppDeviceManager;

    Context mContext;

    TextView measureBtn;
    private String pairingCode = "";
    private String deviceType = "";
    Button disconnectBtn;
    private TextView pairingCodeTextView, deviceTypeTextView, isConnectedTextView;
    CardView connectedDeviceLayout;
    private TextView deviceNameTextView, deviceAddressTextView;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.scan_btn) {
            scanLeDevice();

        } else if (i == R.id.device_ble_btn) {
            boolean isCheckBle = mAppDeviceManager.checkSupportBLE();
            if (isCheckBle) {
                Toast.makeText(this, "BLE supported.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "BLE is not supported.", Toast.LENGTH_LONG).show();
            }

        } else if (i == R.id.device_open_ble) {
            if (!mAppDeviceManager.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Toast.makeText(this, "BLE is Enabled.", Toast.LENGTH_LONG).show();
            }

        } else if (i == R.id.measure_btn) {
            onMeasureClick();

        } else if (i == R.id.disconnect_btn) {
            if (mAppDeviceManager != null) {

                pairingCodeTextView.setText("");
                deviceTypeTextView.setText("");
                isConnectedTextView.setText("");

                mAppDeviceManager.disConnect();
                connectedDeviceLayout.setVisibility(View.GONE);
                refreshListData();
            }

        }
    }

    @Override
    public void showDeviceWarning() {
        dismissLoadingDialog();

        final DeviceVersionDialog versionDialog = new DeviceVersionDialog(this);

        if (!this.isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    versionDialog.show();
                }
            });
        }
    }

    @Override
    public void onDeviceConnected() {
        if (mAppDeviceManager != null) {
            showInfoDialog();
            refreshListData();
        }
    }

    @Override
    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public void showBluetoothError(int error_bluetooth) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAlertDialog(R.string.dialog_title_warning, R.string.error_bluetooth, false);
            }
        });
    }


    private class DeviceHandler extends Handler {
        WeakReference<DeviceActivity> mActivity;

        DeviceHandler(DeviceActivity activity) {
            mActivity = new WeakReference<DeviceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DeviceActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_CONNECTION_TIMEOUT:
                    mAppDeviceManager.connect(mConnectAddress);
                    refreshListData();
                    break;
                case GET_PAIRING_CODE_SUCCESS:
                    pairingCode = (String) msg.obj;
                    showInfoDialog();
                    break;
                case GET_PAIRING_CODE_FAILURE:
                    showInfoDialog();
                    Toast.makeText(activity, "get pairing code fail", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    private View.OnClickListener mOnDisconnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Remove last connected devise so it won't reconnected next time
            mAppDeviceManager.disConnect();
            refreshListData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        mContext = this.getApplicationContext();
        mAppDeviceManager = AppDeviceManager.getInstance(mContext);
        mAppDeviceManager.init(getApplication(), this);

        if (mAppDeviceManager == null) {
            TextView textView = findViewById(R.id.license_expired_text_view);
            textView.setVisibility(View.VISIBLE);
            return;
        }

        mAvailableList = new ArrayList<CnogaDevice>();

        deviceBleBtn = (Button) findViewById(R.id.device_ble_btn);
        deviceBleBtn.setOnClickListener(this);

        openBleBtn = (Button) findViewById(R.id.device_open_ble);
        openBleBtn.setOnClickListener(this);

        mScanBtn = (Button) findViewById(R.id.scan_btn);
        mScanBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        deviceTypeProgressBar = (ProgressBar) findViewById(R.id.device_type_progress_bar);
        pairingCodeProgressBar = (ProgressBar) findViewById(R.id.pairing_code_progress_bar);
        isConnectedProgressBar = (ProgressBar) findViewById(R.id.is_connected_progress_bar);

        mAvailableLv = (RecyclerView) findViewById(R.id.device_available_lv);
        mAvailableAdapter = new DeviceAdapter(mContext, mAvailableList);
        mAvailableAdapter.setOnRecyclerViewListener(this);
        mAvailableLv.setAdapter(mAvailableAdapter);
        mAvailableLv.setLayoutManager(new LinearLayoutManager(this));
        mAvailableLv.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL));

        measureBtn = (TextView) findViewById(R.id.measure_btn);
        measureBtn.setOnClickListener(this);

        deviceAddressTextView = (TextView) findViewById(R.id.device_address_tv);
        deviceNameTextView = (TextView) findViewById(R.id.device_name);

        pairingCodeTextView = (TextView) findViewById(R.id.pairing_code);
        deviceTypeTextView = (TextView) findViewById(R.id.device_type);
        isConnectedTextView = (TextView) findViewById(R.id.is_connected);

        connectedDeviceLayout = (CardView) findViewById(R.id.connected_device_layout);
        disconnectBtn = (Button) findViewById(R.id.disconnect_btn);
        disconnectBtn.setOnClickListener(this);

        refreshListData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mAppDeviceManager.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        mAppDeviceManager.regLeScanListener(this);
        scanLeDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppDeviceManager.unRegLeScanListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            finish();
        }
    }

    @Override
    public void onDeviceClick(CnogaDevice device) {
        if (!device.isAvailable()) {
            return;
        }
        if (!mAppDeviceManager.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        pairingCodeProgressBar.setVisibility(View.VISIBLE);
        deviceAddressTextView.setVisibility(View.VISIBLE);
        isConnectedProgressBar.setVisibility(View.VISIBLE);
        pairingCodeTextView.setText("");
        deviceTypeTextView.setText("");
        isConnectedTextView.setText("");

        mConnectAddress = device.getAddress();

        if (mAppDeviceManager.getConnectedDeviceAddress().equalsIgnoreCase(mConnectAddress)) {

            mDisconnectDialog = new CancelableAlertDialog(this, null, getString(R.string.disconnect_confirmation),
                    null, null, mOnDisconnectListener);
            mDisconnectDialog.show();
        } else {
            mAppDeviceManager.disConnect();
            connectDevice(mConnectAddress);
            showLoadingDialog(getString(R.string.loading), true);
        }
    }

    public void onMeasureClick() {
        Intent intent = new Intent();
        intent.setClass(this, MeasurementActivity.class);
        startActivity(intent);
    }

    public void showInfoDialog() {
        deviceType = "UNKNOWN";

        int intType = mAppDeviceManager.getDeviceType();

        if (intType == DeviceConstant.DEVICE_TYPE_MTX) {
            deviceType = "MTX";
        } else if (intType == DeviceConstant.DEVICE_TYPE_VSM) {
            deviceType = "VSM";
        }

        pairingCode = mAppDeviceManager.getPairingCode(mConnectAddress);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!pairingCode.isEmpty()) {
                    pairingCodeProgressBar.setVisibility(View.GONE);
                    pairingCodeTextView.setText(pairingCode);
                }

                if (!deviceType.equals("UNKNOWN")) {
                    deviceTypeTextView.setText(deviceType);
                    deviceTypeProgressBar.setVisibility(View.GONE);
                }

                isConnectedProgressBar.setVisibility(View.GONE);
                isConnectedTextView.setText(mAppDeviceManager.isDeviceConnected() ? "Yes" : "No");
            }
        });
    }

//
//    private void getPairingCode(final String address) {
//        if (mAppDeviceManager.isDeviceConnected()
//                && mAppDeviceManager.getConnectedDeviceAddress().equalsIgnoreCase(address)) {
//            ThreadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    String pairingCode = mAppDeviceManager.getPairingCode(address);
//                    if ((pairingCode != null) && (!pairingCode.isEmpty())) {
//                        Message msg = new Message();
//                        msg.what = GET_PAIRING_CODE_SUCCESS;
//                        msg.obj = pairingCode;
//                        mHandler.sendMessage(msg);
//                    } else {
//                        mHandler.sendEmptyMessage(GET_PAIRING_CODE_FAILURE);
//                    }
//                }
//            });
//        }
//    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        refreshListData();
    }

    private void connectDevice(String address) {
        if (mAppDeviceManager == null)
            return;

        mHandler.removeMessages(HANDLE_CONNECTION_TIMEOUT);
        mAppDeviceManager.connect(address);
        Loglog.i(TAG, "address: " + address);
        mHandler.sendEmptyMessageDelayed(HANDLE_CONNECTION_TIMEOUT, CONNECTION_TIMEOUT_INTERVAL);

        pairingCode = "";
        deviceType = "";
        deviceTypeProgressBar.setVisibility(View.VISIBLE);
        pairingCodeProgressBar.setVisibility(View.VISIBLE);
        isConnectedProgressBar.setVisibility(View.VISIBLE);
    }

    private void scanLeDevice() {
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mAppDeviceManager.scanLeDevice(true);
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, CnogaDeviceManager.SCAN_PERIOD);
    }


    private void refreshListData() {
        if (mAppDeviceManager == null)
            return;

        CopyOnWriteArrayList<BluetoothDevice> allDevices = mAppDeviceManager.getDevices();
        if (allDevices != null) {
            connectedDevice = null;
            mAvailableList.clear();

            String connectedAddress = mAppDeviceManager.getConnectedDeviceAddress();

            for (BluetoothDevice device : allDevices) {
                CnogaDevice cnogaDevice = new CnogaDevice(device.getAddress(), device.getName(), true, false);

                if (/*mAppDeviceManager.isConnected &&*/ cnogaDevice.getAddress().equalsIgnoreCase(connectedAddress)) {
                    connectedDevice = cnogaDevice;
                } else {
                    mAvailableList.add(cnogaDevice);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAvailableAdapter.setDevices(mAvailableList);

                        if (connectedDevice != null) {
                            connectedDeviceLayout.setVisibility(View.VISIBLE);
                            deviceNameTextView.setText(connectedDevice.getName());
                            deviceAddressTextView.setText(connectedDevice.getAddress());
                        } else {
                            connectedDeviceLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    protected void onDestroy() {
        mAppDeviceManager.onDestroy();
        super.onDestroy();
    }
}