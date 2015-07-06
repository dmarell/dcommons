/*
 * Copyright (c) 2010,2011 Daniel Marell
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
package se.marell.dcommons.progress;

/**
 * This interface provides a simple way for a task/thread to communicate progress to a tracker.
 */
public interface ProgressTracker {
  /**
   * Called by the tracked task/thread in order to check whether
   * the tracker want to cancel/interrupt the working task/thread or not.
   *
   * @return true if task shall be cancelled
   */
  boolean isCancelled();

  /**
   * Called by the tracked task/thread in order to inform the tracker
   * about progress.
   *
   * @param value Number between 0.0 and 1.0
   */
  void setTotalProgress(float value);

  /**
   * Optional call to inform about current action. Intended to be used for setting a text in a progress dialog
   *
   * @param text Human readable text
   */
  void setProgressLabel(String text);

  /**
   * Optional call to inform about activity. Useful if the overall task length is not known but activities of some
   * kind can be reported, which can provide the user with information about that something is happening.
   * @param count A number of "activities"
   */
  void activityReport(int count);
}
