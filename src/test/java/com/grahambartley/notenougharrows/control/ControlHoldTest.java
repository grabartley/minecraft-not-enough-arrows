package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    final ControlHold hold = ControlHold.at(MOB, ANCHOR, ControlSteering.DRAWN, 100L);

    assertFalse(hold.hasExpired(99L));
    assertTrue(hold.hasExpired(100L));
    assertTrue(hold.hasExpired(101L));
  }

  @Test
  void carriesTheSteeringItWasHeldWith() {
    assertEquals(
        ControlSteering.FLEEING,
        ControlHold.at(MOB, ANCHOR, ControlSteering.FLEEING, 10L).steering());
  }

  @Test
  void refusesAHoldWithoutAMobAnAnchorOrASteering() {
    assertThrows(
        NullPointerException.class, () -> ControlHold.at(null, ANCHOR, ControlSteering.DRAWN, 1L));
    assertThrows(
        NullPointerException.class, () -> ControlHold.at(MOB, null, ControlSteering.DRAWN, 1L));
    assertThrows(NullPointerException.class, () -> ControlHold.at(MOB, ANCHOR, null, 1L));
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
