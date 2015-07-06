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

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * Combines one or more lengthy operations with progress feedback into one progress tracker. Each sub operation
 * receives a weight.
 * </p>
 *
 * <p>
 * The caller shall assign weights to all its sub progress operations so that their sum is approximately 1.0.
 * </p>
 * Usage example:
 * <pre>
 * void lengthyCompositeOperation(ProgressTracker pt) {
 *   ProgressCombiner combiner = new ProgressCombiner(pt);
 *   lengthySubOperation(combiner.createSubProgress(0.2f));
 *   lengthySubOperation(combiner.createSubProgress(0.8f));
 * }
 * void lengthySubOperation(ProgressTracker pt) {
 *   ...
 * }
 * </pre>
 */
public class ProgressCombiner {

  private class SubProgressTracker extends ProgressTrackerAdapter {
    private ProgressTracker parent;
    private float value;

    public SubProgressTracker(ProgressTracker parent) {
      this.parent = parent;
    }

    @Override
    public boolean isCancelled() {
      return parent.isCancelled();
    }

    @Override
    public void setTotalProgress(float value) {
      this.value = value;
      setProgress();
    }

    @Override
    public void setProgressLabel(String text) {
      parent.setProgressLabel(text);
    }

    @Override
    public void activityReport(int count) {
      parent.activityReport(count);
    }

    public float getValue() {
      return value;
    }
  }

  private static class SubItem {
    private SubProgressTracker pt;
    private float weight;

    private SubItem(SubProgressTracker pt, float weight) {
      this.pt = pt;
      this.weight = weight;
    }

    public SubProgressTracker getProgressTracker() {
      return pt;
    }

    public float getWeight() {
      return weight;
    }
  }

  private ProgressTracker pt;
  private Collection<SubItem> subItems = new ArrayList<SubItem>();
  private float totalWeight = Float.NaN;
  private float summedWeights;

  /**
   * @param pt     The progress tracker
   */
  public ProgressCombiner(ProgressTracker pt) {
    this.pt = pt;
  }

  /**
   * Using this constructor makes it possible start using ProgressTrackers before creating all of them
   * using @see #createSubProgress(float).
   *
   * @param pt          The parent progress tracker receiving the aggregated progress status
   * @param totalWeight The total weight of all weight parameters in all calls
   *                    to @see #createSubProgress(float)
   */
  public ProgressCombiner(ProgressTracker pt, float totalWeight) {
    this.pt = pt;
    this.totalWeight = totalWeight;
  }

  /**
   * Create a sub progress tracker with the specified weight.
   *
   * @param weight Weight of this sub progress. The total weight of all sub progress operations is recommended to
   *        add up to 1.0
   * @return A newly created progress tracker
   */
  public ProgressTracker createSubProgress(float weight) {
    if (Float.isNaN(totalWeight)) {
      summedWeights += weight;
    }
    SubProgressTracker subPt = new SubProgressTracker(pt);
    subItems.add(new SubItem(subPt, weight));
    return subPt;
  }

  public boolean isCancelled() {
    for (SubItem si : subItems) {
      if (si.getProgressTracker().isCancelled()) {
        return true;
      }
    }
    return false;
  }

  public void setProgress() {
    float totalProgress = 0;
    for (SubItem si : subItems) {
      float subWeight;
      if (Float.isNaN(totalWeight)) {
        subWeight = si.getWeight() / summedWeights;
      } else {
        subWeight = si.getWeight() / totalWeight;
      }
      totalProgress += si.getProgressTracker().getValue() * subWeight;
    }
    pt.setTotalProgress(totalProgress);
  }
}
