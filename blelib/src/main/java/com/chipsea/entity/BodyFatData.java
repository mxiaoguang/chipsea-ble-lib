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
    private double bmi;
    /**
     * 体脂率 body fat rate
     */
    private double bfr;
    /**
     * 皮下脂肪率 Subcutaneous fat rate
     */
    private double sfr;
    /**
     * 内脏脂肪指数
     */
    private int uvi;
    /**
     * 肌肉率 Rate of muscle
     */
    private double rom;
    /**
     * 基础代谢率 basal metabolic rate
     */
    private double bmr;
    /**
     * 骨骼质量 Bone Mass
     */
    private double bm;
    /**
     * 水含量
     */
    private double vwc;
    /**
     * 身体年龄 physical bodyAge
     */
    private int bodyAge;
    /**
     * 蛋白率 protein percentage
     */
    private double pp;

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

    public BodyFatData(String date, String time, float weight, double bmi, double bfr, double sfr, int uvi, double rom, double bmr, double bm, double vwc, int bodyAge,  double pp,float score, int number, int sex, int age, float height, float adc) {
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

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getBfr() {
        return bfr;
    }

    public void setBfr(double bfr) {
        this.bfr = bfr;
    }

    public double getSfr() {
        return sfr;
    }

    public void setSfr(double sfr) {
        this.sfr = sfr;
    }

    public int getUvi() {
        return uvi;
    }

    public void setUvi(int uvi) {
        this.uvi = uvi;
    }

    public double getRom() {
        return rom;
    }

    public void setRom(double rom) {
        this.rom = rom;
    }

    public double getBmr() {
        return bmr;
    }

    public void setBmr(double bmr) {
        this.bmr = bmr;
    }

    public double getBm() {
        return bm;
    }

    public void setBm(double bm) {
        this.bm = bm;
    }

    public double getVwc() {
        return vwc;
    }

    public void setVwc(double vwc) {
        this.vwc = vwc;
    }

    public int getBodyAge() {
        return bodyAge;
    }

    public void setBodyAge(int bodyAge) {
        this.bodyAge = bodyAge;
    }

    public double getPp() {
        return pp;
    }

    public void setPp(double pp) {
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
