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

import org.junit.Test;
import se.marell.dcommons.util.ChainedIterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class ChainedIterablesTest {
  @Test
  public void testEmptyList() throws Exception {
    ChainedIterables<Integer> chain = new ChainedIterables<Integer>(new ArrayList<Iterable<? extends Integer>>());
    for (Integer i : chain) {
      fail();
    }
  }

  @Test
  public void testOneList() throws Exception {
    List<Iterable<? extends Integer>> iterables = new ArrayList<Iterable<? extends Integer>>();
    iterables.add(createIntegerList(1, 5));
    ChainedIterables<Integer> chain = new ChainedIterables<Integer>(iterables);
    int n = 0;
    for (Integer i : chain) {
      ++n;
    }
    assertEquals(5, n);
  }

  @Test
  public void testThreeLists() throws Exception {
    List<Iterable<? extends Integer>> iterables = new ArrayList<Iterable<? extends Integer>>();
    iterables.add(createIntegerList(0, 5));
    iterables.add(createIntegerList(5, 5));
    iterables.add(createIntegerList(10, 5));
    ChainedIterables<Integer> chain = new ChainedIterables<Integer>(iterables);
    int n = 0;
    for (Integer i : chain) {
      assertEquals(n++, i.intValue());
    }
    assertEquals(15, n);
  }

  @Test
  public void testThreeLists1stEmpty() throws Exception {
    List<Iterable<? extends Integer>> iterables = new ArrayList<Iterable<? extends Integer>>();
    iterables.add(createIntegerList(0, 0));
    iterables.add(createIntegerList(0, 5));
    iterables.add(createIntegerList(5, 5));
    ChainedIterables<Integer> chain = new ChainedIterables<Integer>(iterables);
    int n = 0;
    for (Integer i : chain) {
      assertEquals(n++, i.intValue());
    }
    assertEquals(10, n);
  }

  @Test
  public void testThreeLists2ndEmpty() throws Exception {
    List<Iterable<? extends Integer>> iterables = new ArrayList<Iterable<? extends Integer>>();
    iterables.add(createIntegerList(0, 5));
    iterables.add(createIntegerList(0, 0));
    iterables.add(createIntegerList(5, 5));
    ChainedIterables<Integer> chain = new ChainedIterables<Integer>(iterables);
    int n = 0;
    for (Integer i : chain) {
      assertEquals(n++, i.intValue());
    }
    assertEquals(10, n);
  }

  @Test
  public void testThreeLists3rdEmpty() throws Exception {
    List<Iterable<? extends Integer>> iterables = new ArrayList<Iterable<? extends Integer>>();
    iterables.add(createIntegerList(0, 5));
    iterables.add(createIntegerList(5, 5));
    iterables.add(createIntegerList(0, 0));
    ChainedIterables<Integer> chain = new ChainedIterables<Integer>(iterables);
    int n = 0;
    for (Integer i : chain) {
      assertEquals(n++, i.intValue());
    }
    assertEquals(10, n);
  }

  private static Iterable<Integer> createIntegerList(int from, int n) {
    Collection<Integer> ints = new ArrayList<Integer>();
    int num = from;
    for (int i = 0; i < n; ++i) {
      ints.add(num++);
    }
    return ints;
  }
}
