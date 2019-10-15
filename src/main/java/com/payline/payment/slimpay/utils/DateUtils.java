package com.payline.payment.slimpay.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class to manage conversions between Slimpay string-formatted dates and {@link java.util.Date} instances.
 */
public class DateUtils {

    private static final List<String> FIXED_FRENCH_PUBLIC_HOLIDAYS = new ArrayList<>(Arrays.asList(
            "01/01", // New year's day
            "01/05", // Labour day
            "08/05", // Victory day
            "14/07", // National day
            "15/08", // Assumption
            "01/11", // All saint's day
            "11/11", // Armistice
            "25/12" // Christmas
            ));

    /**
     * Slimpay specific ISO8601-inspired format : the extended form of ISO 8601, with milliseconds and without the colon in the timezone.
     */
    private static final String SLIMPAY_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private DateUtils() {
    }

    /**
     * Formats a date with Slimpay ISO8601-inspired format.
     *
     * @param date the date to format
     * @return the formatted datetime string, or <code>null</code> if the given date is <code>null</code>
     */
    public static String format( Date date ){
        String string = null;
        if( date != null ){
            string = new SimpleDateFormat( SLIMPAY_FORMAT ).format( date );
        }
        return string;
    }

    /**
     * Parses a string using Slimpay ISO8601-inspired format to generate a <code>java.util.Date</code> instance.
     *
     * @param strDate the string to parse
     * @return the <code>Date</code> parsed from the string, or <code>null</code> if an error occurred during the parsing.
     */
    public static Date parse( String strDate ){
        Date date;
        try {
            date = new SimpleDateFormat( SLIMPAY_FORMAT ).parse( strDate );
        } catch (ParseException | NullPointerException e) {
            date = null;
        }
        return date;
    }

    /**
     * Add the given number of (french) working days to the given date.
     * The final date is not necessarily a working day.
     * Ex: starting Friday, if we add 1 working day, we simply add the current day. So the result will be the following Saturday.
     * Still starting Friday, if we add 3 working days, we add the current day and the following Monday. So the result would be Tuesday.
     *
     * @param date the initial date
     * @param days the number of days to add
     * @return the new date
     */
    public static Date addWorkingDays( Date date, int days ){
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime( date );
        int n = 0;
        while( n < days ){
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if( dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && !isFrenchPublicHoliday( cal.getTime() ) ){
                n++;
            }
            cal.add(Calendar.DATE, 1);
        }
        return cal.getTime();
    }

    /**
     * Returns <code>true</code> if the given date is a french public holiday, <code>false</code> otherwise
     *
     * @param date the date to test
     */
    public static boolean isFrenchPublicHoliday( Date date ){
        List<String> publicHolidays = FIXED_FRENCH_PUBLIC_HOLIDAYS;

        SimpleDateFormat dayMonthformatter = new SimpleDateFormat("dd/MM");
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime( date );

        // Easter sunday
        Date easter = getEasterSunday( cal.get(Calendar.YEAR) );

        // Easter monday
        cal.setTime(easter);
        cal.add(Calendar.DATE, 1);
        publicHolidays.add( dayMonthformatter.format( cal.getTime() ) );

        // Ascension
        cal.add(Calendar.DATE, 38);
        publicHolidays.add( dayMonthformatter.format( cal.getTime() ) );

        // Whit monday
        cal.add(Calendar.DATE, 11);
        publicHolidays.add( dayMonthformatter.format( cal.getTime() ) );

        return publicHolidays.contains( dayMonthformatter.format( date ) );
    }

    /**
     * Calculates the date of Easter sunday for the given year, following Butcher-Meeus algorithm.
     *
     * @param year the year
     * @return the <code>Date</code> of Easter sunday
     */
    public static Date getEasterSunday( int year ){
        int n = year % 19;
        int c = year / 100;
        int u = year % 100;
        int s = c / 4;
        int t = c % 4;
        int p = (c + 8) / 25;
        int q = (c - p + 1) / 3;
        int e = (19*n + c - s - q + 15) % 30;
        int b = u / 4;
        int d = u % 4;
        int l = (2*t + 2*b - e - d + 32) % 7;
        int h = (n + 11*e + 22*l) / 451;
        int x = (e + l - 7*h + 114);
        int m = x / 31;
        int j = x % 31;
        Calendar cal = GregorianCalendar.getInstance(Locale.FRANCE);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, m-1);
        cal.set(Calendar.DATE, j+1);
        return cal.getTime();
    }

}
