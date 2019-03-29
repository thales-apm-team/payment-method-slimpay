package com.payline.payment.slimpay.utils;

import com.payline.pmapi.bean.common.Amount;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Currency;
import java.util.Locale;

import static com.payline.payment.slimpay.utils.PluginUtils.*;
import static com.payline.payment.slimpay.utils.SlimpayConstants.ERROR_MAX_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

public class PluginUtilsTest {

    private Currency currency = Currency.getInstance("EUR");
    private BigInteger int1 = BigInteger.ZERO;
    private BigInteger int2 = BigInteger.ONE;
    private BigInteger int3 = BigInteger.TEN;
    private BigInteger int4 = BigInteger.valueOf(100);
    private BigInteger int5 = BigInteger.valueOf(1000);

    @Test
    public void createStringAmount() {
        assertNull(PluginUtils.createStringAmount(null));
        assertEquals("0.00", PluginUtils.createStringAmount(new Amount(int1, currency)));
        assertEquals("0.01", PluginUtils.createStringAmount(new Amount(int2, currency)));
        assertEquals("0.10", PluginUtils.createStringAmount(new Amount(int3, currency)));
        assertEquals("1.00", PluginUtils.createStringAmount(new Amount(int4, currency)));
        assertEquals("10.00", PluginUtils.createStringAmount(new Amount(int5, currency)));
    }

    @Test
    public void createFloatAmount() {
        assertNull(PluginUtils.createFloatAmount(null));
        assertEquals(new Float("00.00"), PluginUtils.createFloatAmount(new Amount(int1, currency)));
        assertEquals(new Float("00.01"), PluginUtils.createFloatAmount(new Amount(int2, currency)));
        assertEquals(new Float("00.10"), PluginUtils.createFloatAmount(new Amount(int3, currency)));
        assertEquals(new Float("1.00"), PluginUtils.createFloatAmount(new Amount(int4, currency)));
        assertEquals(new Float("10.00"), PluginUtils.createFloatAmount(new Amount(int5, currency)));
    }

    @Test
    public void truncate() {
        assertEquals("t", PluginUtils.truncate("this is a very long message", 1));
    }

    @Test
    public void getHonorificCode() {
        assertEquals(null, PluginUtils.getHonorificCode(null));
        assertEquals("Mr", PluginUtils.getHonorificCode("4"));
        assertEquals("Mr", PluginUtils.getHonorificCode("5"));
        assertEquals("Mrs", PluginUtils.getHonorificCode("1"));
        assertEquals("Mrs", PluginUtils.getHonorificCode("2"));
        assertEquals("Mrs", PluginUtils.getHonorificCode("6"));
        assertEquals("Miss", PluginUtils.getHonorificCode("3"));
        assertEquals("Mr", PluginUtils.getHonorificCode("-1"));
    }

    @Test
    public void truncateErrorTest() {
        String longText = "I don't think this will (always?) work -- the one piror will be the most recent commit that was merged in from the other branch -- it won't be the most recent commit on the current branch. Right? (This ";
        String truncatedText = truncateError(longText);
        assertEquals(50, truncatedText.length());
        assertEquals(ERROR_MAX_LENGTH, truncatedText.length());
        assertTrue(longText.contains(truncatedText));
    }

    @Test
    public void convertToE164_nothingToDo(){
        assertEquals( "+33601020304", PluginUtils.convertToInternational( "+33601020304", Locale.FRANCE ));
        assertEquals( "+331234", PluginUtils.convertToInternational( "+331234", Locale.FRANCE ));
    }

    @Test
    public void convertToE164_removeDots(){
        assertEquals( "+33601020304", PluginUtils.convertToInternational( "+336.01.02.03.04", Locale.FRANCE ));
    }

    @Test
    public void convertToE164_removeSpaces(){
        assertEquals( "+33601020304", PluginUtils.convertToInternational( "+336 01 02 03 04", Locale.FRANCE ));
    }

    @Test
    public void convertToE164_removeDashes(){
        assertEquals( "+33601020304", PluginUtils.convertToInternational( "+336-010-203-04", Locale.FRANCE ));
    }

    @Test
    public void convertToE164_removeAll(){
        assertEquals( "+33601020304", PluginUtils.convertToInternational( "+336 010-203.04", Locale.FRANCE ));
    }

    @Test
    public void convertToE164_addCountryCode_noCountryFr(){
        assertEquals( "0601020304", PluginUtils.convertToInternational( "0601020304", Locale.FRENCH ));
    }

    @Test
    public void convertToE164_addCountryCode_fr(){
        assertEquals( "+33601020304", PluginUtils.convertToInternational( "0601020304", Locale.FRANCE ));
    }

    @Test
    public void convertToE164_addCountryCode_be_fr(){
        assertEquals( "+32451234567", PluginUtils.convertToInternational( "0451 23 45 67", new Locale("fr", "be") ));
    }

    @Test
    public void convertToE164_addCountryCode_be_nl(){
        assertEquals( "+32451234567", PluginUtils.convertToInternational( "0451 23 45 67", new Locale("nl", "be") ));
    }

    @Test
    public void convertToE164_addCountryCode_nl(){
        assertEquals( "+31123456789", PluginUtils.convertToInternational( "012 345 67 89", new Locale("nl", "nl") ));
    }

    @Test
    public void convertToE164_addCountryCode_noCountryEn(){
        assertEquals( "02041345678", PluginUtils.convertToInternational( "020 4134 5678", Locale.ENGLISH ));
    }

    @Test
    public void convertToE164_addCountryCode_uk(){
        assertEquals( "+442041345678", PluginUtils.convertToInternational( "020 4134 5678", Locale.UK ));
    }

    @Test
    public void convertToE164_addCountryCode_us(){
        assertEquals( "+12135096995", PluginUtils.convertToInternational( "213-509-6995", Locale.US ));
    }

}
