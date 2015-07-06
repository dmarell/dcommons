/*
 * Created by Daniel Marell 12-07-07 9:20 PM
 */
package se.marell.dcommons.time;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PassiveTimerTest {
  @Test
  public void testRun() throws Exception {
    FixedTimeSource ts = new FixedTimeSource(0);
    PassiveTimer t = new PassiveTimer(1000, ts);
    assertThat(t.getDuration(), is(1000L));
    assertThat(t.getRemainingTime(), is(1000L));
    assertThat(t.hasExpired(), is(false));
    assertThat(t.isRunning(), is(true));
    ts.advanceTime(500);
    assertThat(t.getRemainingTime(), is(500L));
    assertThat(t.hasExpired(), is(false));
    assertThat(t.isRunning(), is(true));
    ts.advanceTime(1000L);
    assertThat(t.getRemainingTime(), is(0L));
    assertThat(t.hasExpired(), is(true));
    assertThat(t.isRunning(), is(false));
  }

  @Test
  public void testHold() throws Exception {
    FixedTimeSource ts = new FixedTimeSource(0);
    PassiveTimer t = new PassiveTimer(1000, ts);
    assertThat(t.getRemainingTime(), is(1000L));
    assertThat(t.hasExpired(), is(false));
    assertThat(t.isRunning(), is(true));
    ts.advanceTime(500);
    t.hold(true);
    ts.advanceTime(5000);
    assertThat(t.getRemainingTime(), is(500L));
    assertThat(t.hasExpired(), is(false));
    assertThat(t.isRunning(), is(false));
    t.hold(false);
    ts.advanceTime(400);
    assertThat(t.getRemainingTime(), is(100L));
    assertThat(t.hasExpired(), is(false));
    assertThat(t.isRunning(), is(true));
    ts.advanceTime(200);
    assertThat(t.getRemainingTime(), is(0L));
    assertThat(t.hasExpired(), is(true));
    assertThat(t.isRunning(), is(false));
  }
}
