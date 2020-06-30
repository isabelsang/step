// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
@RunWith(JUnit4.class)
public final class TimeRangeTest {
  @Test
  public void equality() {
    assertEquals(TimeRange.fromStartDuration(100, 50), TimeRange.fromStartDuration(100, 50));

    assertNotEquals(
        TimeRange.fromStartDuration(100, 50), TimeRange.fromStartDuration(100, 51));

    assertEquals(
        TimeRange.fromStartDuration(100, 50), TimeRange.fromStartEnd(100, 150, false));

    assertNotEquals(
        TimeRange.fromStartDuration(100, 50), TimeRange.fromStartEnd(100, 150, true));

    assertEquals(
        TimeRange.fromStartDuration(100, 51), TimeRange.fromStartEnd(100, 150, true));

    assertEquals(
        TimeRange.fromStartEnd(100, 151, false), TimeRange.fromStartEnd(100, 150, true));
  }

  @Test
  public void containsPoint() {
    // Range 100 (inclusive) to 150 (exclusive).
    TimeRange range = TimeRange.fromStartDuration(100, 50);
    assertFalse(range.contains(50));
    assertTrue(range.contains(100));
    assertFalse(range.contains(150));
    assertFalse(range.contains(200));
  }

  @Test
  public void containsRange() {
    TimeRange range = TimeRange.fromStartDuration(200, 50);

    // |---|   |--range--|
    assertFalse(range.contains(TimeRange.fromStartDuration(0, 20)));

    //     |--range--|
    // |---|
    assertFalse(range.contains(TimeRange.fromStartDuration(180, 20)));

    //   |--range--|
    // |---|
    assertFalse(range.contains(TimeRange.fromStartDuration(190, 20)));

    // |--range--|
    // |---|
    assertTrue(range.contains(TimeRange.fromStartDuration(200, 20)));

    // |--range--|
    //    |---|
    assertTrue(range.contains(TimeRange.fromStartDuration(210, 20)));

    // |--range--|
    //       |---|
    assertTrue(range.contains(TimeRange.fromStartDuration(230, 20)));

    // |--range--|
    //         |---|
    assertFalse(range.contains(TimeRange.fromStartDuration(240, 20)));

    // |--range--|
    //           |---|
    assertFalse(range.contains(TimeRange.fromStartDuration(250, 20)));

    // |--range--| |---|
    assertFalse(range.contains(TimeRange.fromStartDuration(260, 20)));
  }

  @Test
  public void overlaps() {
    TimeRange range = TimeRange.fromStartDuration(200, 50);

    // |---|   |--range--|
    assertFalse(range.overlaps(TimeRange.fromStartDuration(0, 20)));

    //     |--range--|
    // |---|
    assertFalse(range.overlaps(TimeRange.fromStartDuration(180, 20)));

    //   |--range--|
    // |---|
    assertTrue(range.overlaps(TimeRange.fromStartDuration(190, 20)));

    // |--range--|
    // |---|
    assertTrue(range.overlaps(TimeRange.fromStartDuration(200, 20)));

    // |--range--|
    //    |---|
    assertTrue(range.overlaps(TimeRange.fromStartDuration(210, 20)));

    // |--range--|
    //       |---|
    assertTrue(range.overlaps(TimeRange.fromStartDuration(230, 20)));

    // |--range--|
    //         |---|
    assertTrue(range.overlaps(TimeRange.fromStartDuration(240, 20)));

    // |--range--|
    //           |---|
    assertFalse(range.overlaps(TimeRange.fromStartDuration(250, 20)));

    // |--range--| |---|
    assertFalse(range.overlaps(TimeRange.fromStartDuration(260, 20)));
  }

  @Test
  public void rangeContainsSelf() {
    TimeRange range = TimeRange.fromStartDuration(100, 100);
    assertTrue(range.contains(range));
  }

  @Test
  public void rangeOverlapsSelf() {
    TimeRange range = TimeRange.fromStartDuration(100, 100);
    assertTrue(range.overlaps(range));
  }

  @Test
  public void emptyRangeContainsNothing() {
    TimeRange range = TimeRange.fromStartDuration(100, 0);

    assertFalse(range.contains(100));
    assertFalse(range.contains(range));
  }

  @Test
  public void canContainEmptyRange() {
    // Range from 100 (inclusive) to 200 (inclusive).
    TimeRange range = TimeRange.fromStartEnd(100, 200, true);

    TimeRange emptyStart = TimeRange.fromStartDuration(100, 0);
    TimeRange emptyMiddle = TimeRange.fromStartDuration(150, 0);
    TimeRange emptyEnd = TimeRange.fromStartDuration(200, 0);

    assertTrue(range.contains(emptyStart));
    assertTrue(range.contains(emptyMiddle));
    assertTrue(range.contains(emptyEnd));
  }

  @Test
  public void canOverlapEmptyRange() {
    // Range from 100 (inclusive) to 200 (inclusive).
    TimeRange range = TimeRange.fromStartEnd(100, 200, true);

    TimeRange emptyStart = TimeRange.fromStartDuration(100, 0);
    TimeRange emptyMiddle = TimeRange.fromStartDuration(150, 0);
    TimeRange emptyEnd = TimeRange.fromStartDuration(200, 0);

    assertTrue(range.overlaps(emptyStart));
    assertTrue(range.overlaps(emptyMiddle));
    assertTrue(range.overlaps(emptyEnd));

    // Overlaps is commutative, meaning that regardless of order, the result should be the same.
    // There for "a overlaps b" should be the same as "b overlaps a".
    assertTrue(emptyStart.overlaps(range));
    assertTrue(emptyMiddle.overlaps(range));
    assertTrue(emptyEnd.overlaps(range));
  }
}
