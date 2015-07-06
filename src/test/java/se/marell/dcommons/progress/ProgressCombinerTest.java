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
package se.marell.dcommons.progress;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ProgressCombinerTest {

  private static class TestProgressTracker extends ProgressTrackerAdapter {
    private float lastValue = Float.NaN;
    private boolean cancelled;
    private boolean completed;
    private float cancelValue = Float.NaN;

    public TestProgressTracker() {
    }

    public TestProgressTracker(float cancelValue) {
      this.cancelValue = cancelValue;
    }

    @Override
    public boolean isCancelled() {
      cancelled = lastValue >= cancelValue;
      return cancelled;
    }

    public boolean isCompleted() {
      return completed;
    }

    public void setTotalProgress(float value) {
      assertFalse(cancelled);
      assertTrue("value=" + value + ",lastValue=" + lastValue, Float.isNaN(lastValue) || value >= lastValue);
      assertTrue("value=" + value, value >= 0f);
      assertTrue("value=" + value, value <= 1.0f);
      completed = value >= 0.99f;
      lastValue = value;
    }
  }

  @Test
  public void testOp() {
    TestProgressTracker tps = new TestProgressTracker();
    lengthyOperation(tps);
    assertFalse(tps.isCancelled());
    assertTrue(tps.isCompleted());
  }

  @Test
  public void testOpCancelled() {
    TestProgressTracker tps = new TestProgressTracker(.5f);
    lengthyOperation(tps);
    assertTrue(tps.isCancelled());
    assertFalse(tps.isCompleted());
  }

  @Test
  public void testCompositeOp() {
    TestProgressTracker tps = new TestProgressTracker();
    lengthyCompositeOperation(tps);
    assertFalse(tps.isCancelled());
    assertTrue(tps.isCompleted());
  }

  @Test
  public void testCompositeOpAbitraryWeights() {
    TestProgressTracker tps = new TestProgressTracker();
    lengthyCompositeOperationWithArbitraryWeights(tps);
    assertFalse(tps.isCancelled());
    assertTrue(tps.isCompleted());
  }

  private static void lengthyOperation(ProgressTracker ps) {
    for (int i = 0; i < 100; ++i) {
      ps.setTotalProgress(i / 100.0f);
      if (ps.isCancelled()) {
        return;
      }
    }
  }

  private static void lengthyCompositeOperation(ProgressTracker ps) {
    ProgressCombiner cps = new ProgressCombiner(ps, 1.0f);
    lengthyOperation(cps.createSubProgress(0.2f));
    lengthyOperation(cps.createSubProgress(0.8f));
  }

  private static void lengthyCompositeOperationWithArbitraryWeights(ProgressTracker ps) {
    ProgressCombiner cps = new ProgressCombiner(ps);
    ProgressTracker ps1 = cps.createSubProgress(200f);
    ProgressTracker ps2 = cps.createSubProgress(800f);
    lengthyOperation(ps1);
    lengthyOperation(ps2);
  }
}
