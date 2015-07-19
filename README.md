# dcommons
``dcommons`` is a library containing scattered and various generic nice-to-have classes which the author at the time
 did not find in any other generic java library and which has no specific dependencies and which does not have another home:

- package ``progress``: utilities for reporting progress from lengthy UI-interacting tasks
in a dedicated thread.

- package ``sound``: sound related utilities on top of the Java Sound API.

- package ``time``, time and date utilities.

- package ``util``, an extension to ``java.util``.

The library is packaged as an OSGi bundle.

## Maven usage

``` 
<dependency>
  <groupId>se.marell</groupId>
  <artifactId>dcommons</artifactId>
  <version>2.0.9</version>
</dependency>
...
```

## Release notes

* Version 2.0.9 - 2015-07-19
  * Changed repo url
* Version 2.0.8 - 2015-07-06
  * Moved code to github
  * Replaced site plugin with README.md
* Version 2.0.7 - 2014-12-28
  * Added EventScheduler
  * SwedishHolidayExplorer: Fixed bug related to "Alla helgons dag" 6/11 (2020, 2055)
  * More conversions related to time and date classes in Java 8
* Version 2.0.0 - 2014-12-16
  * Java 8
  * Added DateUtils.getLocalDateTime(long timeInMillis)
* Version 1.1.21 - 2014-02-08
  * Java 7
  * Changed pom versioning mechanism.
  * Extended site information.
  * Updated versions of dependencies
* Version 1.1 - 2012-07-08
  * Extended the documentation
  * EventWeekTimer --> StateScheduler + extensions montly, yearly and once
  * Added FixedTimeSource
  * Merged packed misc into util. Removed CacheManager
  * Extended ProgressTracker
  * ProgressSplitter --> ProgressCombiner
  * Extended DateUtils
  * Renamed Holiday --> HolidayExplorer and extended interface with enumerations and support for multiple holidays per date
  * Upgraded library versions
  * Improved unit test coverage
* Version 1.0 - 2011-10-30

## Package progress

The progress package supports tracking lengty tasks while executing. The progress support is centered
around the interface ``ProgressTracker``. It is used by lengthy tasks, probably executing in dedicated threads,
to report their progress somewhere.

An important goal is that it should be possible to design a length task without knowing where it should report
back - if it is a swing GUI, a web application, a console application or something else, the lengthy task should
not have to know.

Example of a length task using a ``ProgressTracker``:

``` 
public class LengthyTask {
  public void run(final ProgressTracker pt, final int length) {
    for (int i = 0; i < length; ++i) {
      // Lengthy work here

      // Update progress and check if cancelled
      pt.setProgressLabel("Executing step " + i + " of " + length);
      pt.setTotalProgress(i / (float) length);
      if (pt.isCancelled()) {
        return;
      }
    }
  }
}
``` 

LengthyTask could be called with a ``PrintlnProgressTracker`` which reports progress by printing to standard output.

``` 
LengthyTask task = new LengthyTask();
task.run(new PrintlnProgressTracker(), 100);
``` 

or an ``EmptyProgressTracker`` in order to redirect output to /dev/null:

``` 
task.run(new EmptyProgressTracker(), 100);
``` 

 a ``ProgressCombiner`` is used to combine several ``ProgressTracker`` to one, assigning them individual weights.
 This is useful if a task consists of sub tasks, where each sub task reports its progress using
 a ``ProgressTracker``.

 The ``ProgressTrackerAdapter`` implements all methods in ``ProgressTracker``. Override exactly what is needed
 instead of having to implement the complete interface.

 ``TrackedInputStream`` and ``TrackedReader`` reports reading while reporting progress using a ``ProgressTracker``.

 ``TrackedObjectOutputStream`` reports progress while writing an ``ObjectOutputStream``.

## Package sound
           
Sound related utilities on top of the Java Sound API.

``SoundClip`` plays a clip of a limited length (unlike a stream) on a specific ``SoundOutputDevice``.

Usage example:

```
SoundPlayerDevice device = SoundPlayerDevice.createSoundPlayerDevice("Intel.*");
SoundClip clip = new SoundClip(new File("aieeh.wav"));
clip.play(device);
```

## Package time

Time and date related classes.

### TimeSource

