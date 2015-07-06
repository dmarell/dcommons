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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScheduler<T> {
    protected TimeSource timeSource;
    protected HolidayExplorer holidayExplorer;
    protected List<SchedulerEvent<T>> events = new ArrayList<>();

    protected AbstractScheduler(TimeSource timeSource, HolidayExplorer holidayExplorer) {
        this.timeSource = timeSource;
        this.holidayExplorer = holidayExplorer;
    }

    /**
     * Add event to this EventScheduler.
     *
     * @param event Event to add
     */
    public void addEvent(SchedulerEvent<T> event) {
        for (int i = 0; i < events.size(); ++i) {
            SchedulerEvent<T> e = events.get(i);
            if (e.getTime().isAfter(event.getTime())) {
                events.add(i, event);
                return;
            }
        }
        events.add(event);
    }

    /**
     * Remove the specified event from this EventScheduler.
     *
     * @param event Event instance to remove
     */
    public void removeEvent(SchedulerEvent<T> event) {
        events.remove(event);
    }

    /**
     * Get events in this EventScheduler.
     *
     * @return Event list
     */
    public List<SchedulerEvent<T>> getEvents() {
        return events;
    }

    /**
     * Add events to this EventScheduler.
     *
     * @param events Event list
     */
    public void addEvents(List<SchedulerEvent<T>> events) {
        for (SchedulerEvent<T> e : events) {
            addEvent(e);
        }
    }

    /**
     * Remove all events from this EventScheduler.
     */
    public void clearEvents() {
        events.clear();
    }

    protected boolean dayModifierApplies(LocalDate c, SchedulerEvent<T> e) {
        if (e.getOccurrence() == SchedulerOccurrence.Daily) {
            return true;
        }

        if (e.getOccurrence() == SchedulerOccurrence.Weekly) {
            DayOfWeek dayOfWeek = c.getDayOfWeek();
            boolean todayIsHoliday = holidayExplorer.getHoliday(c) != null;
            if (e.getIsWorkday() != null) {
                if (e.getIsWorkday()) {
                    // Not a holiday, a saturday or a sunday
                    return !todayIsHoliday && dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
                }
                // Any holiday, a saturday or a sunday
                return todayIsHoliday || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
            }

            return dayOfWeek == e.getWeekday();
        }

        if (e.getOccurrence() == SchedulerOccurrence.Monthly) {
            // Every month at dayOfMonth
            return c.getDayOfMonth() == e.getDayOfMonth();
        }

        if (e.getOccurrence() == SchedulerOccurrence.Yearly) {
            // Every year at month and dayOfMonth
            return c.getMonthValue() == e.getMonth() && c.getDayOfMonth() == e.getDayOfMonth();
        }

        // A specific date
        assert e.getOccurrence() == SchedulerOccurrence.Once;
        return c.getYear() == e.getYear() && c.getMonthValue() == e.getMonth() && c.getDayOfMonth() == e.getDayOfMonth();
    }
}
