/*
 * Created by Daniel Marell 14-12-28 13:37
 */
package se.marell.dcommons.time;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventSchedulerTest {
    private SwedishHolidayExplorer he = new SwedishHolidayExplorer();
    private long timeMillis;
    private TimeSource timeSource = new TimeSource() {
        @Override
        public long currentTimeMillis() {
            return timeMillis;
        }

        @Override
        public long nanoTime() {
            return timeMillis * 1000000;
        }
    };

    public void setTime(String dateTime) {
        setTime(LocalDateTime.parse(dateTime));
    }

    public void setTime(LocalDateTime dateTime) {
        this.timeMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public void usageDemo() throws Exception {
        EventScheduler<Boolean> scheduler = new EventScheduler<>(he);
        scheduler.addEvent(new SchedulerEvent<>(LocalTime.parse("19:00:00"), true));
        scheduler.addEvent(new SchedulerEvent<>(LocalTime.parse("23:00:00"), false));

        while (true) {
            SchedulerEvent<Boolean> e = scheduler.consumeEvent();
            if (e != null) {
                turnLampSwitch(e.getState());
            }
            Thread.sleep(1000);
        }
    }

    private void turnLampSwitch(boolean on) {
    }

    @Test
    public void shouldTurnOnAndOff() {
        setTime("2014-12-28T16:00:00");
        EventScheduler<Boolean> scheduler = new EventScheduler<>(he, timeSource);
        scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(19, 0, 0), true));
        scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(23, 0, 0), false));
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-28T17:00:00");
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-28T19:00:00");
        assertThat(scheduler.consumeEvent().getState(), is(true));
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-28T20:00:00");
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-28T23:00:00");
        assertThat(scheduler.consumeEvent().getState(), is(false));
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-28T23:00:01");
        assertThat(scheduler.consumeEvent(), nullValue());

        // Next day

        setTime("2014-12-29T00:00:01");
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-29T19:00:00");
        assertThat(scheduler.consumeEvent().getState(), is(true));
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-29T20:00:00");
        assertThat(scheduler.consumeEvent(), nullValue());

        setTime("2014-12-29T23:00:00");
        assertThat(scheduler.consumeEvent().getState(), is(false));
        assertThat(scheduler.consumeEvent(), nullValue());
    }
}
