package com.hotel.common.util;

public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        return str.matches("\\d+");
    }

    public static String maskIdNumber(String idNumber) {
        if (isEmpty(idNumber) || idNumber.length() < 8) return idNumber;
        return idNumber.substring(0, 4) + "****" + idNumber.substring(idNumber.length() - 4);
    }
}