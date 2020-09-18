package com.shen.baidu.doglost.utils;

public class DataHandlerUtil {
    /**
     * Crc校验。
     * @param buffer
     * @param length
     * @return
     */
    public static int Crc16Sum(byte[] buffer, int length) {
        int crc = 0xFFFF;
        byte i, j;
        for (i = 0; i < length; i++) {
            crc ^= (buffer[i] & 0xFF);
            for (j = 0; j < 8; j++) {
                if ((crc & 0x01) == 0x01)//
                {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }
        return crc & 0xffff;
    }


    public static float getFloat(byte[] bytes)
    {
//        return Float.intBitsToFloat(getInt(bytes));
        int l;
        l = bytes[0];
        l &= 0xff;
        l |= ((long) bytes[1] << 8);
        l &= 0xffff;
        l |= ((long) bytes[2] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[3] << 24);
        return Float.intBitsToFloat(l);

    }

//    private static int getInt(byte[] bytes)
//    {
//        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
//    }
}
