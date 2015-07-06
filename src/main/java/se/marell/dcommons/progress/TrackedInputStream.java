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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * Monitors the progress of reading from some InputStream.
 * Reports progress to a ProgressTracker
 * <p>
 * Usage example:
 * </p>
 * <pre>
 * ProgressTracker tracker = new MyProgressTracker();
 * InputStream in = new BufferedInputStream(
 *                          new TrackedInputStream(
 *                                  tracker,
 *                                  "Reading " + fileName,
 *                                  new FileInputStream(fileName)));
 * </pre>
 */
public class TrackedInputStream extends FilterInputStream {
  private ProgressTracker tracker;
  private int nread = 0; // File pointer position (usually the same thing as #bytes we have read).
  private int size = 0; // File size
  private int lastNRead; // Last #bytes read reported to the progress bar
  private int onePercent; // #bytes corresponding to one percent of the file size

  /**
   * Constructs an object to monitor the progress of an input stream.
   * @param tracker The progress tracker object
   * @param in         The input stream to be monitored.
   */
  public TrackedInputStream(ProgressTracker tracker, InputStream in) {
    super(in);
    this.tracker = tracker;
    try {
      size = in.available();
      onePercent = size / 100;
    } catch (IOException ignore) {
      size = 0;
    }
  }

  /**
   * Return number of bytes read so far
   * @return Number of bytes read so far
   */
  public int getBytesRead() {
    return nread;
  }

  /**
   * Return number of bytes to read
   * @return Number of bytes to read
   */
  public int getBytesToRead() {
    return size;
  }

  /**
   * Update the progress monitor after the read.
   */
  @Override
  public int read() throws IOException {
    int c = in.read();
    if (c >= 0) {
      nread++;
      if (nread - lastNRead > onePercent) {
        tracker.setTotalProgress((nread) / (float) size);
        lastNRead = nread;
      }
    }
    if (tracker.isCancelled()) {
      InterruptedIOException exc = new InterruptedIOException("progress");
      exc.bytesTransferred = nread;
      throw exc;
    }
    return c;
  }


  /**
   * Update the progress monitor after the read.
   */
  @Override
  public int read(byte[] b) throws IOException {
    int nr = in.read(b);
    if (nr > 0) {
      nread += nr;
      if (nread - lastNRead > onePercent) {
        tracker.setTotalProgress((nread) / (float) size);
        lastNRead = nread;
      }
    }
    if (tracker.isCancelled()) {
      InterruptedIOException exc = new InterruptedIOException("progress");
      exc.bytesTransferred = nread;
      throw exc;
    }
    return nr;
  }


  /**
   * Update the progress monitor after the read.
   */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int nr = in.read(b, off, len);
    if (nr > 0) {
      nread += nr;
      if (nread - lastNRead > onePercent) {
        tracker.setTotalProgress((nread) / (float) size);
        lastNRead = nread;
      }
    }
    if (tracker.isCancelled()) {
      InterruptedIOException exc = new InterruptedIOException("progress");
      exc.bytesTransferred = nread;
      throw exc;
    }
    return nr;
  }


  /**
   * Update the progress monitor after the skip.
   */
  @Override
  public long skip(long n) throws IOException {
    long nr = in.skip(n);
    if (nr > 0) {
      nread += nr;
      if (nread - lastNRead > onePercent) {
        tracker.setTotalProgress((nread) / (float) size);
        lastNRead = nread;
      }
    }
    return nr;
  }


  /**
   * Close the progress monitor as well as the stream.
   */
  @Override
  public void close() throws IOException {
    in.close();
  }


  /**
   * Reset the progress monitor as well as the stream.
   */
  @Override
  public synchronized void reset() throws IOException {
    in.reset();
    nread = size - in.available();
    if (Math.abs(nread - lastNRead) > onePercent) {
      tracker.setTotalProgress((nread) / (float) size);
      lastNRead = nread;
    }
  }
}
