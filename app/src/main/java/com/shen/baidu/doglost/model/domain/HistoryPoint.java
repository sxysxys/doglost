package com.shen.baidu.doglost.model.domain;

import java.util.List;

/**
 * 历史数据点的格式。
 */
public class HistoryPoint {

    /**
     * code : 200
     * msg : 成功
     * data : [{"id":1,"deviceId":1,"latitude":1,"longitude":1,"cmd":1,"createDate":"2020-09-14T16:30:02.000+0800","updateDate":"2020-09-14T16:30:00.000+0800","status":1},{"id":2,"deviceId":1,"latitude":2,"longitude":2,"cmd":1,"createDate":"2020-09-14T16:30:24.000+0800","updateDate":"2020-09-14T16:30:20.000+0800","status":1}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * deviceId : 1
         * latitude : 1
         * longitude : 1
         * cmd : 1
         * createDate : 2020-09-14T16:30:02.000+0800
         * updateDate : 2020-09-14T16:30:00.000+0800
         * status : 1
         */

        private int id;
        private int deviceId;
        private float latitude;
        private float longitude;
        private int cmd;
        private String createDate;
        private String updateDate;
        private int status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public float getLatitude() {
            return latitude;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        public int getCmd() {
            return cmd;
        }

        public void setCmd(int cmd) {
            this.cmd = cmd;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
