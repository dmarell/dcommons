/*
 * Copyright (c) 1998,2011 Daniel Marell
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

/**
 * <p>
 * A timer starts measuring time from a certain value and counts down towards zero.
 * It does not call back nor execute in a separate thread. You have to explicit ask
 * for if the PassiveTimer has expired.
 * </p>
 * Usage example:
 * <pre>
 * final int TIME_PERIOD_MSEC = 1000;
 * final int POLL_TIME_MSEC = 200;
 *
 * PassiveTimer t = new PassiveTimer(TIME_PERIOD_MSEC);
 * boolean ledState = false;
 *
 * while (true) {
 *   System.out.println("getRemainingTime()=" + t.getRemainingTime());
 *
 *   if (t.hasExpired()) {
 *     ledState = !ledState;
 *     System.out.println("expired,led=" + ledState);
 *     t.restart();
 *   }
 *   Thread.sleep(POLL_TIME_MSEC);
 * }
 * </pre>
 */
public class PassiveTimer {
  private long duration;
  private boolean hold;
  private long startTime;
  private long holdTime;
  private TimeSource source;

  /**
   * Construct a PassiveTimer counting down from the specified duration.
   *
   * @param msec Number of milliseconds util timer expires
   */
  public PassiveTimer(long msec) {
    duration = msec;
    hold = false;
    holdTime = 0;
    startTime = System.currentTimeMillis();
    source = new DefaultTimeSource();
  }

  /**
   * Construct a PassiveTimer counting down from the specified duration.
   *
   * @param msec   Number of milliseconds util timer expires
   * @param source Alternate time source
   */
  public PassiveTimer(long msec, TimeSource source) {
    duration = msec;
    hold = false;
    holdTime = 0;
    startTime = source.currentTimeMillis();
    this.source = source;
  }

  /**
   * Restart timer with current duration.
   */
  public void restart() {
    startTime = source.currentTimeMillis();
    hold = false;
  }

  /**
   * Restart timer with new duration.
   *
   * @param msec Number of milliseconds util timer expires
   */
  public void restart(long msec) {
    duration = msec;
    restart();
  }

  /**
   * Set timer duration.
   *
   * @param msec Number of milliseconds util timer expires
   */
  public void setDuration(long msec) {
    duration = msec;
  }

  /**
   * Has timer expired?
   *
   * @return true if timer has expired
   */
  public boolean hasExpired() {
    return !hold && source.currentTimeMillis() >= (startTime + duration);
  }

  /**
   * Remaining time.
   *
   * @return Number of milliseconds util timer expires
   */
  public long getRemainingTime() {
    if (hasExpired()) {
      return 0;
    }
    if (hold) {
      return startTime + duration - holdTime;
    }
    return startTime + duration - source.currentTimeMillis();
  }

  /**
   * Force timer to expire.
   */
  public void forceExpire() {
    startTime = source.currentTimeMillis() - duration;
    hold = false;
  }

  /**
   * Freeze/resume timer.
   *
   * @param flag Freezes timer at current duration if true, releases if false
   */
  public void hold(boolean flag) {
    if (flag == hold) {
      return;
    }
    if (flag) {
      holdTime = source.currentTimeMillis();
    } else {
      startTime += source.currentTimeMillis() - holdTime;
    }
    hold = flag;
  }

  /**
   * PassiveTimer running?
   *
   * @return true if timer is running
   */
  public boolean isRunning() {
    return !hold && !hasExpired();
  }

  /**
   * PassiveTimer duration.
   *
   * @return Number of milliseconds for the initial duration. If timer is restarted this is the new duration.
   */
  public long getDuration() {
    return duration;
  }
}
