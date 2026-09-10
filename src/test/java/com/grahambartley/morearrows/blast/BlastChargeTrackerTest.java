package com.grahambartley.morearrows.blast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.explosive.ExplosiveTier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlastChargeTrackerTest {

  private static final UUID FIRST = UUID.randomUUID();
  private static final UUID SECOND = UUID.randomUUID();

  private BlastChargeTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new BlastChargeTracker();
  }

  @Test
  void startsWithNothingBurning() {
    assertTrue(tracker.isEmpty());
    assertEquals(0, tracker.size());
  }

  @Test
  void findsAChargeByTheCarrierItWasAddedAgainst() {
    final BlastCharge charge = new BlastCharge(FIRST, ExplosiveTier.TNT, null);
    tracker.add(charge);

    assertEquals(charge, tracker.chargeOn(FIRST));
    assertNull(tracker.chargeOn(SECOND));
  }

  @Test
  void keepsOneChargePerCarrierSoARearmReplacesRatherThanStacks() {
    tracker.add(new BlastCharge(FIRST, ExplosiveTier.GUNPOWDER, null));
    tracker.add(new BlastCharge(FIRST, ExplosiveTier.FIRE_CHARGE, null));

    assertEquals(1, tracker.size());
    assertEquals(ExplosiveTier.FIRE_CHARGE, tracker.chargeOn(FIRST).tier());
  }

  @Test
  void handsBackTheChargeItRemoves() {
    final BlastCharge charge = new BlastCharge(FIRST, ExplosiveTier.TNT, null);
    tracker.add(charge);

    assertEquals(charge, tracker.remove(FIRST));
    assertTrue(tracker.isEmpty());
    assertNull(tracker.remove(FIRST));
  }

  @Test
  void ignoresNothingRatherThanTrackingIt() {
    tracker.add(null);

    assertTrue(tracker.isEmpty());
    assertNull(tracker.chargeOn(null));
    assertNull(tracker.remove(null));
  }

  @Test
  void dropsEveryChargeWhoseCarrierNoLongerHasAFuse() {
    tracker.add(new BlastCharge(FIRST, ExplosiveTier.TNT, null));
    tracker.add(new BlastCharge(SECOND, ExplosiveTier.GUNPOWDER, null));

    tracker.retainOnly(List.of(SECOND));

    assertEquals(1, tracker.size());
    assertNull(tracker.chargeOn(FIRST));
    assertEquals(ExplosiveTier.GUNPOWDER, tracker.chargeOn(SECOND).tier());
  }

  @Test
  void dropsEverythingWhenNoFuseIsBurningAtAll() {
    tracker.add(new BlastCharge(FIRST, ExplosiveTier.TNT, null));

    tracker.retainOnly(null);

    assertTrue(tracker.isEmpty());
  }
}
