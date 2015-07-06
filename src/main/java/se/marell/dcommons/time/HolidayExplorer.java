/*
 * Copyright (c) 2003,2012 Daniel Marell
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

import java.time.LocalDate;
import java.util.List;

/**
 * Explores holidays for a year or given a date.
 */
public interface HolidayExplorer {
    /**
     * Check if a given date is a holiday or not
     *
     * @param date Local date to be checked
     * @return null if no holiday or a description of the holidays, if more than one, separated with ","
     */
    String getHoliday(LocalDate date);

    /**
     * Check if a given date is a holiday or not
     *
     * @param date      Date to be checked
     * @param separator String to separate holiday strings if more than one
     * @return null if no holiday or a description of the holiday
     */
    String getHoliday(LocalDate date, String separator);

    /**
     * Check holidays for a given date.
     *
     * @param date Date to be checked
     * @return List of holiday on that day or empty list if none
     */
    List<String> getHolidayList(LocalDate date);

    /**
     * Get all holidays for the given year.
     *
     * @param year 4-digit year
     * @return List of holidays
     */
    List<Holiday> getHolidays(int year);
}
