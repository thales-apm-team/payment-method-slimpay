package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.Amount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Currency;

import static com.payline.payment.slimpay.utils.PluginUtils.truncateError;
import static com.payline.payment.slimpay.utils.SlimpayConstants.ERROR_MAX_LENGTH;

public class PluginUtilsTest {

    private Currency currency = Currency.getInstance("EUR");
    private BigInteger int1 = BigInteger.ZERO;
    private BigInteger int2 = BigInteger.ONE;
    private BigInteger int3 = BigInteger.TEN;
    private BigInteger int4 = BigInteger.valueOf(100);
    private BigInteger int5 = BigInteger.valueOf(1000);

    @Test
    public void createStringAmount() {
        Assertions.assertNull(PluginUtils.createStringAmount(null));
        Assertions.assertEquals("0.00", PluginUtils.createStringAmount(new Amount(int1, currency)));
        Assertions.assertEquals("0.01", PluginUtils.createStringAmount(new Amount(int2, currency)));
        Assertions.assertEquals("0.10", PluginUtils.createStringAmount(new Amount(int3, currency)));
        Assertions.assertEquals("1.00", PluginUtils.createStringAmount(new Amount(int4, currency)));
        Assertions.assertEquals("10.00", PluginUtils.createStringAmount(new Amount(int5, currency)));
    }

    @Test
    public void createFloatAmount() {
        Assertions.assertNull(PluginUtils.createFloatAmount(null));
        Assertions.assertEquals(new Float("00.00"), PluginUtils.createFloatAmount(new Amount(int1, currency)));
        Assertions.assertEquals(new Float("00.01"), PluginUtils.createFloatAmount(new Amount(int2, currency)));
        Assertions.assertEquals(new Float("00.10"), PluginUtils.createFloatAmount(new Amount(int3, currency)));
        Assertions.assertEquals(new Float("1.00"), PluginUtils.createFloatAmount(new Amount(int4, currency)));
        Assertions.assertEquals(new Float("10.00"), PluginUtils.createFloatAmount(new Amount(int5, currency)));
    }

    @Test
    public void truncate(){
        Assertions.assertEquals("t", PluginUtils.truncate("this is a very long message", 1));
    }

    @Test
    public void getHonorificCode(){
        Assertions.assertEquals(null, PluginUtils.getHonorificCode(null));
        Assertions.assertEquals("Mr", PluginUtils.getHonorificCode("4"));
        Assertions.assertEquals("Mr", PluginUtils.getHonorificCode("5"));
        Assertions.assertEquals("Mrs", PluginUtils.getHonorificCode("1"));
        Assertions.assertEquals("Mrs", PluginUtils.getHonorificCode("2"));
        Assertions.assertEquals("Mrs", PluginUtils.getHonorificCode("6"));
        Assertions.assertEquals("Miss", PluginUtils.getHonorificCode("3"));
        Assertions.assertEquals("Mr", PluginUtils.getHonorificCode("-1"));
    }

    @Test
    public void truncateErrorTest()
    {
        String longText ="I don't think this will (always?) work -- the one piror will be the most recent commit that was merged in from the other branch -- it won't be the most recent commit on the current branch. Right? (This ";
        String truncatedText = truncateError(longText);
        Assertions.assertEquals(50,truncatedText.length());
        Assertions.assertEquals(ERROR_MAX_LENGTH,truncatedText.length());
        Assertions.assertTrue(longText.contains(truncatedText));
    }

}
