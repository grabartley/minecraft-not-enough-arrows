package com.grahambartley.volley.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrampolineLaunchTest {

  @Test
  void aLaunchSendsWhatLandedUpAtTheConfiguredStrength() {
    assertEquals(
        new Vec3d(0.3, 1.2f, -0.2), TrampolineLaunch.velocity(new Vec3d(0.3, -0.9, -0.2), 1.2f));
  }

  @Test
  void aLaunchKeepsWhateverWayItWasAlreadyMoving() {
    final Vec3d launched = TrampolineLaunch.velocity(new Vec3d(0.5, -0.1, 0.25), 0.8f);

    assertEquals(0.5, launched.getX());
    assertEquals(0.25, launched.getZ());
  }

  @Test
  void aPadWithNoStrengthOnlyStopsTheFall() {
    assertEquals(
        new Vec3d(0.3, 0.0, 0.0), TrampolineLaunch.velocity(new Vec3d(0.3, -0.9, 0.0), 0.0f));
  }

  @ParameterizedTest(name = "falling {0} at strength {1} leaves at {2}")
  @CsvSource({"-0.9, 1.2, 1.2", "-0.1, 0.8, 0.8", "0.4, 1.0, 1.0", "-2.0, 0.0, 0.0"})
  void everyLaunchLeavesUpwardAtTheConfiguredStrength(
      final double fallingAt, final float strength, final double leavesAt) {
    assertEquals(
        leavesAt, TrampolineLaunch.velocity(new Vec3d(0.0, fallingAt, 0.0), strength).y, 1.0e-6);
  }
}
