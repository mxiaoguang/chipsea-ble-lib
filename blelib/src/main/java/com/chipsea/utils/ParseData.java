package com.chipsea.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName:ParseData
 * @PackageName:com.chipsea.utils
 * @Create On 2019/3/24.
 * @Site:te:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/8/13  handongkeji All rights reserved.
 */
public class ParseData {

    /**
     * byte转16进制
     *
     * @param b
     * @return
     */
    public static String binaryToHex(byte b) {
        String str = byteToBit(b);
        String hexStr = Integer.toHexString(Integer.parseInt(str, 2));
        StringBuilder stringBuffer = new StringBuilder();
        if (hexStr.length() == 1) {
            stringBuffer.append("0");
        }
        stringBuffer.append(hexStr);
        return stringBuffer.toString().toUpperCase();
    }

    /**
     * byte转十进制
     *
     * @param b
     * @return
     */
    public static int binaryToDecimal(byte b) {
        String str = byteToBit(b);
        return Integer.parseInt(str, 2);
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b) & 0x1);
    }

    /**
     * int转byte
     *
     * @param res
     * @return
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[2];

        targets[1] = (byte) (res & 0xff);// 最低位
        targets[0] = (byte) ((res >> 8) & 0xff);// 次低位
        /*targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移*/
        return targets;
    }

    /**
     * Bit转Byte
     */
    public static byte bitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * byte数组转str
     *
     * @param b
     * @return
     */
    public static String byteArr2Str(byte[] b) {
        if (b != null && b.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            for (int i = 0; i < b.length; i++) {
                stringBuilder.append("0x");
                stringBuilder.append(addZero(binaryToHex(b[i])));
                if (i < b.length - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
        return "";
    }


    /**
     * 字符串长度等于1的话，补0
     *
     * @param str
     * @return
     */
    public static String addZero(String str) {
        //L.i(ParseData.class, "addZero");
        StringBuilder sBuffer = new StringBuilder();
        if (str.length() == 1) {
            sBuffer.append("0");
            sBuffer.append(str);
            return sBuffer.toString();
        } else {
            return str;
        }
    }


    public static String int2HexStr(int i) {
        return binaryToHex(Integer.valueOf(i).byteValue());
    }


    /**
     * 将数据颠倒顺序
     *
     * @param b
     * @return
     */
    public static byte[] reverse(byte[] b) {
        int len = b.length;
        for (int i = 0; i < len / 2; i++) {
            byte tem = b[i];
            b[i] = b[len - 1 - i];
            b[len - 1 - i] = tem;
        }
        return b;
    }

    /**
     * 判断数组b是否以a开头
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean arrStartWith(byte[] a, byte[] b) {
        int k = 0;
        if (a.length < b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == b[i]) {
                    k++;
                    continue;
                }
            }
            return k == a.length;
        }
        return false;
    }


    public static int getDataInt(int first, int second, byte[] b) {
        int data = ((b[first] & 0xFF) << 8) + (b[second] & 0xFF);
//        L.e(ParseData.class, "getDataInt = " + data);
        return data;
    }

    public static int getDataInt(byte b1, byte b2) {
        int data = ((b1 & 0xFF) << 8) + (b2 & 0xFF);
//        L.e(ParseData.class, "getDataInt = " + data);
        return data;
    }

    /**
     * 获取当前系统时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