``TimeSource`` is an interface representing a source of time, e.g., ``System.currentTimeMillis``.
The purpose is to enable setting time to something else than the system time in order to enable for
example time dependent tests without modifying the system time.

```
public interface TimeSource {
  /**
   * Returns the current time in milliseconds. @see System#currentTimeMillis().
   *
   * @return time in milliseconds
   */
  long currentTimeMillis();

  /**
   * Returns the current value of the running Java Virtual Machine's
   * high-resolution time source, in nanoseconds.  @see System#nanoTime().
   *
   * @return time in nanoseconds
   */
  long nanoTime();
}
```

``DefaultTimeSource`` implements ``TimeSource`` using ``System.currentTimeMillis`` and ``System.nanoTime``.

``FixedTimeSource`` allows for setting and advancing time. This is useful in unit testing. Example:

```
FixedTimeSource ts = new FixedTimeSource("2012-06-07 06:00:00");
...
ts.setDateAndTime("2012-06-07 06:11:00");
...
ts.advanceTime(60 * 1000); // one minute
```

### HolidayExplorer

``HolidayExplorer``, and the implementation ``SwedishHoliday``, is used for
checking if a day is a holiday or not, and if it is a holiday, the local name of it. Holidays for a given year can be
enumerated.

```
HolidayExplorer h = new SwedishHolidayExplorer();
System.out.println(h.getHoliday(DateUtils.parse("2012-06-06")));
```

will print "Nationaldagen", which is the name of a Swedish holiday. The function returns ``null`` if there is
no holiday at this date.

### StateScheduler

``StateScheduler`` schedules events changing a state of something. Events can be scheduled daily, weekly, monthly
yearly or once and modifiers for selecting working and non-working days can be specified.

Initialization example:

```
HolidayExplorer holidayExplorer = new SwedishHolidayExplorer();
StateScheduler<Boolean> scheduler = new StateScheduler<Boolean>(holidayExplorer, false);
scheduler.addEvent(new StateScheduler.Event<Boolean>(new TimeOfDay(19, 0, 0), true)); // Turn on lamps 19:00:00
scheduler.addEvent(new StateScheduler.Event<Boolean>(new TimeOfDay(23, 0, 0), false)); // Turn off lamps 23:00:00
turnOutdoorLamps(scheduler.getCurrentState());
```

At intervals, execute:

```
StateScheduler.Event<Boolean> event = scheduler.getNewState();
if (event != null) {
  turnOutdoorLamps(event.getState());
}
```

### EventScheduler

``EventScheduler`` schedules events without imposing a state on the scheduled event (unlike StateScheduler).
Events can be scheduled daily, weekly, monthly yearly or once and modifiers for selecting working and non-working
days can be specified.

Initialization example:

```
EventScheduler<Boolean> scheduler = new EventScheduler<>(h);
scheduler.addEvent(new SchedulerEvent<>(LocalTime.parse("19:00:00"), true));
scheduler.addEvent(new SchedulerEvent<>(LocalTime.parse("23:00:00"), false));
```

At intervals, execute:

```
SchedulerEvent<Boolean> e = scheduler.consumeEvent();
if (e != null) {
   turnLampSwitch(e.getState());
}
```

## Package util
   
This package in extension to ``java.util``.

### AbstractAdapterIterator

A thin abstraction and a simple class supporting the pattern adapting from type A
to type B while iterating a collection of type A. Left to implement is the next-method which performs the
custom conversion. The main motivation of this pattern is when you have a consumer of Iterable\<B\>, your source is
an Iterable\<A\> and the collection is expensive to copy.

It is more of like a demonstration of a pattern than an actually useful base class.

The following implementation converts Double:s to Integer:s while iterating:

```
class D2iAdapterIterator extends AbstractAdapterIterator<Integer, Double> {
  public D2iAdapterIterator(Iterator<Double> adaptedIter) {
    super(adaptedIter);
  }
  
  @Override
  public Integer next() {
    Double dval = adaptedIter.next();
    return (int) (dval + 0.5); // Convert Double to Integer, round value
  }
}
```

It can be used like this:

```
List<Double> doubles = new ArrayList<Double>() {{ add(0.9); add(2.4); add(2.5); }};

Iterator<Integer> iter = new D2iAdapterIterator(doubles.iterator());
while (iter.hasNext()) {
  Integer value = iter.next(); // Get the rounded value from the Double collection
  ...
}
```

