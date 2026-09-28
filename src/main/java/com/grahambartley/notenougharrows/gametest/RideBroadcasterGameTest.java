package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.network.RidePayloads.RideS2CPayload;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.RideBroadcaster;
import com.grahambartley.notenougharrows.zipline.RideService;
import com.grahambartley.notenougharrows.zipline.SpanService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RideBroadcasterGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-ride-broadcast";
  private static final Vec3d GRIP_CENTER = new Vec3d(1.5, 4.5, 3.5);

  @BeforeBatch(batchId = BATCH)
  public void forgetRidesBeforeBatch(ServerWorld world) {
    RideService.forget();
    SpanService.forget();
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerComingIntoViewOfARiderIsToldTheyAreRiding(TestContext context) {
    final ServerPlayerEntity rider = boardedRider(context);

    final RideS2CPayload payload = RideBroadcaster.catchUpFor(rider).orElse(null);

    context.assertTrue(payload != null, "A rider should be announced to a new viewer");
    context.assertEquals(rider.getId(), payload.riderId(), "Rider id");
    context.assertTrue(payload.riding(), "Riding");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerWhoIsNotRidingIsAnnouncedToNobody(TestContext context) {
    final ServerPlayerEntity walker = context.createMockCreativeServerPlayerInWorld();

    context.assertTrue(RideBroadcaster.catchUpFor(walker).isEmpty(), "Not riding");
    context.assertTrue(
        RideBroadcaster.catchUpFor(context.spawnEntity(EntityType.COW, new BlockPos(3, 3, 3)))
            .isEmpty(),
        "Only players ride");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderSeesTheirOwnPose(TestContext context) {
    final ServerPlayerEntity rider = boardedRider(context);

    context.assertTrue(
        RideBroadcaster.audienceOf(rider).contains(rider),
        "The rider's own client draws them in third person too");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRiderWhoLetsGoIsAnnouncedToNobodyNewcomingIntoView(TestContext context) {
    final ServerPlayerEntity rider = boardedRider(context);

    RideService.stopRidingEverywhere(rider.getUuid());

    context.assertTrue(RideBroadcaster.catchUpFor(rider).isEmpty(), "No longer riding");
    context.complete();
  }

  private static ServerPlayerEntity boardedRider(final TestContext context) {
    ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived());
    final ServerPlayerEntity rider = context.createMockCreativeServerPlayerInWorld();
    TraversalTestSupport.placeCenteredAt(context, rider, GRIP_CENTER);
    context.assertTrue(
        RideService.board(
            context.getWorld(), rider, context.getAbsolutePos(ZiplineTestSupport.CABLE.getFirst())),
        "The rider boards");
    return rider;
  }
}
