package com.shen.baidu.doglost.bean;

/**
 * 记录小狗此时的信息
 */
public class DogCurrentInfo {

    // 经度
    private float longitude;
    // 纬度
    private float latitude;
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

    public float getLongitude() {
        return longitude;
    }


    public float getLatitude() {
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
        float longitude;
        // 纬度
        float latitude;
        // 电量
        int battery;
        // 状态
        byte dogStatus;
        // 备用
        int other;

        public Builder lon(float lon) {
            this.longitude = lon;
            return this;
        }

        public Builder lat(float latitude) {
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
