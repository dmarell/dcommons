/*
 * Copyright (c) 2010,2012 Daniel Marell
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package se.marell.dcommons.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * If you ask me, ISO 8601 should be the only permitted date format on this planet.
 */
public class DateUtils {
    public static final String DATE_TIME_FORMAT_ISO8601 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_ISO8601 = "yyyy-MM-dd";

    private static SimpleDateFormat dfDateTime = new SimpleDateFormat(DATE_TIME_FORMAT_ISO8601);
    private static SimpleDateFormat dfDate = new SimpleDateFormat(DATE_FORMAT_ISO8601);

    private DateUtils() {
    }

    /**
     * Get a string with current date on the format yyyy-MM-dd.
     *
     * @return String representing todays date
     */
    public static String formatDateIso8601() {
        return formatDateIso8601(Calendar.getInstance());
    }

    /**
     * Get a string with date of c on the format yyyy-MM-dd.
     *
     * @param c Calendar object
     * @return String representing date of c
     */
    public static String formatDateIso8601(Calendar c) {
        return dfDate.format(c.getTime());
    }

    /**
     * Get a string with date of d on the format yyyy-MM-dd.
     *
     * @param d Date object
     * @return String representing date of d
     */
    public static String formatDateIso8601(Date d) {
        return dfDate.format(d);
    }


    /**
     * Get a string with current date on the format yyyy-MM-dd.
     *
     * @return String representing todays date
     */
    public static String formatDateTimeIso8601() {
        return formatDateTimeIso8601(Calendar.getInstance());
    }

    /**
     * Get a string with date and time of c on the format yyyy-MM-dd hh:mm:ss.
     *
     * @param c Calendar object
     * @return String representing date and time of c
     */
    public static String formatDateTimeIso8601(Calendar c) {
        return dfDateTime.format(c.getTime());
    }

    /**
     * Get a string with date and time of d on the format yyyy-MM-dd hh:mm:ss.
     *
     * @param d Date object
     * @return String representing date and time of d
     */
    public static String formatDateTimeIso8601(Date d) {
        return dfDateTime.format(d);
    }

    /**
     * Parse a string containing ISO 8601 date on the format yyyy-MM-dd.
     *
     * @param s String to parse
     * @return Calendar object representing date and from string or null if parsing failed
     */
    public static Calendar parseDateIso8601(String s) {
        Calendar c = Calendar.getInstance();
        try {
            Date d = dfDate.parse(s);
            c.setTime(d);
        } catch (ParseException ignore) {
            return null;
        }
        return c;
    }

    /**
     * Parse a string containing ISO 8601 date and time on the format yyyy-MM-dd HH:mm:ss.
     *
     * @param s String to parse
     * @return Calendar object representing date and time from string or null if parsing failed
     */
    public static Calendar parseDateTimeIso8601(String s) {
        Calendar c = Calendar.getInstance();
        try {
            Date d = dfDateTime.parse(s);
            c.setTime(d);
        } catch (ParseException ignore) {
            return null;
        }
        return c;
    }

    /**
     * Get Calendar object representing the specified time in millis.
     *
     * @param timeInMillis Time in milli seconds
     * @return Calendar object
     */
    public static Calendar getCalendar(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    /**
     * Get Calendar object for date.
     *
     * @param year  Year yyyy
     * @param month Month 1-12
     * @param day   Day of month 1-31
     * @return Calendar object
     */
    public static Calendar getCalendar(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c;
    }

    /**
     * Get LocalDateTime for time in milli seconds.
     *
     * @param timeInMillis Time in milli seconds
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTime(long timeInMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault());
    }

    /**
     * Get LocalDate for time in milli seconds.
     *
     * @param timeInMillis Time in milli seconds
     * @return LocalDate
     */
    public static LocalDate getLocalDate(long timeInMillis) {
        return getLocalDateTime(timeInMillis).toLocalDate();
    }

    /**
     * Get LocalDate given Calendar.
     *
     * @param c Calendar
     * @return Local date
     */
    public static LocalDate getLocalDate(Calendar c) {
        return c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
