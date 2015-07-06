/*
 * Copyright (c) 1999,2011 Daniel Marell
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
package se.marell.dcommons.util;

/**
 * Base class defining an interface of filters in the meaning of sluggish variables.
 * Filter implements common operations. Filter can be instantiated by itself and
 * performs no filtering.
 */
public class Filter {
  protected double in;     // Last input level
  protected double out;    // Last output level

  public Filter() {
  }

  public Filter(double level) {
    in = out = level;
  }

  // Input to filter
  public void in(double level) {
    in = out = level;
  }

  // Output from filter
  public double out() {
    return out;
  }

  // Input and output
  public double inout(double level) {
    in(level);
    return out();
  }

  // Get raw input value
  public double raw() {
    return in;
  }

  // Set filter level
  public void set(double level) {
    in = out = level;
  }
}
