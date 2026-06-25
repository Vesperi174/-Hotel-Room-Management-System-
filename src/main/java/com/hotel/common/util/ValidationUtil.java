package com.hotel.common.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern ID_NUMBER_PATTERN = Pattern.compile("^\\d{17}[\\dXx]$");

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidIdNumber(String idNumber) {
        return idNumber != null && ID_NUMBER_PATTERN.matcher(idNumber).matches();
    }
}