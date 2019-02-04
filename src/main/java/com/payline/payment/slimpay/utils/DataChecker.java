package com.payline.payment.slimpay.utils;


import java.util.Arrays;
import java.util.Locale;

public class DataChecker {

    private DataChecker() {
        // ras.
    }

    /**
     * check if a String respect ISO-3166 rules
     *
     * @param countryCode the code to compare
     * @return true if countryCode is in ISO-3166 list, else return false
     */
    public static boolean isISO3166(String countryCode) {
        return Arrays.asList(Locale.getISOCountries()).contains(countryCode);
    }

    /**
     * check if a string is a number
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        } else {
            int sz = str.length();

            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * check if a String is null or empty
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
