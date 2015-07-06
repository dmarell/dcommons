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
 * A MutableElementQueue is queue with pre-allocated elements. Elements must be mutable in
 * order for this queue to be useful because the objects in it cannot be replaced after initialization.
 * This technique supports queue operation with no heap allocation of objects.
 * This implementation is not thread safe.
 */
public class MutableElementQueue<E> implements Iterable<E> {
  /**
   * Used to create element nodes first time at queue initialization.
   *
   * @param <E> Queue element type
   */
  public interface Initializer<E> {
    E create();
  }

  private int firstElement = 0;
  private int lastElement = 0;
  private int nofElements = 0;
  private E[] queue;

  /**
   *
   *
   * @param initArray Array to be used as queue
   */
  /**
   * Create a new MutableElementQueue with the specified maximum number of elements.
   *
   * @param initArray   Empty array. It's length will be the queue size
   * @param initializer Will be used to create objects in initArray
   */
  public MutableElementQueue(E[] initArray, Initializer<E> initializer) {
    this.queue = initArray;
    for (int i = 0; i < initArray.length; ++i) {
      initArray[i] = initializer.create();
    }
  }

  /**
   * @return true if queue is empty
   */
  public boolean isEmpty() {
    return nofElements == 0;
  }

  /**
   * @return true if queue is full
   */
  public boolean isFull() {
    return nofElements >= queue.length;
  }

  /**
   * @return number of elements in queue
   */
  public int size() {
    return nofElements;
  }

  /**
   * @return number of free elements in queue
   */
  public int getFree() {
    return queue.length - nofElements;
  }

  /**
   * @return Max number of elements
   */
  public int getMaxSize() {
    return queue.length;
  }

  /**
   * Look at the object in the first position in this queue without removing it from the queue.
   *
   * @return Object at first position in queue
   */
  public Object peek() {
    assert !isEmpty();
    return queue[firstElement];
  }

  /**
   * <p>
   * Remove the object in the first position in this queue and return that object as the value of the function.
   * </p>
   * <b>Precondition:</b> QueueBuffer not empty
   * <b>Postcondition:</b>	First element in queue is removed from queue
   * QueueBuffer is not full
   * Number of elements one less in queue
   *
   * @return Object at first position in queue or null if queue is empty
   */
  public E getFirst() {
    if (isEmpty()) {
      return null;
    }
    E obj = queue[firstElement];
    firstElement = incrementIndex(firstElement);
    nofElements--;
    return obj;
  }

  /**
   * Put an item at the end of this queue. The added object is returned to the caller so the called can
   * initialize the mutable object with data.
   *
   * @return Added object to be initialized with data or null is queue is full
   */
  public E putLast() {
    if (isFull()) {
      return null;
    }
    E obj = queue[lastElement];
    lastElement = incrementIndex(lastElement);
    nofElements++;
    return obj;
  }

  /**
   * Step the index one step forward in queue. Wrap it to 0 if end of array is reached.
   *
   * @param index int
   * @return int index' new position
   */
  private int incrementIndex(int index) {
    if (index >= queue.length - 1) {
      return 0;
    }
    return index + 1;
  }

  /**
   * @return Iterator for iterating the queue content in order, from first element to last element.
   */
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      int index = firstElement;

      public boolean hasNext() {
        return index < lastElement;
      }

      public E next() {
        if (index >= lastElement) {
          throw new NoSuchElementException();
        }
        return queue[index++];
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
