package com.shen.baidu.doglost.model.domain;


import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * app给服务器发送心跳包的帧格式
 */
public class SendBean implements IPulseSendable {

    private byte[] buffer;

    public SendBean(byte[] temp) {
        buffer = temp;
    }

    @Override
    public byte[] parse() {
        ByteBuffer bb = ByteBuffer.allocate(10);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(buffer);
        return bb.array();
    }
}