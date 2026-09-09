package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.countdown.CountdownBroadcaster;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.network.CountdownPayloads.CountdownS2CPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class CountdownBroadcasterGameTest implements FabricGameTest {
  private static final String BATCH = "countdown-broadcaster";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos CARRIER_POS = new BlockPos(3, 2, 3);
  private static final int LONG_FUSE_TICKS = 200;

  @BeforeBatch(batchId = BATCH)
  public void clearCountdownState(ServerWorld world) {
    FuseService.forget();
    CountdownBroadcaster.forget();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aBurningPayloadRoundTripsAcrossTheWire(TestContext context) {
    final CountdownS2CPayload original = new CountdownS2CPayload(7, 200, 143);
    final CountdownS2CPayload received = roundTrip(context, original);

    context.assertTrue(
        original.equals(received),
        "A burning countdown should survive a wire round trip, but was " + received);
    context.assertTrue(received.isBurning(), "A countdown with time left should read as burning");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anEndedPayloadCarriesNothingLeftToDraw(TestContext context) {
    final CountdownS2CPayload received = roundTrip(context, CountdownS2CPayload.ended(7));

    context.assertEquals(7, received.carrierId(), "An ended countdown should name its carrier");
    context.assertFalse(received.isBurning(), "An ended countdown should not read as burning");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFuseLitOnACarrierIsAnnounced(TestContext context) {
    final PigEntity carrier = carrierIn(context);

    context.runAtTick(2, () -> FuseService.light(context.getWorld(), carrier, LONG_FUSE_TICKS));
    context.runAtTick(
        6,
        () -> {
          context.assertTrue(
              CountdownBroadcaster.isAnnouncing(context.getWorld(), carrier.getUuid()),
              "A lit fuse should be announced to the players watching its carrier");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anExtinguishedFuseStopsBeingAnnounced(TestContext context) {
    final PigEntity carrier = carrierIn(context);

    context.runAtTick(2, () -> FuseService.light(context.getWorld(), carrier, LONG_FUSE_TICKS));
    context.runAtTick(6, () -> FuseService.extinguish(context.getWorld(), carrier.getUuid()));
    context.runAtTick(
        12,
        () -> {
          context.assertFalse(
              CountdownBroadcaster.isAnnouncing(context.getWorld(), carrier.getUuid()),
              "A fuse that stopped burning should stop being announced");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aCarrierThatLeavesStopsBeingAnnounced(TestContext context) {
    final PigEntity carrier = carrierIn(context);

    context.runAtTick(2, () -> FuseService.light(context.getWorld(), carrier, LONG_FUSE_TICKS));
    context.runAtTick(6, carrier::discard);
    context.runAtTick(
        12,
        () -> {
          context.assertFalse(
              CountdownBroadcaster.isAnnouncing(context.getWorld(), carrier.getUuid()),
              "A carrier that is gone should stop being announced");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFuseSurvivesTicksOfEveryOtherWorld(TestContext context) {
    final PigEntity carrier = carrierIn(context);

    context.runAtTick(2, () -> FuseService.light(context.getWorld(), carrier, LONG_FUSE_TICKS));
    context.runAtTick(
        40,
        () -> {
          context.assertTrue(
              CountdownBroadcaster.isAnnouncing(context.getWorld(), carrier.getUuid()),
              "A fuse should stay announced while it burns, however many other worlds tick"
                  + " alongside the one it is burning in");
          context.complete();
        });
  }

  private static PigEntity carrierIn(final TestContext context) {
    final PigEntity carrier = context.spawnEntity(EntityType.PIG, CARRIER_POS);
    carrier.setAiDisabled(true);
    return carrier;
  }

  private static CountdownS2CPayload roundTrip(
      final TestContext context, final CountdownS2CPayload payload) {
    final RegistryByteBuf buf =
        new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
    CountdownS2CPayload.CODEC.encode(buf, payload);
    return CountdownS2CPayload.CODEC.decode(buf);
  }
}
