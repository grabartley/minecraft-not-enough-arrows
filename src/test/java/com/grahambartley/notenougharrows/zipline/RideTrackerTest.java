package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RideTrackerTest {
  private static final UUID SPAN = UUID.randomUUID();
  private static final Vec3d FROM = new Vec3d(0.5, 70.5, 0.5);
  private static final Vec3d TO = new Vec3d(10.5, 70.5, 0.5);

  private RideTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new RideTracker();
  }

  @Test
  void aPlayerRidesAtMostOneSpanAtATime() {
    final UUID rider = UUID.randomUUID();
    tracker.add(ride(rider, 50));
    final RideSession second = ride(rider, 60);
    tracker.add(second);

    assertEquals(1, tracker.size());
    assertEquals(second, tracker.rideOf(rider));
  }

  @Test
  void severalPlayersRideTheSameSpanIndependently() {
    final UUID first = UUID.randomUUID();
    final UUID second = UUID.randomUUID();
    tracker.add(ride(first, 50));
    tracker.add(ride(second, 50));

    tracker.remove(first);

    assertNull(tracker.rideOf(first));
    assertEquals(second, tracker.rideOf(second).riderId());
  }

  @Test
  void aRideWithNoTimeLeftIsNotTracked() {
    tracker.add(ride(UUID.randomUUID(), 0));
    tracker.add(null);

    assertTrue(tracker.isEmpty());
  }

  @Test
  void theRidesAreACopy() {
    final RideSession ride = ride(UUID.randomUUID(), 50);
    tracker.add(ride);

    final List<RideSession> rides = tracker.rides();
    tracker.remove(ride.riderId());

    assertEquals(List.of(ride), rides);
  }

  @Test
  void aMissingRiderHasNoRide() {
    assertNull(tracker.rideOf(null));
    assertNull(tracker.remove(null));
  }

  private static RideSession ride(final UUID rider, final int ticks) {
    return RideSession.beginning(rider, SPAN, FROM, TO, ticks, 10.0);
  }
}