or like this:

```
void consume(Iterable<Integer> integers) { ... }
  ...
  consume(new Iterable<Integer>() {
    @Override
    public Iterator<Integer> iterator() {
      return new D2iAdapterIterator(doubles.iterator());
    }
});
```

### ChainedIterables

``ChainedIterables`` is an adapter class supporting iterating several iterables while having them to look like a single
iterable. The purpose of using ``ChainedIterables`` instead of adding the content of each iterable to
for example an ``ArrayList`` and iterate this list is that the collections are not copied. I a sense ``ChainedIterables``
provides a view to the existing collections. ``ChainedIterables`` does not touch list nor objects in iterables
before actually iterating them which is valuable for example if the iterators implements lazy evaluation.

The motivations of this class are when you have a consumer of Iterable\<B\>, your sources are multiple Iterable\<A\>
and these collection are expensive to copy and/or it is expensive to hit the iterated objects, for example because
they implement lazy evaluation.

Assuming you have a number of iterables like this:

```
List<Iterable<? extends MyBean>> iterables = ...
```

and that you want to pass these iterables to a method taking Iterable\<MyBean\> as a parameter you
wrap the list in a ``ChainedIterables`` object:

```
void consume(Iterable<MyBean> beans) { ... }
...
consume(new ChainedIterables<MyBean>(iterables));
```

Object in the lists are accessed directly in their source lists and the lists are not copied nor even touched until
the consume method is iterating the object.

Iterating all MyBean objects without ``ChainedIterables`` requires a nested loop:

```
for (Iterable<? extends MyBean> iter : iterables) {
  while (iter.hasNext()) {
    MyBean bean = iter.next();
    ...
  }
}
```

with ``ChainedIterables`` there is only need for a flat, single loop:

```
for (MyBean bean : new ChainedIterables<MyBean>(iterables)) {
  ...
}
```

### NestedIterator

Supports iterating child objects in a structure of any depth making it look like a flat
iteration without touching objects before iterating them.

An example will help.

Assume an object hierarchy in three levels, exemplified with
mobile network entities: Site, BaseStation, Cell. A Site can have one or more Base stations and each Base station
has one or more cells:

An iteration across all cells without ``NestedIterator`` can look like this:

```
List<Site> sites = ...
for (Site s : sites) {
  for (BaseStation bs : s.getBaseStations()) {
    for (Cell c : bs.getCells()) {
      System.out.println("Cell " + c.getName());
    }
  }
}
```

and with ``NestedIterator``:

```
Iterator<Cell> cellIter = new CellIterator(new BaseStationIterator(sites.iterator()));
while (cellIter.hasNext()) {
  Cell c = cellIter.next();
  System.out.println("Cell " + c);
  ++count;
}
```

Passing all cells to a method taking Iterable<Cell>

```
void consume(Iterable<Cell> cells) { ... }
...
final Iterator<Cell> cellIter = new CellIterator(new BaseStationIterator(sites.iterator()));
consume(new Iterable<Cell>() {
  @Override
  public Iterator<Cell> iterator() {
    return cellIter;
  }
});
```

### Filter

Filter is a base class defining an interface of filters in the meaning of sluggish variables.
Filter can be instantiated by itself and performs no filtering.

#### FilterSIIR

Performs simple IIR-filtering according to the formula:

```
outLevel = ( (length - 1) * outLevel + inLevel ) / length
```

### HexUtil

HexUtil is a utility class for converting a byte{} from and to Hex string.

```
byte[] arr = new byte[] {1, 0, -128, 0x7f};
String s = HexUtil.asHex(arr);
// s contains "0100807f"
HexUtil.isHexString("10af32"); // evaluates to true
HexUtil.isHexString("foobar"); // evaluates to false
```

### MutableElementQueue

A queue with pre-allocated elements. Elements must be mutable in order
for this queue to be useful because the objects in it cannot be replaced after initialization. This
technique supports queue operation with no heap allocation of objects. This implementation is not thread safe.

### QueueBuffer

Use instead of LinkedList if you have a fixed maximum size of the buffer and if you are worried
about that the LinkedList implementation is allocating a list node-object in addition to the objects you are
storing in the queue.
