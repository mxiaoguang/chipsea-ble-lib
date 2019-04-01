package com.chipsea.entity;

/**
 * @Desc:用户信息
 * @ClassName: User
 * @PackageName:com.chipsea.entity
 * @Create On 2019/3/29 0029
 * @Site:http://www.handongkeji.com
 * @author:chenzhiguang
 * @Copyrights 2018/1/31 0031 handongkeji All rights reserved.
 */
public class User {
    private int id;//id为0x7F是为游客
    private byte sex;//性别 1:男; 2:女
    private int age;//年龄
    private float height;//身高
    private float weight;//体重
    private float adc;//阻抗值

    public User() {
    }

    public User(int id, byte sex, int age, float height, float weight, float adc) {
        this.id = id;
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.adc = adc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getAdc() {
        return adc;
    }

    public void setAdc(float adc) {
        this.adc = adc;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sex=" + sex +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", adc=" + adc +
                '}';
    }
}
