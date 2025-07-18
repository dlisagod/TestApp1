package com.example.androidvideocachedemo.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @create zhl
 * @date 2025/5/14 18:00
 * @description
 * @update
 * @date
 * @description
 **/
public class CalUtil {
    public static String getAboveSecond(Double num) {
        //格式化小数
        DecimalFormat df = new DecimalFormat("0.00");
        //返回的是String类型
        String num1 = df.format(num);
        // String转double/float
//        float fx = Float.parseFloat(num);
//        double dy = Double.parseDouble(num);
        return num1;
    }

    public static double calMBytes(long length) {
        return new BigDecimal(length / 1024 / 1024)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * 根据字节数获取文件大小，自动转成Byte、KByte、MByte、GByte
     */
    public static String getSizeByByte(long sizeByte) {
        if (sizeByte < 0) {
            throw new IllegalArgumentException("sizeByte < 0");
        }

        if (sizeByte < 1024) {
            return sizeByte + "B";
        }

        double divider1 = (double) sizeByte / 1024;
        if (divider1 < 1024.0) {
            return String.format("%.2fKB", divider1);
        }

        double divider2 = divider1 / 1024;
        if (divider2 < 1024.0) {
            return String.format("%.2fMB", divider2);
        }

        double divider3 = divider2 / 1024;
        return String.format("%.2fGB", divider3);
    }
}
