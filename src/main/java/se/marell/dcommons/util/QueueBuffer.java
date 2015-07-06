/*
 * Copyright (c) 2003,2011 Daniel Marell
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
 * <p>
 * Elements are inserted at the end of the queue (putLast operation) and retrieved from the
 * front of the queue (getFirst operation).
 * </p>
 * <p>
 * Null values are allowed to be putLast in the queue.
 * </p>
 * <p>
 * Use this class instead of LinkedList if you have a fixed maximum size of the buffer and if you are worried
 * about that the LinkedList implementation is allocating a list node-object in addition to the objects you are
 * storing in the queue.
 * </p>
 */
public class QueueBuffer<E> implements Iterable<E> {
  private int firstElement = 0;
  private int lastElement = 0;
  private int nofElements = 0;
  private int maxQueueSize;
  private Object[] queue;

  /**
   * Create a new QueueBuffer with the specified maximum number of elements.
   *
   * @param maxQueueSize Max number of items in queue. Must be larger than 0.
   */
  public QueueBuffer(int maxQueueSize) {
    assert maxQueueSize > 0;
    this.maxQueueSize = maxQueueSize;
    this.queue = new Object[maxQueueSize];
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
    return nofElements >= maxQueueSize;
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
    return maxQueueSize - nofElements;
  }

  /**
   * @return Max number of elements in QueueBuffer
   */
  public int getMaxSize() {
    return maxQueueSize;
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
   * Remove the object in the first position in this queue and return that object as the value of the function.
   * <b>Precondition:</b> QueueBuffer not empty
   * <b>Postcondition:</b>	First element in queue is removed from queue
   * QueueBuffer is not full
   * Number of elements one less in queue
   *
   * @return Object at first position in queue or null if queue is empty
   */
  public synchronized E getFirst() {
    if (isEmpty()) {
      return null;
    }
    Object obj = queue[firstElement];
    // Set the queue element to null to make sure that the queue doesn't have
    // any references to the object that disables garbage collection of it
    queue[firstElement] = null;
    firstElement = incrementIndex(firstElement);
    nofElements--;
    return (E) obj;
  }

  /**
   * <p>
   * Put an item at the end of this queue.
   * </p>
   * <b>Precondition:</b> QueueBuffer not full
   * <b>Postcondition:</b>	Element is putLast at end of queue
   * QueueBuffer is not empty
   * Number of elements one more in queue
   *
   * @param obj Object
   */
  public synchronized void putLast(E obj) {
    queue[lastElement] = obj;
    lastElement = incrementIndex(lastElement);
    nofElements++;
  }

  /**
   * <p>
   * Return where an object is in the queue.
   * The return value is the objects distance to the front of the queue.
   * The return value -1 indicates that the object is not in the queue.
   * If obj is null the first occurrence of null in queue is searched for.
   * </p>
   * <b>Precondition:</b> None
   * <b>Postcondition:</b>	None
   *
   * @param obj Object
   * @return int distance to object from front of queue
   */
  public synchronized int search(E obj) {
    if (isEmpty()) {
      return -1;
    }

    int currentIndex = firstElement;
    int distance = 0;
    do {
      if (obj == null) {
        if (queue[currentIndex] == null) {
          return distance;
        }
      } else if (obj.equals(queue[currentIndex])) {
        return distance;
      }
      distance++;
      currentIndex = incrementIndex(currentIndex);
    } while (currentIndex != lastElement);
    return -1;
  }

  /**
   * Step the index one step forward in queue. Wrap it to 0 if end of array is reached.
   * <p/>
   * <b>Precondition:</b> None
   * <b>Postcondition:</b>	Index incremented by 1 or set to 0 if wrapped
   *
   * @param index int
   * @return int index' new position
   */
  private int incrementIndex(int index) {
    if (index >= maxQueueSize - 1) {
      return 0;
    }
    return index + 1;
  }

  /**
   * @return Iterator for iterator the queue content
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
        return (E) queue[index++];
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
