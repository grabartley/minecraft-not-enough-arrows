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
  void ignoresAMissingCloud() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    tracker.add(null);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void ignoresACloudWithNoReach() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    tracker.add(new SmokeCloud(CENTRE, 0.0, 100L));

    assertTrue(tracker.isEmpty());
  }

  @Test
  void takesOnlyTheCloudsThatHaveExpired() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    final SmokeCloud early = new SmokeCloud(CENTRE, 3.0, 50L);
    tracker.add(early);
    tracker.add(new SmokeCloud(CENTRE, 3.0, 150L));

    assertEquals(List.of(early), tracker.takeExpired(100L));
    assertEquals(1, tracker.size());
  }

  @Test
  void anExpiredCloudIsTakenOnlyOnce() {
    final SmokeCloudTracker tracker = new SmokeCloudTracker();
    tracker.add(new SmokeCloud(CENTRE, 3.0, 50L));
    tracker.takeExpired(100L);

    assertEquals(List.of(), tracker.takeExpired(100L));
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
