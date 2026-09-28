package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ZiplineArrowConfig;
import com.grahambartley.notenougharrows.grapple.GrappleFallGuard;
import com.grahambartley.notenougharrows.grapple.GrappleService;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.RidePath;
import com.grahambartley.notenougharrows.zipline.RideService;
import com.grahambartley.notenougharrows.zipline.RideSession;
import com.grahambartley.notenougharrows.zipline.Span;
import com.grahambartley.notenougharrows.zipline.SpanService;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RideServiceGameTest implements FabricGameTest {
  private static final String FORGET_BATCH = "zipline-ride-forget";
  private static final String BATCH = "zipline-ride";
  private static final Vec3d WEST_GRIP_CENTER = new Vec3d(1.5, 4.5, 3.5);
  private static final Vec3d EAST_GRIP_CENTER = new Vec3d(5.5, 4.5, 3.5);
  private static final Vec3d NEAR_EAST_END = new Vec3d(4.8, 4.5, 3.5);
  private static final Vec3d FAR_FROM_THE_CABLE = new Vec3d(3.5, 4.5, 13.5);
  private static final BlockPos GRAPPLE_ANCHOR = new BlockPos(1, 2, 0);
  private static final int SETTLE_TICK = 3;
  private static final int SHORT_LIFETIME_TICKS = 10;
  private static final int FLOATING_TICKS = 40;
  private static final double CREEP_PER_TICK = 0.06;

  @BeforeBatch(batchId = BATCH)
  public void forgetRidesBeforeBatch(ServerWorld world) {
    RideService.forget();
    SpanService.forget();
    GrappleService.forget();
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void usingTheCableBoardsARideTowardTheFarEnd(TestContext context) {
    final Span span = span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);

    context.assertTrue(board(context, rider), "A rider at an intact span should board it");

    final RideSession ride = RideService.rideOf(context.getWorld(), rider.getUuid());
    context.assertTrue(ride != null, "Boarding should start a ride");
    context.assertEquals(span.lastEnd(), ride.to(), "A rider at the west end rides east");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderAtTheOtherEndRidesTheOtherWay(TestContext context) {
    final Span span = span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, EAST_GRIP_CENTER);

    board(context, rider);

    context.assertEquals(
        span.firstEnd(),
        RideService.rideOf(context.getWorld(), rider.getUuid()).to(),
        "A rider at the east end rides west");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderIsCarriedAlongTheSpanByVelocity(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          context.assertTrue(
              rider.getVelocity().getX() > 0.0, "The rider should be carried east along the span");
          context.assertTrue(
              TraversalTestSupport.nearlyEqual(
                  WEST_GRIP_CENTER.getX(),
                  context.getRelative(rider.getBoundingBox().getCenter()).getX()),
              "The rider is moved by velocity, never repositioned by the server");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderReachingTheFarEndIsLetDownWithoutAFall(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK, () -> TraversalTestSupport.placeCenteredAt(context, rider, NEAR_EAST_END));
    context.runAtTick(
        SETTLE_TICK + 2,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "Arriving should end the ride");
          context.assertTrue(
              GrappleFallGuard.spares(rider.getUuid()),
              "The mod carried the rider there, so it answers for the drop");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderWhoLetsGoFallsUnderVanillasRules(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(RideService.LET_GO_GRACE_TICKS + 1, () -> rider.setSneaking(true));
    context.runAtTick(
        RideService.LET_GO_GRACE_TICKS + 3,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "Sneaking should let go of the span");
          context.assertFalse(
              GrappleFallGuard.spares(rider.getUuid()), "A rider who lets go owns their own fall");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void sneakingAsYouBoardDoesNotDropYouStraightOff(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    rider.setSneaking(true);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) != null,
              "A rider still sneaking from boarding should not let go at once");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderMovedAwayFromTheCableIsNotFlownBackToIt(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK,
        () -> TraversalTestSupport.placeCenteredAt(context, rider, FAR_FROM_THE_CABLE));
    context.runAtTick(
        SETTLE_TICK + 2,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "A rider taken off the cable, by a recall or a teleport, is no longer riding");
          context.assertFalse(
              GrappleFallGuard.spares(rider.getUuid()), "The mod does not pay for that fall");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderWhoSitsInABoatMidRideLetsGo(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);
    final var boat = context.spawnEntity(EntityType.BOAT, new BlockPos(1, 3, 1));

    context.runAtTick(SETTLE_TICK, () -> rider.startRiding(boat, true));
    context.runAtTick(
        SETTLE_TICK + 2,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "A seated rider cannot hold the cable");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void usingTheCableAgainMidRideCarriesOnRatherThanRestarting(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK,
        () -> {
          final RideSession before = RideService.rideOf(context.getWorld(), rider.getUuid());
          TraversalTestSupport.placeCenteredAt(context, rider, EAST_GRIP_CENTER);
          context.assertTrue(board(context, rider), "Using the cable again is accepted");
          context.assertEquals(
              before, RideService.rideOf(context.getWorld(), rider.getUuid()), "Same ride");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void cuttingTheSpanDropsItsRider(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK, () -> context.setBlockState(ZiplineTestSupport.MIDDLE_CABLE, Blocks.AIR));
    context.runAtTick(
        SETTLE_TICK + 2,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "A cut span should end the ride");
          context.assertFalse(
              GrappleFallGuard.spares(rider.getUuid()),
              "Somebody cut the line, so the fall is not the mod's to pay for");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 30)
  public void aSpanThatExpiresUnderItsRiderEndsTheRide(TestContext context) {
    span(context, SHORT_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 3,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "An expired span should end the ride");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aRiderWhoStopsMovingIsLetDownRatherThanHeldForever(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        25,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "A ride that makes no progress should end as obstructed");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aRideThatKeepsCreepingButNeverArrivesRunsOutOfTime(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    TraversalTestSupport.withTraversal(
        traversal ->
            traversal.withZipline(
                traversal.zipline().withRideSpeed(ZiplineArrowConfig.RIDE_SPEED_MAX)),
        () -> board(context, rider));
    final RideSession ride = RideService.rideOf(context.getWorld(), rider.getUuid());
    final int budget = ride.remainingTicks();

    context.runAtEveryTick(
        () -> {
          if (RideService.rideOf(context.getWorld(), rider.getUuid()) != null) {
            rider.setPosition(rider.getPos().add(CREEP_PER_TICK, 0.0, 0.0));
          }
        });
    context.runAtTick(
        budget + 3,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "A ride should end once its tick budget is spent");
          context.assertFalse(
              RidePath.hasArrived(
                  ride.from(), ride.to(), RidePath.gripOf(rider.getBoundingBox().getCenter())),
              "The rider never arrived, so the budget is what ended it");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderWhoDiesIsNoLongerRiding(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    ServerLivingEntityEvents.AFTER_DEATH
        .invoker()
        .afterDeath(rider, context.getWorld().getDamageSources().generic());

    context.assertTrue(
        RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
        "Death ends the ride through the same path a disconnect does");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = FORGET_BATCH, tickLimit = 20)
  public void forgettingEveryRideLeavesNothingRunning(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    RideService.forget();

    context.assertTrue(
        RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
        "A stopped server leaves no ride behind");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void twoRidersOnOneSpanRideIndependently(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity first = riderAt(context, WEST_GRIP_CENTER);
    final ServerPlayerEntity second = riderAt(context, EAST_GRIP_CENTER);
    board(context, first);
    board(context, second);

    context.runAtTick(RideService.LET_GO_GRACE_TICKS + 1, () -> first.setSneaking(true));
    context.runAtTick(
        RideService.LET_GO_GRACE_TICKS + 3,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), first.getUuid()) == null,
              "The first rider let go");
          context.assertTrue(
              RideService.rideOf(context.getWorld(), second.getUuid()) != null,
              "The second rider is still riding");
          context.assertTrue(
              second.getVelocity().getX() < 0.0, "The second rider is still carried west");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aGrappleFiredMidRideTakesOver(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    context.setBlockState(GRAPPLE_ANCHOR, Blocks.STONE);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(
        SETTLE_TICK,
        () ->
            GrappleService.start(
                context.getWorld(), rider, context.getAbsolutePos(GRAPPLE_ANCHOR), null));
    context.runAtTick(
        SETTLE_TICK + 2,
        () -> {
          context.assertTrue(
              RideService.rideOf(context.getWorld(), rider.getUuid()) == null,
              "A grapple should take the rider off the span");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderIsNotCountedAsFloatingWhileTheSpanCarriesThem(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(SETTLE_TICK, () -> rider.networkHandler.floatingTicks = FLOATING_TICKS);
    context.runAtTick(
        SETTLE_TICK + 1,
        () -> {
          context.assertEquals(
              0, rider.networkHandler.floatingTicks, "The anti-flight counter is cleared");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderCarriesNoFallFromTheRideItself(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    board(context, rider);

    context.runAtTick(SETTLE_TICK, () -> rider.fallDistance = 12.0f);
    context.runAtTick(
        SETTLE_TICK + 1,
        () -> {
          context.assertEquals(0.0f, rider.fallDistance, "Hanging from a span is not falling");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCutSpanCannotBeBoarded(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    context.setBlockState(ZiplineTestSupport.MIDDLE_CABLE, Blocks.AIR);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);

    context.assertFalse(board(context, rider), "A cut span should refuse riders");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerInAVehicleCannotBoard(TestContext context) {
    span(context, ZiplineTestSupport.LONG_LIFETIME_TICKS);
    final ServerPlayerEntity rider = riderAt(context, WEST_GRIP_CENTER);
    final var boat = context.spawnEntity(EntityType.BOAT, new BlockPos(1, 3, 3));
    rider.startRiding(boat, true);

    context.assertFalse(board(context, rider), "A seated player should not board");
    context.complete();
  }

  private static Span span(final TestContext context, final int lifetimeTicks) {
    return ZiplineTestSupport.stringAcross(
            context,
            null,
            ZiplineTestSupport.zipline(ZiplineArrowConfig.DEFAULT_MAX_SPAN_BLOCKS, lifetimeTicks))
        .strungSpan()
        .orElseThrow();
  }

  private static ServerPlayerEntity riderAt(final TestContext context, final Vec3d center) {
    final ServerPlayerEntity rider = context.createMockCreativeServerPlayerInWorld();
    TraversalTestSupport.placeCenteredAt(context, rider, center);
    return rider;
  }

  private static boolean board(final TestContext context, final ServerPlayerEntity rider) {
    return RideService.board(
        context.getWorld(), rider, context.getAbsolutePos(ZiplineTestSupport.CABLE.getFirst()));
  }
}
