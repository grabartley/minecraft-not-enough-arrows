package com.grahambartley.morearrows.redstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class RedstoneChargeTrackerTest {
  private static final BlockPos EARLY_POSITION = new BlockPos(0, 64, 0);
  private static final BlockPos LATE_POSITION = new BlockPos(1, 64, 0);

  private RedstoneChargeTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new RedstoneChargeTracker();
  }

  @Test
  void startsWithNothingPowered() {
    assertTrue(tracker.isEmpty());
    assertEquals(0, tracker.size());
  }

  @ParameterizedTest
  @NullSource
  void aMissingChargeIsNotTracked(final RedstoneCharge charge) {
    tracker.add(charge);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aTrackedChargeIsCountedAndFound() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));

    assertFalse(tracker.isEmpty());
    assertEquals(1, tracker.size());
    assertTrue(tracker.contains(EARLY_POSITION));
  }

  @Test
  void anUntrackedPositionIsNotFound() {
    assertFalse(tracker.contains(EARLY_POSITION));
  }

  @ParameterizedTest
  @NullSource
  void aMissingPositionIsNeverFound(final BlockPos missing) {
    assertFalse(tracker.contains(missing));
  }

  @Test
  void onlyChargesPastTheirExpiryAreHandedBack() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));
    tracker.add(new RedstoneCharge(LATE_POSITION, 200L));

    assertEquals(List.of(EARLY_POSITION), tracker.takeExpired(150L));
    assertEquals(1, tracker.size());
  }

  @Test
  void anExpiredChargeIsHandedBackOnlyOnce() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));

    assertEquals(List.of(EARLY_POSITION), tracker.takeExpired(150L));
    assertEquals(List.of(), tracker.takeExpired(150L));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void nothingIsHandedBackBeforeAnythingHasExpired() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));

    assertEquals(List.of(), tracker.takeExpired(99L));
    assertEquals(1, tracker.size());
  }

  @Test
  void rechargingAPositionReplacesItsExpiryRatherThanStackingASecondCharge() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));
    tracker.add(new RedstoneCharge(EARLY_POSITION, 300L));

    assertEquals(1, tracker.size());
    assertEquals(List.of(), tracker.takeExpired(150L));
    assertEquals(List.of(EARLY_POSITION), tracker.takeExpired(300L));
  }

  @Test
  void takingEveryChargeEmptiesTheTracker() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, Long.MAX_VALUE));
    tracker.add(new RedstoneCharge(LATE_POSITION, Long.MAX_VALUE));

    assertEquals(2, tracker.takeAll().size());
    assertTrue(tracker.isEmpty());
  }

  @Test
  void takingEveryChargeHandsBackChargesThatHaveNotExpired() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, Long.MAX_VALUE));

    assertEquals(List.of(EARLY_POSITION), tracker.takeAll());
  }

  @Test
  void aRemovedChargeIsNoLongerTracked() {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));

    tracker.remove(EARLY_POSITION);

    assertTrue(tracker.isEmpty());
    assertFalse(tracker.contains(EARLY_POSITION));
  }

  @ParameterizedTest
  @NullSource
  void removingAMissingPositionChangesNothing(final BlockPos missing) {
    tracker.add(new RedstoneCharge(EARLY_POSITION, 100L));

    tracker.remove(missing);

    assertEquals(1, tracker.size());
  }
}
