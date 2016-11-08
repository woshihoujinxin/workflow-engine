package com.workflow.engine.core.common.utils;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by liqiang on 3/3/15.
 */
public class DoubleUtils {

    public static final Double DOUBLE_ZERO = 0.0;

    public static double doubleValue(Double doubleObj){
        return doubleObj == null ? 0.0 : doubleObj.doubleValue();
    }

    public static boolean isNotZero(Double double1) {
        return !equalsDouble(DOUBLE_ZERO, doubleValue(double1));
    }

    public static boolean moreThanZero(Double double1){
        return (double1 != null) && (Double.compare(double1, DOUBLE_ZERO) > 0);
    }

    public static boolean equalsDouble(Double double1, double double2){
        return (double1 != null) && (Double.compare(double1, double2) == 0);
    }

    public static Double add(Double... doubles) {
        double result = DOUBLE_ZERO;
        for (Double double1 : doubles){
            result += doubleValue(double1);
        }

        return result;
    }

    public static Double sub(Double first, Double second){
        return doubleValue(first) - doubleValue(second);
    }

    /**
     * double类型数字相加除
     *
     * @param first
     * @param second
     * @param scale（保留几位）
     * @return
     */
    public static Double div(Double first, Double second, int scale) {
        scale = scale < 0? 2 : scale;
        BigDecimal b1 = new BigDecimal(String.valueOf(first));
        BigDecimal b2 = new BigDecimal(String.valueOf(second));
        return new Double(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * 两数相乘
     *
     * @param first
     * @param second
     * @return
     */
    public static Double mul(Double first, Double second, int scale) {
        scale = scale < 0? 2 : scale;
        BigDecimal b1 = new BigDecimal(String.valueOf(first));
        BigDecimal b2 = new BigDecimal(String.valueOf(second));
        return new Double(b1.multiply(b2).setScale(scale).doubleValue());
    }

    public static Double displayDoubleValue(Double double1){
        return Double.valueOf(new DecimalFormat("#.##").format(doubleValue(double1)));
    }

    public static String formatStripTrailingZeros(Double value){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        return numberFormat.format(value);
    }

    public static String displayAmountByKilo(long long1){
        NumberFormat nf = new DecimalFormat("#,###");
        String str = nf.format(long1);
        return str;
    }

    public static Double positiveDouble(Double value){
        return value == null || value < 0 ? 0.00 : value;
    }

    public static void main(String arg[]){
        Double ttt= null;
        System.out.println(DoubleUtils.isNotZero(ttt));
    }
}
