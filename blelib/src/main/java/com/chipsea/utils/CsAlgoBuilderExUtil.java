package com.chipsea.utils;

import android.content.Context;

import com.chipsea.healthscale.CsAlgoBuilderEx;

/**
 * @Desc:
 * @ClassName:CsAlgoBuilderExUtil
 * @PackageName:com.chipsea.utils
 * @Create On 2019/4/1 0001
 * @Site:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/1/31 0031 handongkeji All rights reserved.
 */
public class CsAlgoBuilderExUtil {

    private static boolean isInit;//是否初始化

    private static CsAlgoBuilderEx algoBuilderEx = null;


    /**
     * 初始化 CsAlgoBuilderEx SDK
     *
     * @param context
     */
    public static void initCsAlgoBuilder(Context context) {
        if (algoBuilderEx == null) {
            algoBuilderEx = new CsAlgoBuilderEx(context);
        }
        isInit = true;
    }

    /**
     * 获取
     *
     * @return CsAlgoBuilderEx
     */
    public static CsAlgoBuilderEx getAlgoBuilderEx() {
        if (isInit) {
            return algoBuilderEx;
        }
        throw new IllegalStateException("Please Init CsAlgoBuilderEx");
    }
}
