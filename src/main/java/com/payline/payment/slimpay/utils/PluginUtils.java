package com.payline.payment.slimpay.utils;


import java.math.BigInteger;
import java.util.Currency;

public class PluginUtils {


    private PluginUtils() {
        // ras.
    }

    public static boolean isEmpty(String s) {

        return s == null || s.isEmpty();
    }

    /**
     * Return a string which was converted from cents to euro
     *
     * @param amount
     * @return
     */
    public static String createStringAmount(BigInteger amount, Currency currency) {
        //récupérer le nombre de digits dans currency
        int nbDigits = currency.getDefaultFractionDigits();

        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - nbDigits, ".");
        return sb.toString();
    }

    /**
     * Return a Float which was converted from cents to euro
     *
     * @param amount
     * @return
     */
    public static Float createFloatAmount(BigInteger amount, Currency currency) {
        if (amount == null || currency == null) {
            return null;
        }
        return Float.parseFloat(createStringAmount(amount, currency));
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    public static String getHonorificCode(String civility) {

        if (civility == null) {
            return null;
        }
        switch (civility.toLowerCase()) {
            //MR
            case "4":
            case "5":
                return "Mr";
            //MME
            case "1":
            case "2":
            case "6":
                return "Mrs";
            //MLLE
            case "3":
                return "Miss";
            default:
                return "Mr";
        }
    }

}