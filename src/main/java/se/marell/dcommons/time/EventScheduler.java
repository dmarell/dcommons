/*
 * Copyright (c) 2014 Daniel Marell
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Schedules events at specified times and days.
 *
 * Usage example:
 <pre>
 <code>
 {@code
 EventScheduler{@code<Boolean>} scheduler = new EventScheduler<>(h);
 scheduler.addEvent(new SchedulerEvent{@code <>}(LocalTime.parse("19:00:00"), true));
 scheduler.addEvent(new SchedulerEvent{@code <>}(LocalTime.parse("23:00:00"), false));

 while (true) {
   SchedulerEvent{@code <Boolean>} e = scheduler.consumeEvent();
   if (e != null) {
     turnLampSwitch(e.getState());
   }
   Thread.sleep(1000);
 }
 }
 </code>
 </pre>
 *
 * @param <T> Event payload data. For example a Boolean for scheduling a lamp on or off.
 *
 */
public class EventScheduler<T> extends AbstractScheduler<T> {
    private LinkedList<SchedulerEvent<T>> pendingEvents = new LinkedList<>();
    private Set<SchedulerEvent<T>> todaysConsumedEvents = new HashSet<>();
    private LocalDate today;

    /**
     * Construct an EventScheduler with the default time source.
     *
     * @param holidayExplorer The holiday explorer
     */
    public EventScheduler(HolidayExplorer holidayExplorer) {
        this(holidayExplorer, new DefaultTimeSource());
    }

    /**
     * Construct an EventScheduler with a specific time source.
     *
     * @param holidayExplorer The holiday explorer
     * @param timeSource      The time source
     */
    public EventScheduler(HolidayExplorer holidayExplorer, TimeSource timeSource) {
        super(timeSource, holidayExplorer);
        LocalDateTime dateTime = getLocalDateTime();
        today = dateTime.toLocalDate();
        for (SchedulerEvent<T> e : events) {
            if (e.getTime().isBefore(dateTime.toLocalTime()) && dayModifierApplies(today, e)) {
                pendingEvents.addLast(e);
            }
        }
    }

    /**
     * Returns a pending event that current time has passed.
     *
     * @return A new event or null if no new event is pending
     */
    public SchedulerEvent<T> consumeEvent() {
        return consumeEvent(getLocalDateTime());
    }

    protected LocalDateTime getLocalDateTime() {
        return DateUtils.getLocalDateTime(timeSource.currentTimeMillis());
    }

    /**
     * Returns a pending event that current time has passed.
     *
     * @param dateTime Specified time
     * @return A new event or null if no new event is pending
     */
    public SchedulerEvent<T> consumeEvent(LocalDateTime dateTime) {
        if (!dateTime.toLocalDate().equals(today)) {
            todaysConsumedEvents.clear();
            today = dateTime.toLocalDate();
        }
        for (SchedulerEvent<T> e : events) {
            if (!todaysConsumedEvents.contains(e) &&
                    !e.getTime().isAfter(dateTime.toLocalTime()) &&
                    dayModifierApplies(today, e)) {
                pendingEvents.addLast(e);
            }
        }
        if (pendingEvents.isEmpty()) {
            return null;
        }

        SchedulerEvent<T> event = pendingEvents.removeFirst();
        todaysConsumedEvents.add(event);
        return event;
    }
}

