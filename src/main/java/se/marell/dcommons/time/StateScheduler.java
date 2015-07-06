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

import java.time.LocalDateTime;

/**
 * This StateScheduler is holding events representing objects at specified time of day and specified days of week.
 * Init the StateScheduler with a week schedule and check it using the method getNewState at regular times
 * in order to check if any new event is due.
 */
public class StateScheduler<T> extends AbstractScheduler<T> {
    private SchedulerEvent<T> lastEvent;
    private T defaultState;

    /**
     * Construct an StateScheduler with the default time source.
     *
     * @param holidayExplorer The holiday explorer
     * @param defaultState    The default state
     */
    public StateScheduler(HolidayExplorer holidayExplorer, T defaultState) {
        this(holidayExplorer, defaultState, new DefaultTimeSource());
    }

    /**
     * Construct an StateScheduler with a specific time source.
     *
     * @param holidayExplorer The holiday explorer
     * @param defaultState    The default state
     * @param timeSource      The time source
     */
    public StateScheduler(HolidayExplorer holidayExplorer, T defaultState, TimeSource timeSource) {
        super(timeSource, holidayExplorer);
        this.defaultState = defaultState;
    }

    /**
     * Return the first event that current time has passed if it is not the same as the last event
     * returned from this method.
     *
     * @return A new event or null if no new event is pending
     */
    public SchedulerEvent<T> getNewState() {
        return getNewState(DateUtils.getLocalDateTime(timeSource.currentTimeMillis()));
    }

    /**
     * Return the first event that has reached the specified time cal if it is not the same as the last event
     * returned from this method.
     *
     * @param dateTime Specified time
     * @return A new event or null if no new event is pending
     */
    public SchedulerEvent<T> getNewState(LocalDateTime dateTime) {
        SchedulerEvent<T> selectedEvent = null;
        for (SchedulerEvent<T> e : events) {
            if (e.getTime().isBefore(dateTime.toLocalTime()) && dayModifierApplies(dateTime.toLocalDate(), e)) {
                if (e == lastEvent) {
                    selectedEvent = null;
                } else {
                    selectedEvent = e;
                }
            }
            if (e.getTime().isAfter(dateTime.toLocalTime())) {
                break;
            }
        }
        if (selectedEvent != null) {
            lastEvent = selectedEvent;
        }
        return selectedEvent;
    }

    /**
     * Get the current state.
     *
     * @return Current state of this StateScheduler
     */
    public T getCurrentState() {
        return getCurrentState(DateUtils.getLocalDateTime(timeSource.currentTimeMillis()));
    }

    /**
     * Get the current state.
     *
     * @param dateTime Specified time
     * @return Current state of this StateScheduler
     */
    public T getCurrentState(LocalDateTime dateTime) {
        for (SchedulerEvent<T> e : events) {
            if (e.getTime().isAfter(dateTime.toLocalTime()) && dayModifierApplies(dateTime.toLocalDate(), e)) {
                lastEvent = e;
            }
        }
        if (lastEvent != null) {
            return lastEvent.getState();
        }
        return defaultState;
    }
}
