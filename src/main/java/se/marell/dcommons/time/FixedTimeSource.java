/*
 * Created by Daniel Marell 12-07-04 5:42 PM
 */
package se.marell.dcommons.time;

/**
 * Represents a fixed TimeSource, possible to initialize to a specific time and possible to advance forward.
 * The main purpose of this class is testing.
 */
public class FixedTimeSource implements TimeSource {
  private long millis;

  /**
   * Set time from millis.
   *
   * @param millis Date and time in milliseconds
   */
  public FixedTimeSource(long millis) {
    this.millis = millis;
  }

  /**
   * Set date and time from string on the format yyyy-MM-dd hh:mm:ss
   *
   * @param dateTime String with date and time
   */
  public FixedTimeSource(String dateTime) {
    setDateAndTime(dateTime);
  }

  public void advanceTime(long millis) {
    this.millis += millis;
  }

  public void setTimeMillis(long millis) {
    this.millis = millis;
  }

  /**
   * Set date and time from string on the format yyyy-MM-dd hh:mm:ss
   *
   * @param dateTime String with date and time
   */
  public void setDateAndTime(String dateTime) {
    this.millis = DateUtils.parseDateTimeIso8601(dateTime).getTimeInMillis();
  }

  @Override
  public long currentTimeMillis() {
    return millis;
  }

  @Override
  public long nanoTime() {
    return millis * 1000;
  }
}
