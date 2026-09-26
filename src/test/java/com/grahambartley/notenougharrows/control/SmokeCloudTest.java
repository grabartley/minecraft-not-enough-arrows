package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class SmokeCloudTest {

  private static final Vec3d CENTRE = new Vec3d(0.0, 64.0, 0.0);

  @Test
  void expiresOnItsExpiryTick() {
    final SmokeCloud cloud = new SmokeCloud(CENTRE, 3.0, 100L);

    assertFalse(cloud.hasExpired(99L));
    assertTrue(cloud.hasExpired(100L));
  }

  @Test
  void holdsWhatIsInsideItsRadius() {
    final SmokeCloud cloud = new SmokeCloud(CENTRE, 3.0, 100L);

    assertTrue(cloud.contains(CENTRE));
    assertTrue(cloud.contains(new Vec3d(2.9, 64.0, 0.0)));
  }

  @Test
  void holdsWhatSitsExactlyOnItsEdge() {
    assertTrue(new SmokeCloud(CENTRE, 3.0, 100L).contains(new Vec3d(3.0, 64.0, 0.0)));
  }

  @Test
  void leavesWhatIsOutsideItsRadiusAlone() {
    final SmokeCloud cloud = new SmokeCloud(CENTRE, 3.0, 100L);

    assertFalse(cloud.contains(new Vec3d(3.1, 64.0, 0.0)));
    assertFalse(cloud.contains(new Vec3d(0.0, 68.0, 0.0)));
  }

  @Test
  void holdsNothingWhenAskedAboutNowhere() {
    assertFalse(new SmokeCloud(CENTRE, 3.0, 100L).contains(null));
  }

  @Test
  void clampsANegativeRadiusToNothing() {
    assertEquals(0.0, new SmokeCloud(CENTRE, -5.0, 100L).radius());
  }

  @Test
  void refusesACloudWithNoCentre() {
    assertThrows(NullPointerException.class, () -> new SmokeCloud(null, 3.0, 100L));
  }
}
