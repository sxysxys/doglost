package com.shen.baidu.doglost.model.domain;

/**
 * 登录验证
 */
public class ResponseLogin {

    /**
     * code : 200
     * msg : 验证成功
     * data : null
     */

    private int code;
    private String msg;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
