package com.alpha.modulegnoga.device;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.alpha.modulegnoga.R;
import com.cnoga.singular.mobile.sdk.common.utils.Loglog;
import com.cnoga.singular.mobile.sdk.constants.BaseConstant;
import com.cnoga.singular.mobile.sdk.constants.DeviceConstant;
import com.cnoga.singular.mobile.sdk.device.CnogaDeviceManager;
import com.cnoga.singular.mobile.sdk.device.IOnLeScanListener;
import com.cnoga.singular.mobile.sdk.device.IOnUpdateDeviceConnectionListener;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


public class AppDeviceManager implements IOnUpdateDeviceConnectionListener {

    private static final String TAG = "AppDeviceManager";

    private static AppDeviceManager sInstance;

    private CnogaDeviceManager mCnogaDeviceManager;

    private Application mApplication;


    private AppDeviceManagerListener listener;


    /**
     * AppDeviceManager = Constructor
     *
     * @param context
     */
    private AppDeviceManager(Context context) {
        mCnogaDeviceManager = CnogaDeviceManager.getInstance(context);
    }

    /**
     * Obtain the AppDeviceManager instance
     *
     * @param context context
     * @return Instance
     */
    public static AppDeviceManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppDeviceManager(context);
        }
        return sInstance;
    }

    /**
     * init
     *
     * @param application
     */
    public void init(Application application, AppDeviceManagerListener listener) {
        mApplication = application;
        mCnogaDeviceManager.regOnUpdateDeviceConnectionListener(this);
        this.listener = listener;
    }

    /**
     * getAvailableDevices
     *
     * @return ArrayList of all available devices
     */
    public ArrayList<CnogaDevice> getAvailableDevices() {
        ArrayList<CnogaDevice> availableDevices = new ArrayList<>();
        CopyOnWriteArrayList<BluetoothDevice> bluetoothDevices = mCnogaDeviceManager.getDevices();

        for (BluetoothDevice device : bluetoothDevices) {
            CnogaDevice cnogaDevice = new CnogaDevice(device.getAddress(), device.getName(), true, false);
            availableDevices.add(cnogaDevice);
        }

        return availableDevices;
    }

    /**
     * getDeviceType
     *
     * @return device type
     */
    public int getDeviceType() {
        return mCnogaDeviceManager.getDeviceType();
    }

    /**
     * checkSupportBLE
     *
     * @return is ble supported (true/false)
     */
    public boolean checkSupportBLE() {
        return mCnogaDeviceManager.checkSupportBLE();
    }

    /**
     * isEnabled
     * Check if bluetooth is enabled
     *
     * @return isEnabled
     */
    public boolean isEnabled() {
        return mCnogaDeviceManager.isEnabled();
    }

    /**
     * scanLeDevice
     * Scan ble device
     *
     * @param enable Start:true/Stop:false
     */
    public void scanLeDevice(final boolean enable) {
        mCnogaDeviceManager.scanLeDevice(enable);
    }

    /**
     * CopyOnWriteArrayList
     * Obtain device list for UI, we create a deep copy here
     *
     * @return device list
     */
    public CopyOnWriteArrayList<BluetoothDevice> getDevices() {
        return mCnogaDeviceManager.getDevices();
    }

    /**
     * regLeScanListener
     * Register ble scan listener
     *
     * @param ls Listener
     */
    public void regLeScanListener(IOnLeScanListener ls) {
        mCnogaDeviceManager.regLeScanListener(ls);
    }

    /**
     * 解除扫描监听器
     * Unregister ble scan listener
     *
     * @param ls Listener
     */
    public void unRegLeScanListener(IOnLeScanListener ls) {
        mCnogaDeviceManager.unRegLeScanListener(ls);
    }

    /**
     * isDeviceConnected
     * Check if the device is connected
     *
     * @return Connected
     */
    public boolean isDeviceConnected() {
        return mCnogaDeviceManager.isDeviceConnected();
    }

    /**
     * getConnectedDeviceAddress
     * Obtain the connected device address
     *
     * @return Device address, return "" not connected
     */
    public String getConnectedDeviceAddress() {
        return mCnogaDeviceManager.getConnectedDeviceAddress();
    }

    /**
     * connect
     * Connect to the specific device
     *
     * @param address Device mac address
     */
    public void connect(String address) {
        mCnogaDeviceManager.connect(address);
    }

    /**
     * getPairingCode
     * get pair code
     *
     * @return
     */
    public String getPairingCode(String deviceAddress) {
        return mCnogaDeviceManager.getPairingCode(deviceAddress);
    }

    /**
     * Disconnect from connected device
     */
    public void disConnect() {
        mCnogaDeviceManager.setFullConnected(false);
        mCnogaDeviceManager.disConnect();
    }


    /**
     * onUpdateDeviceStatus = callback
     *
     * @param connection
     */
    @Override
    public void onUpdateDeviceStatus(int connection) {

        listener.dismissDialog();

        switch (connection) {
            case DeviceConstant.DEVICE_STATUS_CONNECTED:
                String connectedAddress = mCnogaDeviceManager.getConnectedDeviceAddress();
                mCnogaDeviceManager.setFullConnected(true);
                if (listener != null) {
                    mCnogaDeviceManager.getPairingCode(connectedAddress);
                    listener.onDeviceConnected();
                }
                break;
            case DeviceConstant.DEVICE_STATUS_DISCONNECTED:
                if (BaseConstant.IS_DEBUG) {
                    Loglog.e(TAG, "Device disconnected.");
                }
                break;
            case DeviceConstant.DEVICE_STATUS_CONNECT_FAIL:
                if (BaseConstant.IS_DEBUG)
                    Loglog.e(TAG, "Device disconnected.");
                listener.showBluetoothError(R.string.error_bluetooth);
                break;

            case DeviceConstant.DEVICE_VERSION_NO_CAPABILITIES:
                listener.showDeviceWarning();
                break;
        }
    }

    /**
     * onDestroy - called when listener is destroyed.
     */
    public void onDestroy() {
        mCnogaDeviceManager.disConnect();
        mCnogaDeviceManager.unRegDeviceConnectionListener(this);
    }

    public byte[] getCapabilities() {
        return mCnogaDeviceManager.getMeasurementCapabilities();
    }

    public interface AppDeviceManagerListener {

        void showDeviceWarning();

        void onDeviceConnected();

        void dismissDialog();

        void showBluetoothError(int error_bluetooth);
    }
}