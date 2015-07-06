/*
 * Created by Daniel Marell 2011-10-30 12:08
 */
package se.marell.dcommons.progress;

import java.io.*;

public class TrackedInputStreamDemo {
  public static void main(String[] args) {
    ProgressTracker tracker = new PrintlnProgressTracker();
    File file = new File("largefile");
    InputStream in = null;
    try {
      in = new BufferedInputStream(new TrackedInputStream(tracker, new FileInputStream(file)));
      while (in.available() > 0) {
        in.read();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException ignore) {
        }
      }
    }
  }
}
