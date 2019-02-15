package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.Amount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Currency;

public class PluginUtilsTest {

    private Currency currency = Currency.getInstance("EUR");


    @Test
    public void createStringAmount() {
        BigInteger int1 = BigInteger.ZERO;
        BigInteger int2 = BigInteger.ONE;
        BigInteger int3 = BigInteger.TEN;
        BigInteger int4 = BigInteger.valueOf(100);
        BigInteger int5 = BigInteger.valueOf(1000);

        Assertions.assertEquals("0.00", PluginUtils.createStringAmount(new Amount(int1, currency)));
        Assertions.assertEquals("0.01", PluginUtils.createStringAmount(new Amount(int2, currency)));
        Assertions.assertEquals("0.10", PluginUtils.createStringAmount(new Amount(int3, currency)));
        Assertions.assertEquals("1.00", PluginUtils.createStringAmount(new Amount(int4, currency)));
        Assertions.assertEquals("10.00", PluginUtils.createStringAmount(new Amount(int5, currency)));
    }

    @Test
    public void createFloatAmount() {
        BigInteger int1 = BigInteger.ZERO;
        BigInteger int2 = BigInteger.ONE;
        BigInteger int3 = BigInteger.TEN;
        BigInteger int4 = BigInteger.valueOf(100);
        BigInteger int5 = BigInteger.valueOf(1000);

        Assertions.assertEquals(new Float("00.00"), PluginUtils.createFloatAmount(new Amount(int1, currency)));
        Assertions.assertEquals(new Float("00.01"), PluginUtils.createFloatAmount(new Amount(int2, currency)));
        Assertions.assertEquals(new Float("00.10"), PluginUtils.createFloatAmount(new Amount(int3, currency)));
        Assertions.assertEquals(new Float("1.00"), PluginUtils.createFloatAmount(new Amount(int4, currency)));
        Assertions.assertEquals(new Float("10.00"), PluginUtils.createFloatAmount(new Amount(int5, currency)));
    }
}
