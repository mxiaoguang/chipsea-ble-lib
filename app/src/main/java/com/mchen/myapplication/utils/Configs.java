package com.mchen.myapplication.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Suzy on 2016/7/23.
 */
public class Configs {

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    /**
     * 删除指定文件
     *
     * @param filename
     */
    public static int deleteFile(String filename) {
        File tmp = new File(filename);
        int flag = 0;
        if (tmp.exists()) {
            if(tmp.delete()) {
                flag = 1;
            }else {
                flag = 2;
            }
        }
        return flag;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        String dir = sdDir.toString() + "/iWeightDemo/";
        return dir;
    }
}
