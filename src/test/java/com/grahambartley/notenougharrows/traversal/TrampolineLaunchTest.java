package com.grahambartley.notenougharrows.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

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
}
