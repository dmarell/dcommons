/*
 * Created by Daniel Marell 12-07-04 8:30 AM
 */
package se.marell.dcommons.time;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class StateSchedulerTest {
  private int count;
  private SwedishHolidayExplorer he = new SwedishHolidayExplorer();

  @Test
  public void usageDemo() {
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(19, 0, 0), true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(23, 0, 0), false));
    turnOutdoorLamps(scheduler.getCurrentState());

    SchedulerEvent<Boolean> event = scheduler.getNewState();
    if (event != null) {
      turnOutdoorLamps(event.getState());
    }
  }

  private void turnOutdoorLamps(boolean on) {
    // No implementation, just for the show
  }

  @Test
  public void testSimpleEveryDay() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");

    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 10, 0), true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 20, 0), false));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(19, 10, 0), true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(19, 20, 0), false));
    assertThat(scheduler.getCurrentState(), is(false));

    SchedulerEvent<Boolean> event;

    ts.setDateAndTime("2012-06-07 06:11:00");
    event = scheduler.getNewState();
    assertThat(event.getState(), is(true));

    ts.setDateAndTime("2012-06-07 06:21:00");
    event = scheduler.getNewState();
    assertThat(event.getState(), is(false));

    ts.setDateAndTime("2012-06-08 06:22:00");
    event = scheduler.getNewState();
    assertNull(event);

    ts.setDateAndTime("2012-06-07 19:11:00");
    event = scheduler.getNewState();
    assertThat(event.getState(), is(true));

    ts.setDateAndTime("2012-06-07 19:21:00");
    event = scheduler.getNewState();
    assertThat(event.getState(), is(false));

    ts.setDateAndTime("2012-06-08 05:00:00");
    event = scheduler.getNewState();
    assertNull(event);

    for (SchedulerEvent e : scheduler.getEvents()) {
      assertNotNull(scheduler.toString());
    }
    scheduler.removeEvent(scheduler.getEvents().get(0));
    assertThat(scheduler.getEvents().size(), is(3));
    scheduler.clearEvents();
    assertThat(scheduler.getEvents().size(), is(0));
    scheduler.addEvents(new ArrayList<SchedulerEvent<Boolean>>() {{
      add(new SchedulerEvent<>(LocalTime.of(19, 20, 0), false));
    }});
    assertThat(scheduler.getEvents().size(), is(1));
  }

  @Test
  public void testSimpleWeeklyEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");

    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 10, 0), DayOfWeek.FRIDAY, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 20, 0), DayOfWeek.FRIDAY, false));
    assertThat(scheduler.getCurrentState(), is(false));

    SchedulerEvent<Boolean> event;

    ts.setDateAndTime("2012-06-07 06:11:00"); // Wednesday
    event = scheduler.getNewState();
    assertNull(event);

    ts.setDateAndTime("2012-06-07 06:21:00"); // Wednesday
    event = scheduler.getNewState();
    assertNull(event);

    ts.setDateAndTime("2012-06-08 06:11:00"); // Friday
    event = scheduler.getNewState();
    assertThat(event.getState(), is(true));

    ts.setDateAndTime("2012-06-08 06:21:00"); // Friday
    event = scheduler.getNewState();
    assertThat(event.getState(), is(false));
  }

  interface ScheduleChecker<T> {
    void gotEvent(LocalDateTime d, SchedulerEvent<T> e);
  }

  private <T> void verifySchedule(StateScheduler<T> s, ScheduleChecker<T> checker) {
      LocalDateTime c = LocalDateTime.parse("2012-01-01T00:00:00");
      do {
          SchedulerEvent<T> e = s.getNewState(c);
          if (e != null) {
              checker.gotEvent(c, e);
          }
          c = c.plusMinutes(19);
      } while (c.getYear() < 2014);
  }

  @Test
  public void testDailyEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside =
                !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                        e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
      }
    });
  }

  @Test
  public void testWorkingdayEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), true, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), true, false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside =
                d.getDayOfWeek() != DayOfWeek.SATURDAY &&
                        d.getDayOfWeek() != DayOfWeek.SUNDAY &&
                        he.getHoliday(d.toLocalDate()) == null &&
                        !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                        e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
      }
    });
  }

  @Test
  public void testNonWorkingDayEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), false, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), false, false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside =
                (d.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        d.getDayOfWeek() == DayOfWeek.SUNDAY ||
                        he.getHoliday(d.toLocalDate()) != null) &&
                        !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                        e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
      }
    });
  }

  @Test
  public void testWeeklyEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), DayOfWeek.TUESDAY, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), DayOfWeek.TUESDAY, false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside =
                d.getDayOfWeek() == DayOfWeek.TUESDAY &&
                        !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                        e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
        ++count;
      }
    });
    assertTrue(count > 0);
  }

  @Test
  public void testMonthlyEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), 28, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), 28, false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside =
                d.getDayOfMonth() == 28 &&
                        !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                        e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
        ++count;
      }
    });
    assertTrue(count > 0);
  }

  @Test
  public void testYearlyEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), 12, 28, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), 12, 28, false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside =
                d.getMonthValue() == 12 &&
                        d.getDayOfMonth() == 28 &&
                        !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                        e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
        ++count;
      }
    });
    assertTrue(count > 0);
  }

  @Test
  public void testSingleEvent() {
    FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
    StateScheduler<Boolean> scheduler = new StateScheduler<>(he, false, ts);
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(6, 0, 0), 2012, 12, 28, true));
    scheduler.addEvent(new SchedulerEvent<>(LocalTime.of(14, 0, 0), 2012, 12, 28, false));
    assertThat(scheduler.getCurrentState(), is(false));
    verifySchedule(scheduler, new ScheduleChecker<Boolean>() {
      boolean prevState;

      @Override
      public void gotEvent(LocalDateTime d, SchedulerEvent<Boolean> e) {
        boolean inside = d.getYear() == 2012 &&
                d.getMonthValue() == 12 &&
                d.getDayOfMonth() == 28 &&
                !e.getTime().isBefore(LocalTime.of(6, 0, 0)) &&
                e.getTime().isBefore(LocalTime.of(14, 0, 0));
        assertThat(d.toString(), e.getState(), is(inside));
        assertThat(e.getState(), not(prevState));
        prevState = e.getState();
        ++count;
      }
    });
    assertTrue(count > 0);
  }
}

