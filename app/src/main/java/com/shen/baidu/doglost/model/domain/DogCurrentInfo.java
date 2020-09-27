package com.shen.baidu.doglost.model.domain;

/**
 * 记录小狗此时的信息
 */
public class DogCurrentInfo {

    // 经度
    private double longitude;
    // 纬度
    private double latitude;
    // 电量
    private int battery;
    // 状态
    private byte dogStatus;
    // 备用
    private int other;

    private DogCurrentInfo(Builder builder) {
        this.longitude = builder.longitude;
        this.latitude = builder.latitude;
        this.battery = builder.battery;
        this.dogStatus = builder.dogStatus;
        this.other = builder.other;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public void setDogStatus(byte dogStatus) {
        this.dogStatus = dogStatus;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public double getLatitude() {
        return latitude;
    }


    public int getBattery() {
        return battery;
    }


    public int getDogStatus() {
        return dogStatus;
    }


    public int getOther() {
        return other;
    }


    public static class Builder {
        // 经度
        double longitude;
        // 纬度
        double latitude;
        // 电量
        int battery;
        // 状态
        byte dogStatus;
        // 备用
        int other;

        public Builder lon(double lon) {
            this.longitude = lon;
            return this;
        }

        public Builder lat(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder bat(int battery) {
            this.battery = battery;
            return this;
        }

        public Builder status(byte dogStatus) {
            this.dogStatus = dogStatus;
            return this;
        }

        public Builder others(int other) {
            this.other = other;
            return this;
        }

        public DogCurrentInfo build() {
            return new DogCurrentInfo(this);
        }
    }
}
