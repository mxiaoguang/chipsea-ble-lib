package com.chipsea.utils;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.SparseArray;

import com.chipsea.entity.BodyFatData;
import com.chipsea.entity.BroadData;
import com.chipsea.entity.CsFatScale;
import com.chipsea.entity.User;
import com.chipsea.healthscale.CsAlgoBuilderEx;
import com.chipsea.scandecoder.ScanRecord;

import java.util.Date;
import java.util.List;

/**
 * @ClassName:BleConfig
 * @PackageName:com.chipsea.utils
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public class BleConfig {

    private static final String TAG = BleConfig.class.getSimpleName();

    public static final byte BM_CS = 0x0F;

    private static final byte BM_CS_FLAG = (byte) 0xBC;


    //单位
    public final static byte UNIT_KG = 0x00;    //千克
    public final static byte UNIT_LB = 0x01;    //英镑
    public final static byte UNIT_JIN = 0x02;   //斤
    public final static byte UNIT_ST = 0x03;    //stb


    //Map对应的key
    public final static int WEIGHT_DATA = 0;

    //指令
    public final static byte SYNC_UNIT = (byte) 0x01;


    /**
     * 解析广播信息获取广播内容
     *
     * @param scanRecord
     * @param device
     * @param rssi
     * @return
     */
    public static BroadData getBroadData(BluetoothDevice device, int rssi, byte[] scanRecord) {
        ParcelUuid[] parcelUuids = device.getUuids();
        if (parcelUuids != null && parcelUuids.length > 0) {
            for (ParcelUuid uuid : parcelUuids
            ) {
                L.i(">>>>>>>>>> UUID >>>>>>" + uuid.getUuid());

            }
        }


        ScanRecord scanResult = ScanRecord.parseFromBytes(scanRecord);
        if (scanResult != null) {
            SparseArray<byte[]> manufacturerData = scanResult.getManufacturerSpecificData();
            List<ParcelUuid> uuidList = scanResult.getServiceUuids();
            if (!isListEmpty(uuidList)) {
                for (int i = 0; i < uuidList.size(); i++) {
                    L.e(TAG, "uuid: " + uuidList.get(i).getUuid().toString());
                }
            }
            /**
             * //todo
             * 2.广播包规范
             * 接入 OKOK 的蓝牙设备，广播包必须符合规范才能接入,要求广播包中包含 manufacture
             * data,格式要求如下：
             * byte description
             * 0-1 FFF0(芯海标识）
             * 2 广播版本号（当前 02）
             * 3 消息属性，参见表一
             * 4~5 体重
             * 6~9 产品 ID
             * 10~11 蓝牙版本
             * 12~13 秤算法版本
             * 14-19 mac 地址
             */
            if (manufacturerData != null
                    && !TextUtils.isEmpty(device.getName())
                    && device.getName().startsWith("Chipsea-BLE")) {
                L.i("manufacturerData " + manufacturerData.toString());
                BroadData broadData = new BroadData();
                broadData.setAddress(device.getAddress());
                broadData.setName(device.getName());
                broadData.setRssi(rssi);
                broadData.setDeviceType(BM_CS);
                return broadData;
            }
        }
        return null;
    }


    /**
     * 判断数组是否为空
     *
     * @param b
     * @return
     */
    private static boolean isArrEmpty(byte[] b) {
        return b == null || b.length == 0;
    }

    /**
     * 判断list是否为空
     *
     * @param list
     * @param <T>
     * @return
     */
    private static <T> boolean isListEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }


    /**
     * 初始化指令
     *
     * @param index
     * @param unitType
     * @return
     */
    public static byte[] initCmd(byte index, byte unitType) {
        byte[] b = new byte[19];
        b[0] = (byte) 0xFF;
        b[1] = (byte) 0xF0;
        b[2] = (byte) 0x02;

        switch (index) {
            case SYNC_UNIT://同步单位
                b[3] = ParseData.bitToByte("000");
                break;
        }
//        b[0] = AICARE_FLAG;
//        b[1] = deviceType;
//        switch (index) {
//            case SYNC_HISTORY:
//                b[2] = SYNC_HISTORY;
//                b[6] = SYNC_HISTORY_OR_LIST;
//                break;
//            case SYNC_USER_ID:
//                b[2] = SYNC_USER_ID;
//                b[3] = (byte) user.getId();
//                b[6] = SETTINGS;
//                break;
//            case SYNC_USER_INFO:
//                b[2] = SYNC_USER_INFO;
//                b[3] = (byte) user.getSex();
//                b[4] = ((Integer) user.getAge()).byteValue();
//                b[5] = ((Integer) user.getHeight()).byteValue();
//                b[6] = SETTINGS;
//                break;
//            case SYNC_DATE:
//                String[] date = ParseData.getDate().split("-");
//                b[2] = SYNC_DATE;
//                b[3] = Integer.valueOf(date[0].substring(2, 4)).byteValue();
//                b[4] = Integer.valueOf(date[1]).byteValue();
//                b[5] = Integer.valueOf(date[2]).byteValue();
//                b[6] = SETTINGS;
//                break;
//            case SYNC_TIME:
//                String[] time = ParseData.getTime().split(":");
//                b[2] = SYNC_TIME;
//                b[3] = Integer.valueOf(time[0]).byteValue();
//                b[4] = Integer.valueOf(time[1]).byteValue();
//                b[5] = Integer.valueOf(time[2]).byteValue();
//                b[6] = SETTINGS;
//                break;
//            case SYNC_UNIT:
//                b[2] = OPERATE_OR_STATE;
//                b[3] = SYNC_UNIT;
//                b[4] = unitType;
//                b[6] = SETTINGS;
//                break;
//            case GET_BLE_VERSION:
//                b[2] = GET_BLE_VERSION;
//                b[6] = SETTINGS;
//                break;
//            case SYNC_LIST_OVER:
//                b[2] = UPDATE_USER_OR_LIST;
//                b[3] = SYNC_LIST_OVER;
//                b[6] = SYNC_HISTORY_OR_LIST;
//                break;
//        }
//        b[7] = getByteSum(b, SUM_START, SUM_END);
        L.i(TAG, "initCmd: " + ParseData.byteArr2Str(b));
        return b;
    }

    /**
     * 获取设备端返回值
     *
     * @param b
     * @return
     */
    public static SparseArray<Object> getDatas(byte[] b) {
        L.i(TAG, "getDatas: " + ParseData.byteArr2Str(b));
        SparseArray<Object> sparseArray = new SparseArray<>();
        if (checkData(b)) {
            sparseArray = getCsFatScaleData(b);
        }

        return sparseArray;
    }


    /**
     * 校验数据是否正确
     *
     * @param b
     * @return
     */
    private static boolean checkData(byte[] b) {
//        if (b == null || b.length == 0) return false;
//        if (b.length == 8 && b[0] == AICARE_FLAG && (b[1] == TYPE_WEI || b[1] == TYPE_WEI_TEMP || b[1] == TYPE_WEI_TEMP_BROAD || b[1] == TYPE_WEI_BROAD)) {
//            byte result = getByteSum(b, SUM_START, SUM_END);
//            L.e(TAG, "result = " + result);
//            L.e(TAG, "b[SUM_END] = " + b[SUM_END]);
//            return result == b[SUM_END];
//        }
//        return false;
        return true;
    }

    /**
     * 获取体重数据
     *
     * @param b
     * @return
     */
    private static SparseArray<Object> getCsFatScaleData(byte[] b) {
        L.i(TAG, "getCsFatScaleData: " + ParseData.byteArr2Str(b));
        SparseArray<Object> sparseArray = new SparseArray<>();


        //
        int flag = ParseData.getDataInt(b[1], b[0]);

        int year = ParseData.getDataInt(b[3], b[2]);
        int month = ParseData.binaryToDecimal(b[4]);
        int day = ParseData.binaryToDecimal(b[5]);

        int hour = ParseData.binaryToDecimal(b[6]);
        int minute = ParseData.binaryToDecimal(b[7]);
        int second = ParseData.binaryToDecimal(b[8]);


        int resistanceOne = ParseData.getDataInt(b[10], b[9]);

        int weight = ParseData.getDataInt(b[12], b[11]);

        int resistanceTwo = ParseData.getDataInt(b[14], b[13]);

        int msg = ParseData.binaryToDecimal(b[15]);

        //b[16] - b[19]  预留字段

        Date date = new Date(year, month, day, hour, minute, second);

        CsFatScale csFatScale = new CsFatScale(flag, weight, date, resistanceOne, resistanceTwo, msg);

        sparseArray.put(WEIGHT_DATA, csFatScale);
        return sparseArray;
    }

    /**
     * 获取体脂数据
     *
     * @param user
     * @param csFatScale
     * @return
     */
    public static BodyFatData getBodyFatData(User user, CsFatScale csFatScale) {
        BodyFatData bodyFatData = null;

        if (user != null) {

            CsAlgoBuilderEx csAlgoBuilderEx = CsAlgoBuilderExUtil.getAlgoBuilderEx();

            /**
             * 设置用户信息
             * @param height 身高 单位cm
             * @param sex 男-1 女-0
             * @param age 年龄
             * @param weight 当前测量体重 单位kg
             * @param r 当前测量电阻
             *
             */
//            csAlgoBuilderEx.setUserInfo(155.0f, (byte) 0, 29, 65.6f, 475.55f);

            csAlgoBuilderEx.setUserInfo(user.getHeight(), user.getSex(), user.getAge(), (csFatScale.getWeight() / 10f), (csFatScale.getAdc() / 10f));

            /**如果需要进行电阻滤波，请调用以下方法
             * 滤波原理:对比当次与上一次测量电阻值，依据测量时间及根据一定的滤波规则对是电阻进行滤波处理，避免连续测量由于电阻的干扰带来较大的测量结果差异
             * public float setUserInfo(float height, byte sex, int age, float curWeight, float curR, Date curTime, float lastR, Date lastTime)
             * 加入电阻滤波后的构造函数（用于在根据蓝牙秤上传的数据后调用并计算结果）
             * @param height 身高
             * @param sex 男-1 女-0
             * @param age 年龄
             * @param curWeight 当前测量体重
             * @param curR 当前测量电阻
             * @param curTime 当前测量时间
             * @param lastR 上一次测量电阻
             * @param lastTime 上一次测量时间
             * @return 返回滤波后的电阻
             */
            StringBuilder sb = new StringBuilder();
            sb.append("细胞外液EXF:" + csAlgoBuilderEx.getEXF() + "\r\n");
            sb.append("细胞内液Inf:" + csAlgoBuilderEx.getInF() + "\r\n");
            sb.append("总水重TF:" + csAlgoBuilderEx.getTF() + "\r\n");
            sb.append("含水百分比TFR:" + csAlgoBuilderEx.getTFR() + "\r\n");
            sb.append("去脂体重LBM:" + csAlgoBuilderEx.getLBM() + "\r\n");
            sb.append("肌肉重(含水)SLM:" + csAlgoBuilderEx.getSLM() + "\r\n");
            sb.append("蛋白质PM:" + csAlgoBuilderEx.getPM() + "\r\n");
            sb.append("脂肪重FM:" + csAlgoBuilderEx.getFM() + "\r\n");
            sb.append("脂肪百份比BFR:" + csAlgoBuilderEx.getBFR() + "\r\n");
            sb.append("水肿测试EE:" + csAlgoBuilderEx.getEE() + "\r\n");
            sb.append("肥胖度OD:" + csAlgoBuilderEx.getOD() + "\r\n");
            sb.append("肌肉控制MC:" + csAlgoBuilderEx.getMC() + "\r\n");
            sb.append("体重控制WC:" + csAlgoBuilderEx.getWC() + "\r\n");
            sb.append("基础代谢BMR:" + csAlgoBuilderEx.getBMR() + "\r\n");
            sb.append("骨(无机盐)MSW:" + csAlgoBuilderEx.getMSW() + "\r\n");
            sb.append("内脏脂肪等级VFR:" + csAlgoBuilderEx.getVFR() + "\r\n");
            sb.append("身体年龄BodyAge:" + csAlgoBuilderEx.getBodyAge() + "\r\n");
            sb.append("评分:" + csAlgoBuilderEx.getScore() + "\r\n");


            L.i(TAG, ">>>>>>>>>>BodyFatData>>>>>>>>>>" + sb.toString());


            bodyFatData = new BodyFatData(DateUtil.getYmd(csFatScale.getDate()), DateUtil.getTime(csFatScale.getDate()),
                    csFatScale.getWeight() / 10f, (csFatScale.getWeight() / 10f) / ((user.getHeight() / 100f) * (user.getHeight()) / 100f), csAlgoBuilderEx.getBFR(), csAlgoBuilderEx.getBFR(), (int) csAlgoBuilderEx.getVFR(), 0, csAlgoBuilderEx.getBMR(), csAlgoBuilderEx.getMSW(), csAlgoBuilderEx.getTF(), (int) csAlgoBuilderEx.getBodyAge(), csAlgoBuilderEx.getPM(), csAlgoBuilderEx.getScore(), user.getId(), user.getSex()
                    , user.getAge(), user.getHeight(), (csFatScale.getAdc() / 10f));
        }

        return bodyFatData;
    }
}
