package com.shen.baidu.doglost.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

public class DataHandlerUtil {

    private static CoordinateConverter converter;
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

    public static double getFloat2(byte[] bytes) {
        return ((bytes[0] & 0x0ff) + bytes[1] * 0.01 + bytes[2] * 0.0001 + bytes[3] * 0.000001);
    }

    /**
     * 转化坐标
     * @param lat
     * @param lon
     * @return
     */
    public static LatLng changeGps2Bai(double lat, double lon) {
        if (converter == null) {
            converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
        }
        LatLng sourceLatLng = new LatLng(lat, lon);
        converter.coord(sourceLatLng);
        return converter.convert();
    }

    public static boolean isEqual(double a,double b)
    {
        return Math.abs(a-b) < 0.000001;
    }

//    private static int getInt(byte[] bytes)
//    {
//        return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
//    }
}
