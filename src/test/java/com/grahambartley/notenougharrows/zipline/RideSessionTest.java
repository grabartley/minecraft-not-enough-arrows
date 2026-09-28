package com.grahambartley.notenougharrows.zipline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.grapple.GrappleProgress;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

class RideSessionTest {
  private static final UUID RIDER = UUID.randomUUID();
  private static final BlockPos WEST_END = new BlockPos(0, 70, 0);
  private static final BlockPos EAST_END = new BlockPos(10, 70, 0);
  private static final Span SPAN =
      new Span(UUID.randomUUID(), List.of(WEST_END, WEST_END.east(5), EAST_END), 1000L);

  @Test
  void aRiderBoardingNearTheWestEndRidesEast() {
    final RideSession ride =
        RideSession.toTheFarEnd(RIDER, SPAN, Vec3d.ofCenter(WEST_END.east()), 0.6);

    assertEquals(SPAN.firstEnd(), ride.from());
    assertEquals(SPAN.lastEnd(), ride.to());
  }

  @Test
  void aRiderBoardingNearTheEastEndRidesWest() {
    final RideSession ride =
        RideSession.toTheFarEnd(RIDER, SPAN, Vec3d.ofCenter(EAST_END.west()), 0.6);

    assertEquals(SPAN.lastEnd(), ride.from());
    assertEquals(SPAN.firstEnd(), ride.to());
  }

  @Test
  void aNewRideCarriesItsSpanAndABudgetForItsLength() {
    final Vec3d grip = Vec3d.ofCenter(WEST_END);

    final RideSession ride = RideSession.toTheFarEnd(RIDER, SPAN, grip, 0.6);

    assertEquals(RIDER, ride.riderId());
    assertEquals(SPAN.id(), ride.spanId());
    assertEquals(RidePath.lifetimeTicks(ride.from(), ride.to(), 0.6), ride.remainingTicks());
    assertEquals(0, ride.riddenTicks());
  }

  @Test
  void ridingOneTickSpendsOneTickOfTheBudget() {
    final RideSession ride = RideSession.toTheFarEnd(RIDER, SPAN, SPAN.firstEnd(), 0.6);

    final RideSession rode = ride.rode(8.0);

    assertEquals(ride.remainingTicks() - 1, rode.remainingTicks());
    assertEquals(1, rode.riddenTicks());
  }

  @Test
  void aRideRunsOutOfTimeWhenItsBudgetIsSpent() {
    final RideSession lastTick =
        RideSession.beginning(RIDER, SPAN.id(), SPAN.firstEnd(), SPAN.lastEnd(), 1, 9.0);

    assertFalse(lastTick.hasExpired());
    assertTrue(lastTick.rode(9.0).hasExpired());
  }

  @Test
  void aRideThatStopsClosingOnTheFarEndHasStopped() {
    RideSession ride =
        RideSession.beginning(RIDER, SPAN.id(), SPAN.firstEnd(), SPAN.lastEnd(), 500, 9.0);
    for (int tick = 0; tick < GrappleProgress.IDLE_TICKS_LIMIT; tick++) {
      assertFalse(ride.hasStopped(), "tick " + tick);
      ride = ride.rode(9.0);
    }

    assertTrue(ride.hasStopped());
  }

  @Test
  void aRideThatKeepsClosingHasNotStopped() {
    RideSession ride =
        RideSession.beginning(RIDER, SPAN.id(), SPAN.firstEnd(), SPAN.lastEnd(), 500, 9.0);
    for (int tick = 1; tick <= GrappleProgress.IDLE_TICKS_LIMIT * 2; tick++) {
      ride = ride.rode(9.0 - tick * 0.1);
    }

    assertFalse(ride.hasStopped());
  }

  @Test
  void aRideNeedsItsRiderSpanAndEnds() {
    final UUID span = SPAN.id();
    final Vec3d end = SPAN.firstEnd();
    final GrappleProgress progress = GrappleProgress.startingAt(1.0);
    assertThrows(
        NullPointerException.class, () -> new RideSession(null, span, end, end, 1, 0, progress));
    assertThrows(
        NullPointerException.class, () -> new RideSession(RIDER, null, end, end, 1, 0, progress));
    assertThrows(
        NullPointerException.class, () -> new RideSession(RIDER, span, null, end, 1, 0, progress));
    assertThrows(
        NullPointerException.class, () -> new RideSession(RIDER, span, end, null, 1, 0, progress));
    assertThrows(
        NullPointerException.class, () -> new RideSession(RIDER, span, end, end, 1, 0, null));
  }
}
