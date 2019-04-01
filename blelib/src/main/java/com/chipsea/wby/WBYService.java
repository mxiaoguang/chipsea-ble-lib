package com.chipsea.wby;

import android.content.Intent;
import android.os.IBinder;

import com.chipsea.bleprofile.BleManager;
import com.chipsea.bleprofile.BleProfileService;
import com.chipsea.entity.BodyFatData;
import com.chipsea.entity.CsFatScale;
import com.chipsea.entity.User;
import com.chipsea.utils.BleConfig;
import com.chipsea.utils.L;

/**
 * @ClassName:WBYService
 * @PackageName:com.chipsea.wby
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public class WBYService extends BleProfileService implements WBYManagerCallbacks {

    public static final String ACTION_WEIGHT_DATA = "com.chipsea.ACTION_WEIGHT_DATA";

    public static final String ACTION_FAT_DATA = "com.chipsea.ACTION_FAT_DATA";

    public static final String ACTION_CSFAT_SCALE_DATA = "com.chipsea.ACTION_CSFAT_SCALE_DATA";

    public static final String EXTRA_IS_HISTORY = "com.chipsea.EXTRA_IS_HISTORY";

    public static final String EXTRA_FAT_DATA = "com.chipsea.EXTRA_FAT_DATA";

    public static final String EXTRA_CSFAT_SCALE_DATA = "com.chipsea.EXTRA_CSFAT_SCALE_DATA";

    private WBYManager mManager;

    public boolean mBinded;

    private final LocalBinder mBinder = new WBYBinder();

    @Override
    protected BleManager<WBYManagerCallbacks> initializeManager() {
        return mManager = WBYManager.getWBYManager();
    }


    @Override
    protected LocalBinder getBinder() {
        return mBinder;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        mBinded = true;
        return super.onBind(intent);
    }


    @Override
    public void onRebind(final Intent intent) {
        mBinded = true;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        mBinded = false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager = null;
        mBinded = false;
    }

    @Override
    public void getCMD(String cmd) {

    }

    @Override
    public void getCsFatScale(CsFatScale csFatScale) {
        Intent intent = new Intent(ACTION_CSFAT_SCALE_DATA);
        intent.putExtra(EXTRA_CSFAT_SCALE_DATA, csFatScale);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        L.i(csFatScale.toString());
    }

    @Override
    public void getBodyFatData(BodyFatData bodyFatData) {
        Intent intent = new Intent(ACTION_FAT_DATA);
        intent.putExtra(EXTRA_IS_HISTORY, false);
        intent.putExtra(EXTRA_FAT_DATA, bodyFatData);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        L.i(bodyFatData.toString());
    }


    public class WBYBinder extends LocalBinder {

        /**
         * 获取历史记录
         */
        public void syncHistory() {
//            mManager.sendCmd(AicareBleConfig.SYNC_HISTORY, AicareBleConfig.UNIT_KG);
        }

        /**
         * 查询电池电量
         */
        public void queryBattayCapacity() {

        }

        /**
         * 同步时间
         */
        public void syncTime() {

        }

        /**
         * ota 升级
         */
        public void otaUpgrade() {

        }

        public void syncUser(User user) {
            mManager.syncUser(user);
        }

        /**
         * 同步单位
         *
         * @param unit
         */
        public void syncUnit(byte unit) {
            mManager.sendCmd(BleConfig.SYNC_UNIT, unit);
        }

        public WBYService getService() {
            return WBYService.this;
        }
    }
}
