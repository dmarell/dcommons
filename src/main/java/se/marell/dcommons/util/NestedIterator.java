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
package se.marell.dcommons.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * NestedIterator supports iterating child objects using dual iterators.
 * F=top/parent object
 * S=child object
 */
public abstract class NestedIterator<F, S> implements Iterator<S> {
  private Iterator<F> firstIter;
  private Iterator<S> secondIter;

  protected NestedIterator(Iterator<F> firstIter) {
    this.firstIter = firstIter;
  }

  /**
   * @param obj Object on 1st level
   * @return Iterator for iterating 2nd level given object on 1st level
   */
  protected abstract Iterator<S> getSecondIterator(F obj);

  public boolean hasNext() {
    while (true) {
      if (secondIter != null) {
        if (secondIter.hasNext()) {
          return true;
        }
      }
      if (firstIter.hasNext()) {
        secondIter = getSecondIterator(firstIter.next());
      } else {
        secondIter = null;
        return false;
      }
    }
  }

  public S next() {
    while (true) {
      if (secondIter != null) {
        if (secondIter.hasNext()) {
          return secondIter.next();
        }
      }
      if (!firstIter.hasNext()) {
        throw new NoSuchElementException();
      }
      secondIter = getSecondIterator(firstIter.next());
      if (secondIter.hasNext()) {
        return secondIter.next();
      }
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
