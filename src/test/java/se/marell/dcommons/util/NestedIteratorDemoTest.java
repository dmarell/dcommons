/*
 * Created by Daniel Marell 12-07-03 6:00 PM
 */
package se.marell.dcommons.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class BaseStationIterator extends NestedIterator<Site, BaseStation> {
  BaseStationIterator(Iterator<Site> firstIter) {
    super(firstIter);
  }

  @Override
  protected Iterator<BaseStation> getSecondIterator(Site site) {
    return site.getBaseStations().iterator();
  }
}

class CellIterator extends NestedIterator<BaseStation, Cell> {
  CellIterator(Iterator<BaseStation> firstIter) {
    super(firstIter);
  }

  @Override
  protected Iterator<Cell> getSecondIterator(BaseStation bs) {
    return bs.getCells().iterator();
  }
}

public class NestedIteratorDemoTest {
  @Test
  public void test() {
    int count = 0;
    List<Site> sites = createSites(2);
    for (Site s : sites) {
      for (BaseStation bs : s.getBaseStations()) {
        for (Cell c : bs.getCells()) {
          //System.out.println("Cell " + c.getName());
          ++count;
        }
      }
    }
    assertThat(count, is(12));
  }

  @Test
  public void testOneLevel() {
    int count = 0;
    List<Site> sites = createSites(2);
    Iterator<BaseStation> bsIter = new BaseStationIterator(sites.iterator());
    while (bsIter.hasNext()) {
      BaseStation bs = bsIter.next();
      //System.out.println("BaseStation " + bs);
      ++count;
    }
    //System.out.println();
    assertThat(count, is(4));
  }

  @Test
  public void testTwoLevels() {
    int count = 0;
    List<Site> sites = createSites(2);
    Iterator<Cell> cellIter = new CellIterator(new BaseStationIterator(sites.iterator()));
    while (cellIter.hasNext()) {
      Cell c = cellIter.next();
      //System.out.println("Cell " + c);
      ++count;
    }
    //System.out.println();
    assertThat(count, is(12));
  }

  @Test
  public void testPassToIterable() {
    int count = 0;
    List<Site> sites = createSites(2);
    final Iterator<Cell> cellIter = new CellIterator(new BaseStationIterator(sites.iterator()));
    consume(new Iterable<Cell>() {
      @Override
      public Iterator<Cell> iterator() {
        return cellIter;
      }
    });
  }

  private void consume(Iterable<Cell> cells) {
    int count = 0;
    for (Cell c : cells) {
      //System.out.println("Cell " + c);
      ++count;
    }
    assertThat(count, is(12));
  }

  private List<Site> createSites(int n) {
    List<Site> result = new ArrayList<Site>();
    for (int i = 0; i < n; ++i) {
      result.add(new Site("Site " + i, createBaseStations(2)));
    }
    return result;
  }

  private List<BaseStation> createBaseStations(int n) {
    List<BaseStation> result = new ArrayList<BaseStation>();
    for (int i = 0; i < n; ++i) {
      result.add(new BaseStation("BaseStation " + i, createCells(3)));
    }
    return result;
  }

  private List<Cell> createCells(int n) {
    List<Cell> result = new ArrayList<Cell>();
    for (int i = 0; i < n; ++i) {
      result.add(new Cell("Cell " + i));
    }
    return result;
  }
}

class Cell {
  private String name;

  Cell(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Site{" +
            "name='" + name + '\'' +
            '}';
  }
}

class BaseStation {
  private String name;
  private List<Cell> cells;

  BaseStation(String name, List<Cell> cells) {
    this.name = name;
    this.cells = cells;
  }

  public String getName() {
    return name;
  }

  public List<Cell> getCells() {
    return cells;
  }

  @Override
  public String toString() {
    return "Site{" +
            "name='" + name + '\'' +
            ", cells=" + cells +
            '}';
  }
}

class Site {
  private String name;
  private List<BaseStation> baseStations;

  Site(String name, List<BaseStation> baseStations) {
    this.name = name;
    this.baseStations = baseStations;
  }

  public String getName() {
    return name;
  }

  public List<BaseStation> getBaseStations() {
    return baseStations;
  }

  @Override
  public String toString() {
    return "Site{" +
            "name='" + name + '\'' +
            ", baseStations=" + baseStations +
            '}';
  }
}
