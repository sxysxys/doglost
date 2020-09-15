package com.shen.baidu.doglost.model.domain;


import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 用来发送消息，送入要发送的数据
 */

public class MsgDataBean implements ISendable {

    private byte[] content;

    public MsgDataBean(String str) {
        this.content = str.getBytes();
    }

    public MsgDataBean(byte[] content) {
        this.content = content;
    }


    @Override
    public byte[] parse() {
        ByteBuffer bb = ByteBuffer.allocate(content.length + 3);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put((byte) 0xFF);
        bb.put((byte) 0xE2);
        bb.put((byte)(content.length & 0xFF));
        bb.put(content);
        return bb.array();
    }
}
