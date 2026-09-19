package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class ControlHoldTrackerTest {

  private static final Vec3d ANCHOR = new Vec3d(0.0, 64.0, 0.0);
  private static final UUID FIRST = UUID.nameUUIDFromBytes("first".getBytes());
  private static final UUID SECOND = UUID.nameUUIDFromBytes("second".getBytes());

  private static ControlHold hold(final UUID mob, final long expiry) {
    return new ControlHold(mob, ANCHOR, ControlSteering.DRAWN, expiry);
  }

  @Test
  void startsEmpty() {
    assertTrue(new ControlHoldTracker().isEmpty());
  }

  @Test
  void keepsOneHoldPerMobSoASecondShotReplacesTheFirst() {
    final ControlHoldTracker tracker = new ControlHoldTracker();
    tracker.hold(hold(FIRST, 50L));
    tracker.hold(new ControlHold(FIRST, ANCHOR, ControlSteering.FLEEING, 90L));

    assertEquals(1, tracker.size());
    assertEquals(ControlSteering.FLEEING, tracker.find(FIRST).orElseThrow().steering());
    assertEquals(90L, tracker.find(FIRST).orElseThrow().expiryTick());
  }

  @Test
  void takesOnlyTheHoldsThatHaveExpired() {
    final ControlHoldTracker tracker = new ControlHoldTracker();
    tracker.hold(hold(FIRST, 50L));
    tracker.hold(hold(SECOND, 150L));

    assertEquals(List.of(hold(FIRST, 50L)), tracker.takeExpired(100L));
    assertEquals(1, tracker.size());
    assertTrue(tracker.find(SECOND).isPresent());
  }

  @Test
  void anExpiredHoldIsTakenOnlyOnce() {
    final ControlHoldTracker tracker = new ControlHoldTracker();
    tracker.hold(hold(FIRST, 50L));
    tracker.takeExpired(100L);

    assertEquals(List.of(), tracker.takeExpired(100L));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void forgetsAHoldOnRequest() {
    final ControlHoldTracker tracker = new ControlHoldTracker();
    tracker.hold(hold(FIRST, 50L));
    tracker.forget(FIRST);

    assertEquals(Optional.empty(), tracker.find(FIRST));
    assertTrue(tracker.isEmpty());
  }

  @Test
  void reportsEveryLiveHold() {
    final ControlHoldTracker tracker = new ControlHoldTracker();
    tracker.hold(hold(FIRST, 50L));
    tracker.hold(hold(SECOND, 60L));

    assertEquals(2, tracker.live().size());
    assertFalse(tracker.isEmpty());
  }
}
