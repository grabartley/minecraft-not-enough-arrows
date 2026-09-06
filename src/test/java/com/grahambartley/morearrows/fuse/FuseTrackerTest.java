package com.grahambartley.morearrows.fuse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class FuseTrackerTest {
  private static final UUID FIRST_HOST = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
  private static final UUID SECOND_HOST = UUID.fromString("00000000-0000-0000-0000-0000000000a2");
  private static final int DELAY_TICKS = 60;

  private FuseTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new FuseTracker();
  }

  @Test
  void startsWithNothingBurning() {
    assertTrue(tracker.isEmpty());
    assertEquals(0, tracker.size());
  }

  @ParameterizedTest
  @NullSource
  void aMissingFuseIsNotTracked(final Fuse fuse) {
    tracker.add(fuse);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aFuseThatHasAlreadyExpiredIsNotTracked() {
    tracker.add(Fuse.lit(FIRST_HOST, 0));

    assertTrue(tracker.isEmpty());
  }

  @Test
  void aLitFuseIsFoundByTheHostThatCarriesIt() {
    final Fuse fuse = Fuse.lit(FIRST_HOST, DELAY_TICKS);
    tracker.add(fuse);

    assertEquals(fuse, tracker.fuseOn(FIRST_HOST));
    assertEquals(1, tracker.size());
  }

  @Test
  void aHostCarriesAtMostOneFuse() {
    tracker.add(Fuse.lit(FIRST_HOST, DELAY_TICKS));
    tracker.add(Fuse.lit(FIRST_HOST, 20));

    assertEquals(1, tracker.size());
    assertEquals(20, tracker.fuseOn(FIRST_HOST).delayTicks());
  }

  @Test
  void burningAFuseDownReplacesWhatIsTrackedForItsHost() {
    tracker.add(Fuse.lit(FIRST_HOST, DELAY_TICKS));
    tracker.add(tracker.fuseOn(FIRST_HOST).burned());

    assertEquals(1, tracker.size());
    assertEquals(DELAY_TICKS - 1, tracker.fuseOn(FIRST_HOST).remainingTicks());
  }

  @ParameterizedTest
  @NullSource
  void nothingIsFoundForAMissingHost(final UUID hostId) {
    tracker.add(Fuse.lit(FIRST_HOST, DELAY_TICKS));

    assertNull(tracker.fuseOn(hostId));
    assertNull(tracker.remove(hostId));
  }

  @Test
  void anUntrackedHostCarriesNothing() {
    assertNull(tracker.fuseOn(SECOND_HOST));
  }

  @Test
  void extinguishingAFuseHandsItBackAndStopsTrackingIt() {
    final Fuse fuse = Fuse.lit(FIRST_HOST, DELAY_TICKS);
    tracker.add(fuse);

    assertEquals(fuse, tracker.remove(FIRST_HOST));
    assertTrue(tracker.isEmpty());
    assertNull(tracker.remove(FIRST_HOST));
  }

  @Test
  void severalHostsBurnTheirOwnFusesInTrackedOrder() {
    final Fuse first = Fuse.lit(FIRST_HOST, DELAY_TICKS);
    final Fuse second = Fuse.lit(SECOND_HOST, 20);
    tracker.add(first);
    tracker.add(second);

    assertEquals(List.of(first, second), tracker.fuses());
    assertEquals(2, tracker.size());
  }

  @Test
  void extinguishingOneHostsFuseLeavesTheOthersBurning() {
    tracker.add(Fuse.lit(FIRST_HOST, DELAY_TICKS));
    tracker.add(Fuse.lit(SECOND_HOST, DELAY_TICKS));

    tracker.remove(FIRST_HOST);

    assertFalse(tracker.isEmpty());
    assertNull(tracker.fuseOn(FIRST_HOST));
    assertEquals(1, tracker.size());
  }
}
