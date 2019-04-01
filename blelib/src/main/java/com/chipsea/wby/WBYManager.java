package com.chipsea.wby;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.chipsea.bleprofile.BleManager;
import com.chipsea.entity.BroadData;
import com.chipsea.entity.CsFatScale;
import com.chipsea.entity.User;
import com.chipsea.utils.BleConfig;
import com.chipsea.utils.L;
import com.chipsea.utils.ParseData;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName:WBYManager
 * @PackageName:com.chipsea.wby
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public class WBYManager implements BleManager<WBYManagerCallbacks> {

    private static final String TAG = WBYManager.class.getSimpleName();
    private WBYManagerCallbacks mCallbacks;
    private BluetoothGatt mBluetoothGatt;
    private Context mContext;
    private TimeOutHandler handler;
    private BroadData broadData;
    private BluetoothDevice device;


    private static final int TIMER_OUT = 500;


    /**
     * ------------------------------ UUID -----------------------------------------------------------------------------
     */

    private final static UUID NON_LOCKED_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    private final static UUID NON_LOCKED_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");

    private static final UUID NON_LOCKED_WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    private static final UUID LOCKED_SERVICE_UUID = UUID.fromString("0000181b-0000-1000-8000-00805f9b34fb");

    private static final UUID LOCKED_READ_INDICATE_CHARACTERISTIC_UUID = UUID.fromString("00002a9c-0000-1000-8000-00805f9b34fb");

    private static final UUID HISTORY_SERVICE_UUID = UUID.fromString("0000181b-0000-1000-8000-00805f9b34fb");

    private static final UUID HISTORY_INDICATE_CHARACTERISTIC_UUID = UUID.fromString("0000fa9c-0000-1000-8000-00805f9b34fb");

    private static final UUID BATTERY_CAPACITY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");

    private static final UUID BATTERY_CAPACITY_READ_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    private static final UUID TIME_SERVICE_UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");

    private static final UUID TIME_WRITE_READ_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("00002a08-0000-1000-8000-00805f9b34fb");

    private static final UUID OTA_SERVICE_UUID = UUID.fromString("0000faa0-0000-1000-8000-00805f9b34fb");

    private static final UUID OTA_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000faa1-0000-1000-8000-00805f9b34fb");

    private static final UUID OTA_WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000faa2-0000-1000-8000-00805f9b34fb");

    private static final UUID UNINT_SERVICE_UUID = UUID.fromString("0000ff0-0000-1000-8000-00805f9b34fb");

    private static final UUID UNINT_CHARACTERISTIC_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");


    //
    private static final UUID TEST_SERVER_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");

    private static final UUID TEST_CHARACTERISTIC_UUID = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");

    private static final UUID DESCR_TWO = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * -------------------------------------------------------------------------------------------------------------------
     */


    private final static String ERROR_CONNECTION_STATE_CHANGE = "Error on connection state change";
    private final static String ERROR_DISCOVERY_SERVICE = "Error on discovering services";
    private final static String ERROR_WRITE_DESCRIPTOR = "Error on writing descriptor";

//    private BluetoothGattCharacteristic mWriteCharacteristic, mNotifyCharacteristic;

    private BluetoothGattCharacteristic mNonLockWriteCharacteristic, mNonLockNotifyCharacteristic;

    private BluetoothGattCharacteristic mLockRICharacteristic;

    private BluetoothGattCharacteristic mHistoryIndicateCharacteristic;

    private BluetoothGattCharacteristic mBattyReadCharacteristic, mBattyNofifyCharacteristic;

    private BluetoothGattCharacteristic mTimeReadCharacteristic, mTimeWriteCharacteristic, mTimeNotifyCharacteristic;

    private BluetoothGattCharacteristic mOtaWriteCharacteristic, mOtaNotifyCharacteristic;

    private static WBYManager managerInstance = null;


    private User user = null;


    /**
     * singleton implementation of WBYManager class
     */
    public static synchronized WBYManager getWBYManager() {
        if (managerInstance == null) {
            managerInstance = new WBYManager();
        }
        return managerInstance;
    }


    @Override
    public void connect(Context context, BluetoothDevice device) {
        L.i(TAG, "connect" + device.toString());

        closeBluetoothGatt();

        this.device = device;
        broadData = new BroadData();
        broadData.setAddress(device.getAddress());
        broadData.setName(device.getName());

        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
        mContext = context;
        handler = new TimeOutHandler(this);
    }

    @Override
    public void disconnect() {
        L.d(TAG, "disconnect");
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }

    @Override
    public void setGattCallbacks(WBYManagerCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void closeBluetoothGatt() {
        mContext = null;
        broadData = null;

        if (mBluetoothGatt != null) {
            refresh(mBluetoothGatt);
            mBluetoothGatt.close();
            mBluetoothGatt = null;
//            mWriteCharacteristic = null;
//            mNotifyCharacteristic = null;
            mNonLockWriteCharacteristic = null;
            mNonLockNotifyCharacteristic = null;

            mLockRICharacteristic = null;

            mHistoryIndicateCharacteristic = null;
            mBattyReadCharacteristic = null;
            mBattyNofifyCharacteristic = null;

            mTimeReadCharacteristic = null;
            mTimeWriteCharacteristic = null;
            mTimeNotifyCharacteristic = null;

            mOtaWriteCharacteristic = null;
            mOtaNotifyCharacteristic = null;
        }
    }

    /**
     * 同步用户
     */
    public void syncUser(User user) {
        this.user = user;
    }

    /**
     * 同步
     *
     * @param index
     * @param unitType
     */
    public void sendCmd(byte index, byte unitType) {
//        if (index == AicareBleConfig.GET_BLE_VERSION) {
//            L.d(TAG, "getVersion");
//            startGetVersionTimer();//1、开始版本计时器
//        }
//        byte[] b = BleConfig.initCmd(index, unitType);
        byte[] b = new byte[2];
        b[0] = index;
        b[1] = unitType;
        writeValue(b);

//        read();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {

                    L.d(TAG, "Device STATE_CONNECTED");
                    // This will send callback to HTSActivity when device get
                    // connected
                    mCallbacks.onDeviceConnected();
                    /*try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.discoverServices();
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    L.d(TAG, "Device disconnected");
                    // This will send callback to HTSActivity when device get
                    // disconnected
                    mCallbacks.onDeviceDisconnected();
                }
            } else {
                L.e(TAG, "onConnectionStateChange error: (" + status + ")");
                mCallbacks.onError(ERROR_CONNECTION_STATE_CHANGE, status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                L.i(TAG, "onServicesDiscovered Success");
                L.i(TAG, "onServicesDiscovered status = " + status);
                List<BluetoothGattService> services = gatt.getServices();
                L.i(TAG, "onServicesDiscovered services = " + services.size());
                if (services.size() == 0) {
                    //mBluetoothGatt.discoverServices();
                    disconnect();
                }
                for (BluetoothGattService service : services) {
                    L.e(TAG, service.getUuid().toString());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        L.e(TAG, characteristic.getUuid().toString() + "; permission: " + ParseData.int2HexStr(characteristic.getPermissions())
                                + "; property: " + ParseData.int2HexStr(characteristic.getProperties()));
                    }
                }

                /**
                 * todo 使能 只能同时写一个特征
                 */

                /**
                 * 非锁定数据 使能
                 */
                boolean isNotify = enableNotification(mBluetoothGatt, LOCKED_SERVICE_UUID, LOCKED_READ_INDICATE_CHARACTERISTIC_UUID);
                if (isNotify) {
                    mCallbacks.onIndicationSuccess();
                }

            } else {
                L.e(TAG, "onServicesDiscovered error: (" + status + ")");
                mCallbacks.onError(ERROR_DISCOVERY_SERVICE, status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead");
            byte[] b = characteristic.getValue();
            L.i(TAG, "onCharacteristicRead " + ParseData.byteArr2Str(b));

            if (characteristic.getUuid().equals(LOCKED_READ_INDICATE_CHARACTERISTIC_UUID)) {
                L.i(TAG, " onCharacteristicRead  LOCKED_READ_CHARACTERISTIC_UUID");
            }


        }
//        onCharacteristicRead


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onCharacteristicWrite");
                if (characteristic.getUuid().equals(NON_LOCKED_WRITE_CHARACTERISTIC_UUID)) {
                    byte[] b = characteristic.getValue();
                    L.i(TAG, "NON LOCKED onCharacteristicWrite: " + ParseData.byteArr2Str(b));
                }

//                if (characteristic.getUuid().equals(AICARE_WRITE_CHARACTERISTIC_UUID)) {
//                    byte[] b = characteristic.getValue();
//                    L.i(TAG, "onCharacteristicWrite: " + ParseData.byteArr2Str(b));
//                    mCallbacks.getCMD("Write:" + ParseData.byteArr2Str(b));
//                    if (usersByte.size() != 0) {
//                        System.out.println("index = " + index);
//                        if (index < usersByte.size() - 1) {
//                            if (Arrays.equals(b, usersByte.get(index))) {
//                                writeValue(usersByte.get(++index));
//                            }
//                        } else {
//                            if (Arrays.equals(b, usersByte.get(index))) {
//                                sendCmd(AicareBleConfig.SYNC_LIST_OVER, AicareBleConfig.UNIT_KG);
//                                index = 0;
//                            }
//                        }
//                    }
//
//                    if (Arrays.equals(b, userIdByte)) {
//                        syncUserInfo();
//                    }
//
//                    if (Arrays.equals(b, dateByte)) {
//                        sendCmd(AicareBleConfig.SYNC_TIME, AicareBleConfig.UNIT_KG);
//                    }
//                }
            } else {
                L.e(TAG, "onCharacteristicWrite error: +  (" + status + ")");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] b = characteristic.getValue();
            L.i(TAG, "onCharacteristicChanged: " + ParseData.byteArr2Str(b));

            L.i(TAG, "onCharacteristicChanged: characteristic  UUID" + characteristic.getUuid());

            mCallbacks.getCMD("Changed:" + ParseData.byteArr2Str(b));

            if (characteristic.getUuid().equals(NON_LOCKED_NOTIFY_CHARACTERISTIC_UUID)) {
                L.i(TAG, "onCharacteristicChanged: NON LOCKED DATA NOTIFY");
                handleData(b);
            } else if (characteristic.getUuid().equals(LOCKED_READ_INDICATE_CHARACTERISTIC_UUID)) {
                L.i(TAG, "onCharacteristicChanged: LOCK DATA NOTIFY");
                handleData(b);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //indications has been enabled
                L.i(TAG, "onDescriptorWrite");
                //mCallbacks.onIndicationSuccess();
                // sendCmd(AicareBleConfig.GET_BLE_VERSION, AicareBleConfig.UNIT_KG);//a、获取版本
            } else {
                L.e(TAG, "onDescriptorWrite error: +  (" + status + ")");
                mCallbacks.onError(ERROR_WRITE_DESCRIPTOR, status);
            }
        }
    };


    /**
     * 判断是否有chipsea UUID
     *
     * @return
     */
    private boolean hasCsUUID() {
        return mNonLockWriteCharacteristic != null;
    }

    /**
     * 往aicare有写入属性的特征值中写值
     *
     * @param b
     */
    private void writeValue(byte[] b) {
        if (hasCsUUID()) {
            mNonLockWriteCharacteristic.setValue(b);
            mNonLockWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            boolean success = mBluetoothGatt.writeCharacteristic(mNonLockWriteCharacteristic);
            if (success) {
                L.e(TAG, "writeValue: bytes = " + ParseData.byteArr2Str(b));
            }
        }
    }


    /**
     * 清除设备缓存
     *
     * @param gatt
     * @return
     */
    private void refresh(BluetoothGatt gatt) {
        try {
            L.e(TAG, "refresh device cache");
            Method localMethod = gatt.getClass().getMethod("refresh", (Class[]) null);
            if (localMethod != null) {
                boolean result = (Boolean) localMethod.invoke(gatt, (Object[]) null);
                if (!result)
                    L.e(TAG, "refresh failed");
            }
        } catch (Exception e) {
            L.e(TAG, "An exception occurred while refreshing device cache");
        }
    }

    /**
     * 获取相关信息超时
     */
    private static class TimeOutHandler extends Handler {

        private static final int MAX_GET_DECIMAL_TIMES = 3;
        private WeakReference<WBYManager> wbyManagerRef;
        private int getDecimalCount = 0;

        public TimeOutHandler(WBYManager wbyManager) {
            this.wbyManagerRef = new WeakReference<>(wbyManager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WBYManager wbyManager = wbyManagerRef.get();
            if (wbyManager != null) {
                switch (msg.what) {
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 处理ble传过来的数据
     *
     * @param b
     */
    private void handleData(byte[] b) {
        SparseArray<Object> sparseArray = BleConfig.getDatas(b);
        if (sparseArray != null && sparseArray.size() != 0) {
            if (sparseArray.indexOfKey(BleConfig.WEIGHT_DATA) >= 0) {
                CsFatScale csFatScale = (CsFatScale) sparseArray.get(BleConfig.WEIGHT_DATA);
                mCallbacks.getCsFatScale(csFatScale);

                /**
                 * 获取体脂数据
                 */
                mCallbacks.getBodyFatData(BleConfig.getBodyFatData(user, csFatScale));
            }
        }
    }


    /**
     * 启用给定特征的通知
     */
    private boolean enableNotification(BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID) {
        boolean success = false;
        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = findNotifyCharacteristic(service, characteristicUUID);
            if (characteristic != null) {
                success = gatt.setCharacteristicNotification(characteristic, true);
                if (success) {
                    // 来源：http://stackoverflow.com/questions/38045294/oncharacteristicchanged-not-called-with-ble
                    for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                        if (dp != null) {
                            if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                                dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                            }
                            gatt.writeDescriptor(dp);
                        }
                    }
                }
            }
        }
        return success;
    }

    private BluetoothGattCharacteristic findNotifyCharacteristic(BluetoothGattService service, UUID characteristicUUID) {
        BluetoothGattCharacteristic characteristic = null;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {
            if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
                    && characteristicUUID.equals(c.getUuid())) {
                characteristic = c;
                break;
            }
        }
        if (characteristic != null)
            return characteristic;
        for (BluetoothGattCharacteristic c : characteristics) {
            if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0
                    && characteristicUUID.equals(c.getUuid())) {
                characteristic = c;
                break;
            }
        }
        return characteristic;
    }
}
