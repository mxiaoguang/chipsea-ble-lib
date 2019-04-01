package com.chipsea.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Desc:体脂称原始数据
 * @ClassName:CsFatScale
 * @PackageName:com.chipsea.entity
 * @Create On 2019/3/26 0026
 * @Site:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/1/31 0031 handongkeji All rights reserved.
 */
public class CsFatScale implements Serializable {

    private int flag;

    private float weight;//体重

    private Date date;//日期

    private float adc;//阻抗1

    private float adcTwo;//阻抗2

    private int msg;

    public CsFatScale() {
    }

    public CsFatScale(int flag, float weight, Date date, int adc, int adcTwo, int msg) {
        this.flag = flag;
        this.weight = weight;
        this.date = date;
        this.adc = adc;
        this.adcTwo = adcTwo;
        this.msg = msg;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public float getAdc() {
        return adc;
    }

    public void setAdc(float adc) {
        this.adc = adc;
    }

    public float getAdcTwo() {
        return adcTwo;
    }

    public void setAdcTwo(float adcTwo) {
        this.adcTwo = adcTwo;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CsFatScale{" +
                "flag=" + flag +
                ", weight=" + weight +
                ", date=" + date +
                ", adc=" + adc +
                ", adcTwo=" + adcTwo +
                ", msg=" + msg +
                '}';
    }
}
