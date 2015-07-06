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
import se.marell.dcommons.util.NestedIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static junit.framework.Assert.*;

public class NestedIteratorTest {
  @Test
  public void testEmptyList() throws Exception {
    // Test with empty list
    int i = 0;
    for (String s : getChildElements(new ArrayList<List<String>>())) {
      ++i;
    }
    assertEquals(i, 0);
  }

  @Test
  public void testNonEmptyList() throws Exception {
    List<List<String>> tc = new ArrayList<List<String>>();

    {
      // First child list has three elements
      List<String> c = new ArrayList<String>();
      c.add("c1:0");
      c.add("c1:1");
      c.add("c1:2");
      tc.add(c);
    }

    {
      // 2nd child list has no elements
      List<String> c = new ArrayList<String>();
      tc.add(c);
    }

    {
      // Next child list has one element
      List<String> c = new ArrayList<String>();
      c.add("c3:0");
      tc.add(c);
    }

    {
      // Last child list has two elements
      List<String> c = new ArrayList<String>();
      c.add("c4:0");
      c.add("c4:1");
      tc.add(c);
    }

    // Test with list containing both empty and non-empty elements
    {
      int i = 0;
      for (String s : getChildElements(tc)) {
        switch (i) {
          case 0:
            assertEquals(s, "c1:0");
            break;
          case 1:
            assertEquals(s, "c1:1");
            break;
          case 2:
            assertEquals(s, "c1:2");
            break;
          case 3:
            assertEquals(s, "c3:0");
            break;
          case 4:
            assertEquals(s, "c4:0");
            break;
          case 5:
            assertEquals(s, "c4:1");
            break;
          default:
            fail();
        }
        ++i;
      }
    }
  }

  @Test
  public void testAbnormalIteratorCalls() throws Exception {
    List<List<String>> tc = new ArrayList<List<String>>();

    {
      // First child list has three elements
      List<String> c = new ArrayList<String>();
      c.add("c1:0");
      c.add("c1:1");
      c.add("c1:2");
      tc.add(c);
    }

    {
      // 2nd child list has no elements
      List<String> c = new ArrayList<String>();
      tc.add(c);
    }

    {
      // Next child list has one element
      List<String> c = new ArrayList<String>();
      c.add("c3:0");
      tc.add(c);
    }

    Iterator<String> iter = getChildElements(tc).iterator();

    for (int i = 0; i < 3; ++i) {
      assertTrue(iter.hasNext());
    }

    assertEquals(iter.next(), "c1:0");
    assertEquals(iter.next(), "c1:1");

    for (int i = 0; i < 3; ++i) {
      assertTrue(iter.hasNext());
    }

    assertEquals(iter.next(), "c1:2");
    assertEquals(iter.next(), "c3:0");

    for (int i = 0; i < 3; ++i) {
      assertFalse(iter.hasNext());
    }

    boolean caught = false;
    try {
      iter.next();
    } catch (NoSuchElementException e) {
      caught = true;
    }
    assertTrue(caught);
  }

  private static Iterable<String> getChildElements(final List<List<String>> c) {
    return new Iterable<String>() {
      public Iterator<String> iterator() {
        return new NestedIterator<List<String>, String>(c.iterator()) {
          protected Iterator<String> getSecondIterator(List<String> obj) {
            return obj.iterator();
          }
        };
      }
    };
  }
}
