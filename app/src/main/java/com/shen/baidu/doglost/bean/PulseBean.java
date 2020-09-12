package com.shen.baidu.doglost.bean;


import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * app给服务器发送心跳包的帧格式
 */
public class PulseBean implements IPulseSendable {

    private byte[] buffer = new byte[]{(byte) 0xFF, (byte)0xE2, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0D, 0x0A};

    @Override
    public byte[] parse() {
        ByteBuffer bb = ByteBuffer.allocate(9);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(buffer);
        return bb.array();
    }
}