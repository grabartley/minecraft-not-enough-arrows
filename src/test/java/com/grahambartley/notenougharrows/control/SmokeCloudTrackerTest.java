package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class SmokeCloudTrackerTest {

  private static final Vec3d CENTRE = new Vec3d(0.0, 64.0, 0.0);

  @Test
  void startsEmpty() {
    assertTrue(new SmokeCloudTracker().isEmpty());
  }

  @Test
  void removesOnlyTheCloudsThatHaveExpired() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    final SmokeCloud lasting = new SmokeCloud(CENTRE, 3.0, 150L);
    tracker.add(new SmokeCloud(CENTRE, 3.0, 50L));
    tracker.add(lasting);

    tracker.removeExpired(100L);

    assertEquals(List.of(lasting), tracker.live());
  }

  @Test
  void keepsACloudStandingRightUpToItsExpiryTick() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    tracker.add(new SmokeCloud(CENTRE, 3.0, 100L));

    tracker.removeExpired(99L);
    assertEquals(1, tracker.size());

    tracker.removeExpired(100L);
    assertTrue(tracker.isEmpty());
  }

  @Test
  void keepsEveryLiveCloudIncludingTwoAtTheSamePlace() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    tracker.add(new SmokeCloud(CENTRE, 3.0, 100L));
    tracker.add(new SmokeCloud(CENTRE, 3.0, 100L));

    assertEquals(2, tracker.live().size());
  }
}
