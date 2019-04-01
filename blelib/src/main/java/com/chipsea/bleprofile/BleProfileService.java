package com.chipsea.bleprofile;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresPermission;

import com.chipsea.utils.L;

import static android.Manifest.permission.BLUETOOTH;

/**
 * @ClassName:BleProfileService
 * @PackageName:com.chipsea.bleprofile
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public abstract class BleProfileService extends Service implements BleManagerCallbacks {
    private static final String TAG = BleProfileService.class.getSimpleName();

    private ConnectHandler handler;

    private static final int CONNECT_TIME_OUT = 10 * 1000;
    private static final int MSG_CONNECT_TIME_OUT = 0;

    public static final String ACTION_CONNECT_STATE_CHANGED = "com.chipsea.action.CONNECT_STATE_CHANGED";
    public static final String EXTRA_CONNECT_STATE = "com.chipsea.extra.CONNECT_STATE";

    public static final String ACTION_CONNECT_ERROR = "com.chipsea.action.CONNECT_ERROR";
    public static final String EXTRA_ERROR_CODE = "com.chipsea.extra.ERROR_CODE";
    public static final String EXTRA_ERROR_MSG = "com.chipsea.extra.ERROR_MSG";

    public static final int STATE_CONNECTING = 4;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_SERVICES_DISCOVERED = 2;
    public static final int STATE_INDICATION_SUCCESS = 3;
    public static final int STATE_TIME_OUT = 5;

    /**
     * The parameter passed when creating the service. Must contain the address
     * of the sensor that we want to connect to
     */
    public static final String EXTRA_DEVICE_ADDRESS = "com.chipsea.extra.DEVICE_ADDRESS";

    private BleManager<BleManagerCallbacks> mBleManager;

    private boolean mConnected;
    private String mDeviceAddress;
    private String mDeviceName;

    private static class ConnectHandler extends Handler {

        private BleProfileService bleProfileService;
        private BleProfileService.LocalBinder binder;
        private String deviceAddress;

        private ConnectHandler(BleProfileService bleProfileService) {
            this.bleProfileService = bleProfileService;
            this.binder = bleProfileService.getBinder();
            this.deviceAddress = binder.getDeviceAddress();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CONNECT_TIME_OUT) {
                bleProfileService.sendStateChangedBroadcast(deviceAddress, STATE_TIME_OUT);
                binder.disconnect();
            }
        }
    }

    private void startConnectTimer() {
        cancelConnectTimer();
        handler.sendEmptyMessageDelayed(MSG_CONNECT_TIME_OUT, CONNECT_TIME_OUT);
    }

    private void cancelConnectTimer() {
        handler.removeMessages(MSG_CONNECT_TIME_OUT);
    }

    public class LocalBinder extends Binder {
        /**
         * Disconnects from the sensor.
         */
        public final void disconnect() {
            L.e(TAG, "disconnect mConnected = " + mConnected);
            if (!mConnected) {
                onDeviceDisconnected();
                return;
            }

            mBleManager.disconnect();
        }

        /**
         * Returns the device address
         *
         * @return device address
         */
        public String getDeviceAddress() {
            return mDeviceAddress;
        }

        /**
         * Returns the device name
         *
         * @return the device name
         */
        public String getDeviceName() {
            return mDeviceName;
        }

        /**
         * Returns <code>true</code> if the device is connected to the sensor.
         *
         * @return <code>true</code> if device is connected to the sensor,
         * <code>false</code> otherwise
         */
        public boolean isConnected() {
            return mConnected;
        }

        public BleProfileService getService() {
            return BleProfileService.this;
        }

    }

    @Override
    public IBinder onBind(final Intent intent) {
        return getBinder();
    }

    /**
     * Returns the binder implementation. This must return class implementing
     * the additional manager interface that may be used in the binded activity.
     *
     * @return the service binder
     */
    protected LocalBinder getBinder() {
        // default implementation returns the basic binder. You can overwrite
        // the LocalBinder with your own, wider implementation
        return new LocalBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // we must allow to rebind to the same service
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the manager
        mBleManager = initializeManager();
        mBleManager.setGattCallbacks(this);
        handler = new ConnectHandler(this);
    }

    @SuppressWarnings("rawtypes")
    protected abstract BleManager initializeManager();

    @Override
    @RequiresPermission(BLUETOOTH)
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        L.e(TAG, "BleProfileService onStartCommand!");
        if (intent == null || !intent.hasExtra(EXTRA_DEVICE_ADDRESS))
            throw new UnsupportedOperationException("No device address at EXTRA_DEVICE_ADDRESS key");

        mDeviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);

        sendStateChangedBroadcast(mDeviceAddress, STATE_CONNECTING);
        startConnectTimer();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();
        final BluetoothDevice device = adapter.getRemoteDevice(mDeviceAddress);
        mDeviceName = device.getName();
        onServiceStarted();
        L.e(TAG, "mConnected = " + mConnected);
        if (!mConnected) {
            mBleManager.connect(getApplicationContext(), device);
        }else {
            cancelConnectTimer();
        }
        return START_NOT_STICKY;
    }

    /**
     * Called when the service has been started. The device name and address are
     * set. It nRF Logger is installed than logger was also initialized.
     */
    protected void onServiceStarted() {
        // empty default implementation
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.e(TAG, "Service onDestroy!");
        // shutdown the manager
        mBleManager.closeBluetoothGatt();
        mBleManager = null;
        mDeviceAddress = null;
        mDeviceName = null;
        mConnected = false;
        cancelConnectTimer();
    }

    @Override
    public void onDeviceConnected() {
        L.e(TAG, "onDeviceConnected!");
        cancelConnectTimer();
        mConnected = true;
        sendStateChangedBroadcast(mDeviceAddress, STATE_CONNECTED);
    }

    @Override
    public void onDeviceDisconnected() {
        L.e(TAG, "onDeviceDisconnected!");
        sendStateChangedBroadcast(mDeviceAddress, STATE_DISCONNECTED);
        mConnected = false;
        mDeviceAddress = null;
        mDeviceName = null;
        // user requested disconnection. We must stop the service
        stopSelf();
    }

    @Override
    public void onServicesDiscovered() {
        L.e(TAG, "onServicesDiscovered!");
        sendStateChangedBroadcast(mDeviceAddress, STATE_SERVICES_DISCOVERED);
    }

    @Override
    public void onError(final String message, final int errorCode) {
        L.e(TAG, "onError message = " + message + ", errorcode = " + errorCode);
        sendErrorBroadcast(message, errorCode);
        onDeviceDisconnected();
    }

    @Override
    public void onIndicationSuccess() {
        L.e(TAG, "onIndicationSuccess!");
        sendStateChangedBroadcast(mDeviceAddress, STATE_INDICATION_SUCCESS);
    }

    private void sendStateChangedBroadcast(String address, int state) {
        Intent stateChangedBroadcast = new Intent(ACTION_CONNECT_STATE_CHANGED);
        stateChangedBroadcast.putExtra(EXTRA_DEVICE_ADDRESS, address);
        stateChangedBroadcast.putExtra(EXTRA_CONNECT_STATE, state);
        stateChangedBroadcast.setPackage(getPackageName());
        sendBroadcast(stateChangedBroadcast);
    }

    private void sendErrorBroadcast(String errMsg, int errCode) {
        Intent errorBroadcast = new Intent(ACTION_CONNECT_ERROR);
        errorBroadcast.setPackage(getPackageName());
        errorBroadcast.putExtra(EXTRA_ERROR_MSG, errMsg);
        errorBroadcast.putExtra(EXTRA_ERROR_CODE, errCode);
        sendBroadcast(errorBroadcast);
    }
}
