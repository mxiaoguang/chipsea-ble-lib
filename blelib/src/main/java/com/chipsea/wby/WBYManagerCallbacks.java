package com.chipsea.wby;

import com.chipsea.bleprofile.BleManagerCallbacks;
import com.chipsea.entity.BodyFatData;
import com.chipsea.entity.CsFatScale;

/**
 * @ClassName:WBYManagerCallbacks
 * @PackageName:com.chipsea.wby
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public interface WBYManagerCallbacks extends BleManagerCallbacks {

    void getCMD(String cmd);

    void getCsFatScale(CsFatScale csFatScale);

    void getBodyFatData(BodyFatData bodyFatData);
}
