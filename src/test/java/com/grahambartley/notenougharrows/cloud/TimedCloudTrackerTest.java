package com.grahambartley.notenougharrows.cloud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class TimedCloudTrackerTest {

  private static final Vec3d CENTRE = new Vec3d(0.0, 64.0, 0.0);

  @Test
  void startsEmpty() {
    assertTrue(new TimedCloudTracker().isEmpty());
  }

  @Test
  void removesOnlyTheCloudsThatHaveExpired() {
    final TimedCloudTracker tracker = new TimedCloudTracker();
    final TimedCloud lasting = new TimedCloud(CENTRE, 3.0, 150L);
    tracker.add(new TimedCloud(CENTRE, 3.0, 50L));
    tracker.add(lasting);

    tracker.removeExpired(100L);

    assertEquals(List.of(lasting), tracker.live());
  }

  @Test
  void keepsACloudStandingRightUpToItsExpiryTick() {
    final TimedCloudTracker tracker = new TimedCloudTracker();
    tracker.add(new TimedCloud(CENTRE, 3.0, 100L));

    tracker.removeExpired(99L);
    assertEquals(1, tracker.size());

    tracker.removeExpired(100L);
    assertTrue(tracker.isEmpty());
  }

  @Test
  void keepsEveryLiveCloudIncludingTwoAtTheSamePlace() {
    final TimedCloudTracker tracker = new TimedCloudTracker();
    tracker.add(new TimedCloud(CENTRE, 3.0, 100L));
    tracker.add(new TimedCloud(CENTRE, 3.0, 100L));

    assertEquals(2, tracker.live().size());
  }
}
