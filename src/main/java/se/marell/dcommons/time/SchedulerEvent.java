/*
 * Copyright (c) 2002,2011 Daniel Marell
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

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Represents a scheduled event.
 */
public class SchedulerEvent<T> {
    private LocalTime time;
    private SchedulerOccurrence occurrence;
    private int year;
    private int month;
    private int dayOfMonth;
    private DayOfWeek weekday;
    private Boolean isWorkday;
    private T state;

    /**
     * Daily event.
     *
     * @param time  Time of day
     * @param state Event state
     */
    public SchedulerEvent(LocalTime time, T state) {
        this.time = time;
        this.occurrence = SchedulerOccurrence.Daily;
        this.state = state;
    }

    /**
     * Weekly event.
     *
     * @param time    Time of day
     * @param weekday WeekDay
     * @param state   Event state
     */
    public SchedulerEvent(LocalTime time, DayOfWeek weekday, T state) {
        this.time = time;
        this.occurrence = SchedulerOccurrence.Weekly;
        this.weekday = weekday;
        this.state = state;
    }

    /**
     * Weekly event.
     *
     * @param time      Time of day
     * @param isWorkday True if working day, else a non working day
     * @param state     Event state
     */
    public SchedulerEvent(LocalTime time, boolean isWorkday, T state) {
        this.time = time;
        this.occurrence = SchedulerOccurrence.Weekly;
        this.isWorkday = isWorkday;
        this.state = state;
    }

    /**
     * Monthly event.
     *
     * @param time       Time of day
     * @param dayOfMonth Day of month 1-31
     * @param state      Event state
     */
    public SchedulerEvent(LocalTime time, int dayOfMonth, T state) {
        this.time = time;
        this.occurrence = SchedulerOccurrence.Monthly;
        this.dayOfMonth = dayOfMonth;
        this.state = state;
    }

    /**
     * Yearly event.
     *
     * @param time       Time of day
     * @param month      Month 1-12
     * @param dayOfMonth Day of month 1-31
     * @param state      Event state
     */
    public SchedulerEvent(LocalTime time, int month, int dayOfMonth, T state) {
        this.time = time;
        this.occurrence = SchedulerOccurrence.Yearly;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.state = state;
    }

    /**
     * Single event.
     *
     * @param time       Time of day
     * @param year       Year, 4 digits
     * @param month      Month 1-12
     * @param dayOfMonth Day of month 1-31
     * @param state      Event state
     */
    public SchedulerEvent(LocalTime time, int year, int month, int dayOfMonth, T state) {
        this.time = time;
        this.occurrence = SchedulerOccurrence.Once;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.state = state;
    }

    /**
     * @return Time of day
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * @return Occurrence
     */
    public SchedulerOccurrence getOccurrence() {
        return occurrence;
    }

    /**
     * @return Weekday
     */
    public DayOfWeek getWeekday() {
        return weekday;
    }

    /**
     * @return true if working day
     */
    public Boolean getIsWorkday() {
        return isWorkday;
    }

    /**
     * @return Year, e.g., 2012
     */
    public int getYear() {
        return year;
    }

    /**
     * @return Month 1-12
     */
    public int getMonth() {
        return month;
    }

    /**
     * @return Day of month 1-31
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * @return Event state
     */
    public T getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Event{" +
                "time=" + time +
                ", occurrence=" + occurrence +
                ", year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", weekday=" + weekday +
                ", isWorkday=" + isWorkday +
                ", state=" + state +
                '}';
    }
}
