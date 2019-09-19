package com.payline.payment.slimpay.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    private SimpleDateFormat frDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

    @BeforeAll
    static void setupAll(){
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
    }

    @Test
    void format() throws ParseException {
        // winter date
        Date winterTestDate = frDateFormat.parse("01/01/2019");
        assertEquals("2019-01-01T00:00:00.000+0100", DateUtils.format( winterTestDate ));
        // summer date
        Date summerTestDate = frDateFormat.parse("01/08/2019");
        assertEquals("2019-08-01T00:00:00.000+0200", DateUtils.format( summerTestDate ));
        // null date
        assertNull( DateUtils.format(null) );
    }

    @Test
    void parse(){
        Calendar cal = GregorianCalendar.getInstance();

        // right format
        Date result = DateUtils.parse( "2019-01-01T00:00:00.000+0000" );
        cal.setTime( result );
        assertEquals(2019, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH)); // 0: January
        assertEquals(1, cal.get(Calendar.DATE));
        assertEquals(1, cal.get(Calendar.HOUR_OF_DAY)); // If it's midnight in UTC timezone (+0000), during winter, it's 1am in Paris
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));

        // wrong formats
        assertNull( DateUtils.parse("01/01/2019") ); // french style date
        assertNull( DateUtils.parse("2019-01-01T00:00:00.000+00:00") ); // colon in the timezone, not supported by SimpleDateFormat

        // null
        assertNull( DateUtils.parse( null ) );
    }

    @Test
    void addWorkingDays() throws ParseException {
        // 5 days: Thursday => Thursday
        assertEquals( frDateFormat.parse("26/09/2019"), DateUtils.addWorkingDays( frDateFormat.parse("19/09/2019"), 5 ) );
        // 2 days: Friday => Tuesday
        assertEquals( frDateFormat.parse("24/09/2019"), DateUtils.addWorkingDays( frDateFormat.parse("20/09/2019"), 2 ) );
        // 1 day: Friday => Saturday
        assertEquals( frDateFormat.parse("21/09/2019"), DateUtils.addWorkingDays( frDateFormat.parse("20/09/2019"), 1 ) );
        // 3 days: Monday before Christmas 2019 => Thuesday
        assertEquals( frDateFormat.parse("27/12/2019"), DateUtils.addWorkingDays( frDateFormat.parse("23/12/2019"), 3 ) );
    }

    @Test
    void getEasterSunday(){
        assertEquals("21/04/2019", frDateFormat.format(DateUtils.getEasterSunday(2019)));
        assertEquals("12/04/2020", frDateFormat.format(DateUtils.getEasterSunday(2020)));
        assertEquals("04/04/2021", frDateFormat.format(DateUtils.getEasterSunday(2021)));
    }

    @Test
    void isFrenchPublicHoliday() throws ParseException {
        assertTrue( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("01/01/2019") ) ); // new year's eve 2019
        assertTrue( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("22/04/2019") ) ); // easter monday 2019
        assertTrue( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("05/04/2021") ) ); // easter monday 2021
        assertTrue( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("14/07/2023") ) ); // national day 2023
        assertTrue( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("25/12/2158") ) ); // christmas 2158
        assertTrue( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("01/05/2022") ) ); // labour day 2022

        assertFalse( DateUtils.isFrenchPublicHoliday( frDateFormat.parse("01/02/2019") ) );
    }

}
