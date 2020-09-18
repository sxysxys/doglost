package com.shen.baidu.doglost.model.domain;


import com.shen.baidu.doglost.constant.Const;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.shen.baidu.doglost.utils.DataHandlerUtil.Crc16Sum;

/**
 * app给服务器发送心跳包的帧格式
 */
public class SendBean implements IPulseSendable {

    public byte[] buffer = new byte[]{(byte) 0xFF,(byte) 0xE2,Const.sendLength, Const.deviceId,0x00,0x00,0x00, 0x00, 0x0D, 0x0A};

    private static SendBean sendBean;

    private SendBean(){}

    public static SendBean getInstance() {
        if (sendBean == null) {
            sendBean = new SendBean();
        }
        return sendBean;
    }

    /**
     * 修改第四个
     * @param flag
     */
    public void setMFlag(byte flag) {
        buffer[4] = flag;
    }

    public byte getMFlag() {
        return buffer[4];
    }


    @Override
    public byte[] parse() {
        ByteBuffer bb = ByteBuffer.allocate(10);
        bb.order(ByteOrder.BIG_ENDIAN);
        // 再对buffer进行crc校验
        int crc = Crc16Sum(buffer, 6);
        byte crcLow = (byte) (crc >> 8);
        byte crcHigh = (byte) crc;

        byte[] sendBytes = getBytes(crcLow, crcHigh);
        bb.put(sendBytes);
        return bb.array();
    }

    private byte[] getBytes(byte crcLow, byte crcHigh) {
        byte[] sendBytes = new byte[10];
        System.arraycopy(buffer, 0, sendBytes, 0, 6);
        sendBytes[6] = crcLow;
        sendBytes[7] = crcHigh;
        sendBytes[8] = (byte) 0x0D;
        sendBytes[9] = (byte) 0x0A;
        return sendBytes;
    }
}