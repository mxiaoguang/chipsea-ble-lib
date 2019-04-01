package com.chipsea.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Desc:
 * @ClassName:DateUtil
 * @PackageName:com.chipsea.utils
 * @Create On 2019/3/30 0030
 * @Site:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/1/31 0031 handongkeji All rights reserved.
 */
public class DateUtil {

    public static SimpleDateFormat DF_CENTER_LINE = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static SimpleDateFormat DF_TIME = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

    /**
     * 获取日期 如：2019-3-30
     *
     * @param date
     * @return
     */
    public static String getYmd(Date date) {
        return DF_CENTER_LINE.format(date);
    }

    /**
     * 获取时间 如：09:19:55
     *
     * @param date
     * @return
     */
    public static String getTime(Date date) {
        return DF_TIME.format(date);
    }
}
