package com.chipsea.bleprofile;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * @ClassName:BleManager
 * @PackageName:com.chipsea.bleprofile
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public interface BleManager<E extends BleManagerCallbacks> {

    /**
     * Connects to the Bluetooth Smart device
     *
     * @param context this must be an application context, not the Activity. Call {@link android.app.Activity#getApplicationContext()} to get one.
     * @param device  a device to connect to
     */
    void connect(final Context context, final BluetoothDevice device);

    /**
     * Disconnects from the device. Does nothing if not connected.
     */
    void disconnect();

    /**
     * Sets the manager callback listener
     *
     * @param callbacks the callback listener
     */
    void setGattCallbacks(E callbacks);

    /**
     * Closes and releases resources. May be also used to unregister broadcast listeners.
     */
    void closeBluetoothGatt();
}
