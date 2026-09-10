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
    final BlastCharge charge = BlastCharge.on(FIRST, ExplosiveTier.TNT);
    tracker.add(charge);

    assertEquals(charge, tracker.chargeOn(FIRST));
    assertNull(tracker.chargeOn(SECOND));
  }

  @Test
  void keepsOneChargePerCarrierSoARearmReplacesRatherThanStacks() {
    tracker.add(BlastCharge.on(FIRST, ExplosiveTier.GUNPOWDER));
    tracker.add(BlastCharge.on(FIRST, ExplosiveTier.FIRE_CHARGE));

    assertEquals(1, tracker.size());
    assertEquals(ExplosiveTier.FIRE_CHARGE, tracker.chargeOn(FIRST).tier());
  }

  @Test
  void handsBackTheChargeItRemoves() {
    final BlastCharge charge = BlastCharge.on(FIRST, ExplosiveTier.TNT);
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
    tracker.add(BlastCharge.on(FIRST, ExplosiveTier.TNT));
    tracker.add(BlastCharge.on(SECOND, ExplosiveTier.GUNPOWDER));

    tracker.retainOnly(List.of(SECOND));

    assertEquals(1, tracker.size());
    assertNull(tracker.chargeOn(FIRST));
    assertEquals(ExplosiveTier.GUNPOWDER, tracker.chargeOn(SECOND).tier());
  }

  @Test
  void dropsEverythingWhenNoFuseIsBurningAtAll() {
    tracker.add(BlastCharge.on(FIRST, ExplosiveTier.TNT));

    tracker.retainOnly(null);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void listsEveryChargeItHolds() {
    final BlastCharge first = BlastCharge.on(FIRST, ExplosiveTier.TNT);
    final BlastCharge second = BlastCharge.on(SECOND, ExplosiveTier.GUNPOWDER);
    tracker.add(first);
    tracker.add(second);

    assertEquals(List.of(first, second), tracker.charges());
  }
}
