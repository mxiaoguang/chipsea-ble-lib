package com.chipsea.bleprofile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.chipsea.entity.BodyFatData;
import com.chipsea.entity.BroadData;
import com.chipsea.entity.CsFatScale;
import com.chipsea.healthscale.CsAlgoBuilderEx;
import com.chipsea.healthscale.ScaleActivateStatusEvent;
import com.chipsea.utils.BleConfig;
import com.chipsea.utils.CsAlgoBuilderExUtil;
import com.chipsea.utils.L;
import com.chipsea.utils.ParseData;
import com.chipsea.wby.WBYService;

import static android.Manifest.permission.BLUETOOTH_ADMIN;

/**
 * @ClassName:BleProfileServiceReadyActivity
 * @PackageName:com.chipsea.bleprofile
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public abstract class BleProfileServiceReadyActivity<E extends WBYService.WBYBinder> extends AppCompatActivity {

    private final static String TAG = BleProfileServiceReadyActivity.class.getSimpleName();

    protected static final int REQUEST_ENABLE_BT = 2;

    private E mService;

    private boolean mIsScanning = false;

    private BluetoothAdapter adapter = null;

    private BroadcastReceiver mCommonBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                bluetoothStateChanged(state);
            } else if (BleProfileService.ACTION_CONNECT_STATE_CHANGED.equals(action)) {
                int connectState = intent.getIntExtra(BleProfileService.EXTRA_CONNECT_STATE, -1);
                String address = intent.getStringExtra(BleProfileService.EXTRA_DEVICE_ADDRESS);
                onStateChanged(address, connectState);
            } else if (BleProfileService.ACTION_CONNECT_ERROR.equals(action)) {
                String errMsg = intent.getStringExtra(BleProfileService.EXTRA_ERROR_MSG);
                int errCode = intent.getIntExtra(BleProfileService.EXTRA_ERROR_CODE, -1);
                onError(errMsg, errCode);
            } else if (WBYService.ACTION_CSFAT_SCALE_DATA.equals(action)) {
                CsFatScale csFatScale = (CsFatScale) intent.getSerializableExtra(WBYService.EXTRA_CSFAT_SCALE_DATA);
                onWeightData(false, csFatScale);
            } else if (WBYService.ACTION_FAT_DATA.equals(action)) {
                BodyFatData bodyFatData = (BodyFatData) intent.getSerializableExtra(WBYService.EXTRA_FAT_DATA);
                boolean isHistory = intent.getBooleanExtra(WBYService.EXTRA_IS_HISTORY, false);
                onBodyFatData(isHistory, bodyFatData);
            }
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            final E bleService = mService = (E) service;
            onServiceBinded(bleService);
            // and notify user if device is connected
            if (bleService.isConnected())
                onStateChanged(bleService.getDeviceAddress(), BleProfileService.STATE_CONNECTED);
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            onServiceUnbinded();
        }
    };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onInitialize();
        bindService(null);
        getApplication().registerReceiver(mCommonBroadcastReceiver, makeIntentFilter());

    }

    protected void bindService(String address) {
        final Intent service = new Intent(this, WBYService.class);
        if (!TextUtils.isEmpty(address)) {
            service.putExtra(BleProfileService.EXTRA_DEVICE_ADDRESS, address);
            startService(service);
        }
        bindService(service, mServiceConnection, 0);
    }

    protected void onInitialize() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplication().unregisterReceiver(mCommonBroadcastReceiver);
        unbindService();
    }

    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BleProfileService.ACTION_CONNECT_STATE_CHANGED);
        intentFilter.addAction(BleProfileService.ACTION_CONNECT_ERROR);
        intentFilter.addAction(WBYService.ACTION_WEIGHT_DATA);
        intentFilter.addAction(WBYService.ACTION_CSFAT_SCALE_DATA);
        intentFilter.addAction(WBYService.ACTION_FAT_DATA);
        return intentFilter;
    }


    /**
     * 开始连接设备
     *
     * @param address
     */
    public void startConnect(String address) {
        stopScan();//连接设备时需停止扫描
        bindService(address);
    }

    /**
     * 是否已连接
     *
     * @return true:已连接; false:未连接
     */
    protected boolean isDeviceConnected() {
        return mService != null && mService.isConnected();
    }

    /**
     * 是否支持BLE
     *
     * @return true:支持; false:不支持
     */
    protected boolean ensureBLESupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 蓝牙是否开启
     *
     * @return true:开启; false:未开启
     */
    protected boolean isBLEEnabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    /**
     * 显示开启蓝牙提示框
     */
    protected void showBLEDialog() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
    }

    /**
     * 是否正在扫描
     *
     * @return true:正在扫描; false:已停止扫描
     */
    public boolean isScanning() {
        return mIsScanning;
    }

    /**
     * 开始扫描
     */
    @RequiresPermission(BLUETOOTH_ADMIN)
    protected void startScan() {
        if (isBLEEnabled()) {
            if (!mIsScanning) {
                adapter.startLeScan(mLEScanCallback);
                mIsScanning = true;
                handler.postDelayed(stopScanRunnable, SCAN_DURATION);
            }
        } else {
            showBLEDialog();
        }
    }

    private Handler handler = new Handler();

    private static final int SCAN_DURATION = 60 * 1000;

    private Runnable startScanRunnable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
            handler.post(startScanRunnable);
        }
    };

    /**
     * 停止扫描
     */
    @RequiresPermission(BLUETOOTH_ADMIN)
    protected void stopScan() {
        handler.removeCallbacks(startScanRunnable);
        handler.removeCallbacks(stopScanRunnable);
        if (mIsScanning) {
            if (adapter != null) {
                adapter.stopLeScan(mLEScanCallback);
            }
            mIsScanning = false;
        }
    }

    private final BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            L.e(TAG, "onLeScan");
            if (device != null) {
                L.e(TAG, "address: " + device.getAddress() + "; name: " + device.getName());
                L.e(TAG, ParseData.byteArr2Str(scanRecord));
                L.e(TAG, "name: " + device.getName() + "; address: " + device.getAddress());

                final BroadData bleInfo = BleConfig.getBroadData(device, rssi, scanRecord);
                if (bleInfo != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBLEDevice(bleInfo);
                        }
                    });
                }
            }
        }
    };


    @RequiresPermission(BLUETOOTH_ADMIN)
    protected void bluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_OFF:
                if (mService != null) {
                    mService.disconnect();
                }
                stopScan();
                break;
        }
    }

    /**
     * 连接状态改变
     *
     * @param deviceAddress 设备地址
     * @param state         状态
     * @see BleProfileService#STATE_CONNECTED
     * @see BleProfileService#STATE_DISCONNECTED
     * @see BleProfileService#STATE_INDICATION_SUCCESS
     * @see BleProfileService#STATE_SERVICES_DISCOVERED
     * @see BleProfileService#STATE_CONNECTED
     * @see BleProfileService#STATE_TIME_OUT
     */
    protected void onStateChanged(String deviceAddress, int state) {
        switch (state) {
            case BleProfileService.STATE_DISCONNECTED:
                unbindService();
                break;
            case BleProfileService.STATE_CONNECTED://成功连接蓝牙称
                /**
                 * =================请在绑定蓝牙秤后有网络连接的情况下调用SDK激活方法，激活成功后无须再调用此方法=======================
                 * SDK激活  SDK未激活有调用次数限制（500次），超过调用次数，所有人体成份计算接口返回结果0; 激活成功后无限制，如果App重装或者用户清除缓存需要重新激活
                 * 如果网络问题导致onHttpError,需要再次激活
                 * @param mac 蓝牙秤的mac地址，全部大写
                 *@param ScaleActivateStatusEvent 结果回调 此方法为异步方法，不会阻塞调用线程
                 */
                //SDK激活需要在Manifest.xml申明如下权限：
                //<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
                //<uses-permission android:name="android.permission.INTERNET" />
                //<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
                CsAlgoBuilderExUtil.initCsAlgoBuilder(getApplicationContext());
                CsAlgoBuilderEx csAlgoBuilderEx = CsAlgoBuilderExUtil.getAlgoBuilderEx();
                if (!csAlgoBuilderEx.getAuthStatus()) {
                    //激活方法，正式激活请将mac地址替换为蓝牙秤的蓝牙mac地址
                    csAlgoBuilderEx.Authorize(deviceAddress, new ScaleActivateStatusEvent() {
                        @Override
                        public void onActivateSuccess() {
                            L.i(TAG, "激活成功!");
                        }

                        @Override
                        public void onActivateFailed() {
                            //如果激活的mac地址激活次数过多，或者mac地址不是授权的蓝牙地址SDK会被冻结
                            L.i(TAG, "激活失败,SDK被冻结!");
                        }

                        @Override
                        public void onHttpError(int i, String s) {
                            L.i(TAG, "激活失败,需要重新激活,ErrCode:" + i);
                        }
                    });
                }
                break;
        }
    }

    private void unbindService() {
        try {
            unbindService(mServiceConnection);
            mService = null;
            onServiceUnbinded();
        } catch (final IllegalArgumentException e) {
            // do nothing. This should never happen but does...
        }
    }

    protected abstract void onServiceBinded(E binder);

    protected abstract void onServiceUnbinded();

    /**
     * 连接异常
     *
     * @param errMsg  错误信息
     * @param errCode 错误码
     */
    protected abstract void onError(String errMsg, int errCode);


    /**
     * 获取到符合Chipsea协议的体脂秤
     *
     * @param broadData
     */
    protected abstract void getBLEDevice(BroadData broadData);

    /**
     * 获取原始体重数据
     *
     * @param isHistory
     * @param csFatScale
     */
    protected abstract void onWeightData(boolean isHistory, CsFatScale csFatScale);

    /**
     * 获取体脂数据
     *
     * @param isHistory
     * @param bodyFatData
     */
    protected abstract void onBodyFatData(boolean isHistory, BodyFatData bodyFatData);
}
