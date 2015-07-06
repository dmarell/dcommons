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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class supports iterating several iterables in a sequence. The purpose of using ChainedIterables
 * instead of adding the content of each iterable to for example an ArrayList and iterate this list is
 * that ChainedIterable does not touch elements in iterables before actually iterating them. This is also
 * nice when the iterators uses lazy evaluation.
 */
public class ChainedIterables<T> implements Iterable<T> {
  private Iterator<Iterator<? extends T>> iterablesIter;
  private Iterator<? extends T> currentIterator;

  public ChainedIterables(Iterable<Iterable<? extends T>> iterables) {
    List<Iterator<? extends T>> tmp = new ArrayList<Iterator<? extends T>>();
    for (Iterable<? extends T> i : iterables) {
      tmp.add(i.iterator());
    }
    iterablesIter = tmp.iterator();
  }

  public ChainedIterables(Iterable<Iterator<? extends T>> iterators, boolean dummy) {
    iterablesIter = iterators.iterator();
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      @Override
      public boolean hasNext() {
        if (currentIterator == null) {
          advanceCurrentIterator();
          if (currentIterator == null) {
            return false;
          }
        }
        while (currentIterator != null && !currentIterator.hasNext()) {
          advanceCurrentIterator();
        }
        return currentIterator != null;
      }

      @Override
      public T next() {
        if (currentIterator == null) {
          throw new NoSuchElementException();
        }
        return currentIterator.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  private void advanceCurrentIterator() {
    if (iterablesIter.hasNext()) {
      currentIterator = iterablesIter.next();
    } else {
      currentIterator = null;
    }
  }
}
