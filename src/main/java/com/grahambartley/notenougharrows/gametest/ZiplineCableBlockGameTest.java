package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.RideService;
import com.grahambartley.notenougharrows.zipline.SpanService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ZiplineCableBlockGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-cable";
  private static final Vec3d GRIP_CENTER = new Vec3d(1.5, 4.5, 3.5);
  private static final BlockPos STRAY_CABLE = new BlockPos(3, 3, 5);

  @BeforeBatch(batchId = BATCH)
  public void forgetZiplinesBeforeBatch(ServerWorld world) {
    RideService.forget();
    SpanService.forget();
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void usingACableOfAStrungSpanRidesIt(TestContext context) {
    ZiplineTestSupport.stringAcross(context, null, ZiplineTestSupport.longLived());
    final ServerPlayerEntity rider = context.createMockCreativeServerPlayerInWorld();
    TraversalTestSupport.placeCenteredAt(context, rider, GRIP_CENTER);

    context.useBlock(ZiplineTestSupport.CABLE.getFirst(), rider);

    context.assertTrue(
        RideService.rideOf(context.getWorld(), rider.getUuid()) != null,
        "Using the cable should board the span");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void usingACableThatBelongsToNoSpanDoesNothing(TestContext context) {
    context.setBlockState(STRAY_CABLE, ModBlocks.ZIPLINE_CABLE);
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();

    context.useBlock(STRAY_CABLE, player);

    context.assertTrue(
        RideService.rideOf(context.getWorld(), player.getUuid()) == null,
        "A cable no span owns is not a ride");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aCableCannotBeWalkedInto(TestContext context) {
    context.setBlockState(STRAY_CABLE, ModBlocks.ZIPLINE_CABLE);

    context.assertTrue(
        context
            .getBlockState(STRAY_CABLE)
            .getCollisionShape(context.getWorld(), context.getAbsolutePos(STRAY_CABLE))
            .isEmpty(),
        "A cable blocks nobody, so a rider hangs from it and a walker passes under it");
    context.complete();
  }
}
