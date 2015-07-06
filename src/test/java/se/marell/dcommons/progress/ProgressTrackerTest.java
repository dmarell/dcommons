/*
 * Created by Daniel Marell 12-07-03 10:44 PM
 */
package se.marell.dcommons.progress;

import org.junit.Test;

public class ProgressTrackerTest {

  private static class LengthyTask {
    public void run(final ProgressTracker pt, final int length) {
      for (int i = 0; i < length; ++i) {
        // Lengthy work here

        // Update progress and check if cancelled
        pt.setProgressLabel("Executing step " + i + " of " + length);
        pt.setTotalProgress(i / (float) length);
        if (pt.isCancelled()) {
          return;
        }
      }
    }
  }

  @Test
  public void test() {
    final LengthyTask task = new LengthyTask();
    task.run(new EmptyProgressTracker(), 2);
    task.run(new PrintlnProgressTracker(), 2);
    task.run(new ProgressTrackerAdapter(), 2);
  }
}

