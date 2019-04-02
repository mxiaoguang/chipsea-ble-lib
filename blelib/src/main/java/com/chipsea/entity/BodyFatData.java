package com.chipsea.entity;

import java.io.Serializable;

/**
 * @Desc:体脂数据
 * @ClassName: BodyFatData
 * @PackageName:com.chipsea.entity
 * @Create On 2019/3/29 0029
 * @Site:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/1/31 0031 handongkeji All rights reserved.
 */
public class BodyFatData implements Serializable {

    private static final long serialVersionUID = 3240789953283964275L;
    /**
     * 日期
     */
    private String date;

    /**
     * 时间
     */
    private String time;
    /**
     * 体重
     */
    private float weight;
    /**
     * 身体质量指数
     */
    private float bmi;
    /**
     * 体脂率 body fat rate
     */
    private float bfr;
    /**
     * 皮下脂肪率 Subcutaneous fat rate
     */
    private float sfr;
    /**
     * 内脏脂肪指数
     */
    private float uvi;
    /**
     * 肌肉率 Rate of muscle
     */
    private float rom;
    /**
     * 基础代谢率 basal metabolic rate
     */
    private float bmr;
    /**
     * 骨骼质量 Bone Mass
     */
    private float bm;
    /**
     * 水含量
     */
    private float vwc;
    /**
     * 身体年龄 physical bodyAge
     */
    private float bodyAge;
    /**
     * 蛋白率 protein percentage
     */
    private float pp;

    /**
     * 得分
     */
    private float score;

    /**
     * 用户编号
     */
    private int number;
    /**
     * 性别
     */
    private int sex;
    /**
     * 年龄
     */
    private int age;
    /**
     * 身高
     */
    private float height;
    /**
     * 阻抗值
     */
    private float adc;


    public BodyFatData() {
    }

    public BodyFatData(String date, String time, float weight, float bmi, float bfr, float sfr, float uvi, float rom, float bmr, float bm, float vwc, float bodyAge, float pp, float score, int number, int sex, int age, float height, float adc) {
        this.date = date;
        this.time = time;
        this.weight = weight;
        this.bmi = bmi;
        this.bfr = bfr;
        this.sfr = sfr;
        this.uvi = uvi;
        this.rom = rom;
        this.bmr = bmr;
        this.bm = bm;
        this.vwc = vwc;
        this.bodyAge = bodyAge;
        this.pp = pp;
        this.score = score;
        this.number = number;
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.adc = adc;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBfr() {
        return bfr;
    }

    public void setBfr(float bfr) {
        this.bfr = bfr;
    }

    public float getSfr() {
        return sfr;
    }

    public void setSfr(float sfr) {
        this.sfr = sfr;
    }

    public float getUvi() {
        return uvi;
    }

    public void setUvi(float uvi) {
        this.uvi = uvi;
    }

    public float getRom() {
        return rom;
    }

    public void setRom(float rom) {
        this.rom = rom;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public float getBm() {
        return bm;
    }

    public void setBm(float bm) {
        this.bm = bm;
    }

    public float getVwc() {
        return vwc;
    }

    public void setVwc(float vwc) {
        this.vwc = vwc;
    }

    public float getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(float bodyAge) {
        this.bodyAge = bodyAge;
    }

    public float getPp() {
        return pp;
    }

    public void setPp(float pp) {
        this.pp = pp;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getAdc() {
        return adc;
    }

    public void setAdc(float adc) {
        this.adc = adc;
    }

    @Override
    public String toString() {
        return "BodyFatData{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", weight=" + weight +
                ", bmi=" + bmi +
                ", bfr=" + bfr +
                ", sfr=" + sfr +
                ", uvi=" + uvi +
                ", rom=" + rom +
                ", bmr=" + bmr +
                ", bm=" + bm +
                ", vwc=" + vwc +
                ", bodyAge=" + bodyAge +
                ", pp=" + pp +
                ", score=" + score +
                ", number=" + number +
                ", sex=" + sex +
                ", age=" + age +
                ", height=" + height +
                ", adc=" + adc +
                '}';
    }
}
