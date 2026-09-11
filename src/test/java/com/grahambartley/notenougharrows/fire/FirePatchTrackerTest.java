package com.grahambartley.notenougharrows.fire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class FirePatchTrackerTest {
  private static final BlockPos EARLY_POSITION = new BlockPos(0, 64, 0);
  private static final BlockPos LATE_POSITION = new BlockPos(1, 64, 0);

  private FirePatchTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new FirePatchTracker();
  }

  @Test
  void startsWithNothingBurning() {
    assertTrue(tracker.isEmpty());
    assertEquals(0, tracker.size());
  }

  @ParameterizedTest
  @NullSource
  void aMissingPatchIsNotTracked(final FirePatch patch) {
    tracker.add(patch);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aPatchThatBurnsNothingIsNotTracked() {
    tracker.add(new FirePatch(List.of(), 100L));

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aTrackedPatchIsCounted() {
    tracker.add(new FirePatch(List.of(EARLY_POSITION), 100L));

    assertFalse(tracker.isEmpty());
    assertEquals(1, tracker.size());
  }

  @Test
  void onlyPatchesPastTheirExpiryAreHandedBack() {
    tracker.add(new FirePatch(List.of(EARLY_POSITION), 100L));
    tracker.add(new FirePatch(List.of(LATE_POSITION), 200L));

    assertEquals(List.of(EARLY_POSITION), tracker.takeExpired(150L));
    assertEquals(1, tracker.size());
  }

  @Test
  void anExpiredPatchIsHandedBackOnlyOnce() {
    tracker.add(new FirePatch(List.of(EARLY_POSITION), 100L));

    assertEquals(List.of(EARLY_POSITION), tracker.takeExpired(150L));
    assertEquals(List.of(), tracker.takeExpired(150L));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void nothingIsHandedBackBeforeAnythingHasExpired() {
    tracker.add(new FirePatch(List.of(EARLY_POSITION), 100L));

    assertEquals(List.of(), tracker.takeExpired(99L));
    assertEquals(1, tracker.size());
  }

  @Test
  void everyPositionOfAnExpiredPatchIsHandedBackTogether() {
    tracker.add(new FirePatch(List.of(EARLY_POSITION, LATE_POSITION), 100L));

    assertEquals(List.of(EARLY_POSITION, LATE_POSITION), tracker.takeExpired(100L));
  }

  @Test
  void expiringSeveralPatchesAtOnceHandsBackTheirPositionsInTrackedOrder() {
    tracker.add(new FirePatch(List.of(EARLY_POSITION), 100L));
    tracker.add(new FirePatch(List.of(LATE_POSITION), 100L));

    assertEquals(List.of(EARLY_POSITION, LATE_POSITION), tracker.takeExpired(100L));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void aPatchThatNeverExpiresKeepsBurning() {
    tracker.add(new FirePatch(List.of(LATE_POSITION), Long.MAX_VALUE));

    assertEquals(List.of(), tracker.takeExpired(Long.MAX_VALUE - 1));
    assertEquals(1, tracker.size());
  }
}
