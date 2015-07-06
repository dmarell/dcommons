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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;

/**
 * <p>
 * Monitors the progress of reading from some Reader.
 * Reports progress to a ProgressTracker.
 * </p>
 * Usage example:
 * <pre>
 * ProgressTracker tracker = new MyProgressTracker();
 * BufferedReader reader = new TrackedReader(tracker,
 *                                           file.length(),
 *                                           new BufferedReader(new FileReader(file)));
 * </pre>
 */
public class TrackedReader extends BufferedReader {
  private ProgressTracker tracker;
  private int nread;
  private long size;

  public TrackedReader(ProgressTracker tracker, long size, Reader in) {
    super(in);
    this.tracker = tracker;
    this.size = size;
  }

  /**
   * Check if the reading task has been cancelled.
   *
   * @throws InterruptedIOException thrown if the task has been cancelled.
   */
  protected void checkCancellation() throws InterruptedIOException {
    if (tracker.isCancelled()) {
      InterruptedIOException exc = new InterruptedIOException("progress");
      exc.bytesTransferred = nread;
      throw exc;
    }
  }

  @Override
  public int read() throws IOException {
    int c = super.read();
    if (c >= 0) {
      tracker.setTotalProgress(++nread / (float) size);
    }
    checkCancellation();
    return c;
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int n = super.read(cbuf, off, len);
    if (n >= 0) {
      tracker.setTotalProgress((nread += n) / (float) size);
    }
    checkCancellation();
    return n;
  }

  @Override
  public int read(char[] cbuf) throws IOException {
    int n = super.read(cbuf);
    if (n >= 0) {
      tracker.setTotalProgress((nread += n) / (float) size);
    }
    checkCancellation();
    return n;
  }

  @Override
  public String readLine() throws IOException {
    String s = super.readLine();
    if (s != null) {
      tracker.setTotalProgress((nread += s.length()) / (float) size);
    }
    checkCancellation();
    return s;
  }

  @Override
  public long skip(long n) throws IOException {
    long nr = super.skip(n);
    if (nr > 0) {
      tracker.setTotalProgress((nread += nr) / (float) size);
    }
    return nr;
  }
}
