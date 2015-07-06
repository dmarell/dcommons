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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * <p>
 * This ObjectOutputStream makes it possible to provide progress indication when writing objects.
 * This is done by counting written objects of a specific reference class and assuming that
 * the caller can find a representative class and provide the total expected number of objects of
 * that class to write.
 * </p>
 * Progress is reported through the ProgressTracker interface.
 */
public class TrackedObjectOutputStream extends ObjectOutputStream {
  private ProgressTracker tracker;
  private int count;
  private Class<?> refClass;
  private int numRefClassObjects;

  /**
   * Construct a supervised ObjectOutputStream.
   *
   * @param out                OutputStream forwarded to super class ctor
   * @param tracker            Object that received progress information
   * @param refClass           The class to be counted
   * @param numRefClassObjects Expected number of instances of refClass to write
   * @throws IOException if an I/O error occurs while writing stream header
   */
  public TrackedObjectOutputStream(OutputStream out, ProgressTracker tracker, Class<?> refClass, int numRefClassObjects) throws IOException {
    super(out);
    this.tracker = tracker;
    this.refClass = refClass;
    this.numRefClassObjects = numRefClassObjects;
    enableReplaceObject(true);
  }

  @Override
  protected Object replaceObject(Object obj) throws IOException {
    if (obj.getClass().isAssignableFrom(refClass)) {
      tracker.setTotalProgress((float) ++count / (float) numRefClassObjects);
    }
    return super.replaceObject(obj);
  }
}