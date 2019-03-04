package com.payline.payment.slimpay.utils;


import com.payline.pmapi.bean.common.Amount;

import java.math.BigInteger;
import java.util.Currency;

import static com.payline.payment.slimpay.utils.SlimpayConstants.ERROR_MAX_LENGTH;

public class PluginUtils {

    public static final String PHONE_CHECKER_REGEX = "^\\+?[1-9]\\d{1,14}$";
    //french phone number e164
    public static final String FRENCH_PHONE_CHECKER_REGEX = "^\\+33?[1-9]\\d{9}$";

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
    public static String createStringAmount(Amount amount) {
        if (amount == null || amount.getAmountInSmallestUnit() == null || amount.getCurrency() == null) {
            return null;
        }
        Currency currency = amount.getCurrency();
        BigInteger amountInSmallestUnit = amount.getAmountInSmallestUnit();

        //récupérer le nombre de digits dans currency
        int nbDigits = currency.getDefaultFractionDigits();

        StringBuilder sb = new StringBuilder();
        sb.append(amountInSmallestUnit);

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
    public static Float createFloatAmount(Amount amount) {
        if (amount == null) return null;
        return Float.parseFloat(createStringAmount(amount));
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

    /**
     * Truncate a String to max length define in SlimpayConstants class
     *
     * @param error
     * @return
     */
    public static String truncateError(String error) {
        return PluginUtils.truncate(error, ERROR_MAX_LENGTH);
    }

    /**
     * convert phone number to e164 format
     *
     * @param telephone
     * @return
     */
    public static String toInternationalFrenchNumber(String telephone) {
        //suppress white space
        String telWithoutSpace = telephone.replace(" ", "");
        //suppress stripes
         telWithoutSpace = telWithoutSpace.replace("-", "");
        //suppress dots
        telWithoutSpace = telWithoutSpace.replace(".", "");
        //Tester si respecter regex
        if (!(telWithoutSpace.matches(FRENCH_PHONE_CHECKER_REGEX))) {
            //replace 00 ou 0X  par +33
            if (telWithoutSpace.startsWith("00")) {
                telWithoutSpace = telWithoutSpace.replace("00", "+");
            } else if (telWithoutSpace.startsWith("06")|| telWithoutSpace.startsWith("07")) {
                telWithoutSpace = telWithoutSpace.replace("0", "+33");
            }
            //else this is not a French mobile number

        }
        return telWithoutSpace;
    }


}