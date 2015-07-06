/*
 * Created by Daniel Marell 12-07-02 10:29 PM
 */
package se.marell.dcommons.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AbstractAdapterIteratorTest {
  @Test
  public void test() throws Exception {
    final List<Double> doubleList = new ArrayList<Double>() {{
      add(0.9);
      add(2.4);
      add(2.5);
    }};

    int n = 0;
    Iterator<Integer> iter = new D2iAdapterIterator(doubleList.iterator());
    while (iter.hasNext()) {
      Integer value = iter.next();
      assertThat(value, is(++n));
      System.out.println("adapted value=" + value);
    }

    consume(new Iterable<Integer>() {
      @Override
      public Iterator<Integer> iterator() {
        return new D2iAdapterIterator(doubleList.iterator());
      }
    });
  }

  private void consume(Iterable<Integer> intIterable) {
    System.out.println("consume:");
    for (Integer i : intIterable) {
      System.out.println(" i=" + i);
    }
    System.out.println();
  }
}

/**
 * Convert Iterator<Double> to Iterator<Integer>.
 */
class D2iAdapterIterator extends AbstractAdapterIterator<Integer, Double> {
  public D2iAdapterIterator(Iterator<Double> adaptedIter) {
    super(adaptedIter);
  }

  @Override
  public Integer next() {
    Double dval = adaptedIter.next();
    return (int) (dval + 0.5);
  }
}
