package com.example.testapp.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    String string;

    private Test() {

    }

    Test(String str) {
        this.string = str;
    }

    public static void check() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };
        timer.schedule(timerTask, 1000);
    }

    private void checkPrivate() {

    }

    public static void testBigDecimal() {
        long a = 100L;
        long b = 300L;
        double c = new BigDecimal((double) a / b).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Log.d("test", "c = " + c);
        c += new BigDecimal((double) a / b).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        Log.d("test", "c = " + c);
    }

    public void testThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

    public void insertSortFromSmallToBig(int[] n) {
        int temp = 0, j;
        for (int i = 1; i < n.length; i++) {
            temp = n[i];
            for (j = i; j > 0; j--) {
                //如果当前数前面的数大于当前数，则把前面的数向后移一个位置
                if (n[j - 1] > temp) {
                    n[j] = n[j - 1];

                    //第一个数已经移到第二个数，将当前数放到第一个位置，这一趟结束
                    if (j == 1) {
                        n[j - 1] = temp;
                        break;
                    }

                } else {//如果不大于，将当前数放到j的位置，这一趟结束

                    n[j] = temp;
                    break;
                }
            }
            System.out.println(Arrays.toString(n));
        }
        System.out.println(Arrays.toString(n));
    }

    public static class StaticInnerTest {
        private String name;
        private static String ID;
    }

    public class InnerClass {
        private String name;
        private String ID;
    }

    public static int dp2px(Context context, float dpVal) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void logDensity(Activity context) {
        //获取分辨率
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 宽度（PX）
        int height = metric.heightPixels; // 高度（PX）
        float density = metric.density; // 密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 密度DPI（120 / 160 / 240）
//屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);//屏幕宽度(dp)
        int screenHeight = (int) (height / density);//屏幕高度(dp)
        Log.i("logDensity", "宽度:" + width + " 高度:" + height + " 密度:" + density + " 密度DPI:" + densityDpi
                + "\r\n屏幕dp宽度：" + screenWidth + " 屏幕dp高度：" + screenHeight);
    }

}

/**
 * class Test {
 * var string: String? = null
 * <p>
 * private constructor() {}
 * internal constructor(str: String?) {
 * string = str
 * }
 * <p>
 * private fun checkPrivate() {}
 * fun testThread() {
 * Thread(Runnable { }).start()
 * }
 * <p>
 * fun insertSortFromSmallToBig(n: IntArray) {
 * var temp = 0
 * var j: Int
 * for (i in 1 until n.size) {
 * temp = n[i]
 * j = i
 * while (j > 0) {
 * <p>
 * //如果当前数前面的数大于当前数，则把前面的数向后移一个位置
 * if (n[j - 1] > temp) {
 * n[j] = n[j - 1]
 * <p>
 * //第一个数已经移到第二个数，将当前数放到第一个位置，这一趟结束
 * if (j == 1) {
 * n[j - 1] = temp
 * break
 * }
 * } else { //如果不大于，将当前数放到j的位置，这一趟结束
 * n[j] = temp
 * break
 * }
 * j--
 * }
 * println(Arrays.toString(n))
 * }
 * println(Arrays.toString(n))
 * }
 * <p>
 * class StaticInnerTest {
 * private val name: String? = null
 * <p>
 * companion object {
 * private val ID: String? = null
 * }
 * }
 * <p>
 * inner class InnerClass {
 * private val name: String? = null
 * private val ID: String? = null
 * }
 * <p>
 * companion object {
 * fun check() {
 * val timer = Timer()
 * val timerTask: TimerTask = object : TimerTask() {
 * override fun run() {}
 * }
 * timer.schedule(timerTask, 1000)
 * }
 * <p>
 * fun testBigDecimal() {
 * val a = 100L
 * val b = 300L
 * var c: Double = BigDecimal(a.toDouble() / b)
 * .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
 * Log.d("test", "c = $c")
 * c += BigDecimal(a.toDouble() / b)
 * .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
 * Log.d("test", "c = $c")
 * }
 * }
 * }
 */