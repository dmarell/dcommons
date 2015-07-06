/*
 * Created by Daniel Marell 2011-10-30 12:02
 */
package se.marell.dcommons.progress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TrackedReaderDemo {
  public static void main(String[] args) {
    ProgressTracker tracker = new PrintlnProgressTracker();
    BufferedReader reader = null;
    try {
      File file = new File("largefile");
      reader = new TrackedReader(tracker, (int) file.length(), new FileReader(file));
      int lineNo = 0;
      while (reader.readLine() != null) {
        ++lineNo;
        tracker.setProgressLabel("Reading line " + lineNo);
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignore) {
        }
      }
    }
  }
}
