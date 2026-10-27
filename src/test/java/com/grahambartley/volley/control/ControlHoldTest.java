package com.grahambartley.volley.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ControlHoldTest {

  private static final UUID MOB = UUID.nameUUIDFromBytes("mob".getBytes());
  private static final Vec3d ANCHOR = new Vec3d(1.0, 2.0, 3.0);
  private static final UUID SUBJECT = UUID.nameUUIDFromBytes("subject".getBytes());

  @Test
  void holdsUntilItsExpiryTick() {
    final ControlHold hold = ControlHold.drawn(MOB, ANCHOR, Optional.empty(), 100L);

    assertFalse(hold.hasExpired(99L));
    assertTrue(hold.hasExpired(100L));
    assertTrue(hold.hasExpired(101L));
  }

  @Test
  void carriesTheSteeringItWasHeldWith() {
    assertEquals(
        ControlSteering.FLEEING,
        new ControlHold(MOB, ANCHOR, Optional.empty(), 0.0, ControlSteering.FLEEING, 10L)
            .steering());
  }

  @Test
  void refusesAHoldWithoutAMobAnAnchorOrASteering() {
    assertThrows(
        NullPointerException.class, () -> ControlHold.drawn(null, ANCHOR, Optional.empty(), 1L));
    assertThrows(
        NullPointerException.class, () -> ControlHold.drawn(MOB, null, Optional.empty(), 1L));
    assertThrows(
        NullPointerException.class,
        () -> new ControlHold(MOB, ANCHOR, Optional.empty(), 0.0, null, 1L));
  }

  @ParameterizedTest
  @CsvSource({"15.9, 0.0, false", "16.0, 0.0, true", "0.0, 20.0, true", "3.0, 3.0, false"})
  void aFleeingMobIsSafeOnceItIsTheFleeDistanceFromTheImpactOnTheGround(
      final double dx, final double dz, final boolean expected) {
    final ControlHold hold = ControlHold.fleeing(UUID.randomUUID(), Vec3d.ZERO, 16.0, 100L);
    assertEquals(expected, hold.hasFledFarEnough(new Vec3d(dx, 30.0, dz)));
  }

  @Test
  void aFleeDistanceOfZeroMeansRunForTheWholeHold() {
    final ControlHold hold = ControlHold.fleeing(UUID.randomUUID(), Vec3d.ZERO, 0.0, 100L);
    assertFalse(hold.hasFledFarEnough(new Vec3d(500.0, 0.0, 0.0)));
  }
}
