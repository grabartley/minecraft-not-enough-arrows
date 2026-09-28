package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.TraversalArrows;
import com.grahambartley.notenougharrows.entity.ZiplineArrowEntity;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.zipline.PendingAnchorService;
import com.grahambartley.notenougharrows.zipline.SpanService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ZiplineArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "zipline-arrow";
  private static final BlockPos NORTH_POST = new BlockPos(6, 3, 1);
  private static final BlockPos SOUTH_POST = new BlockPos(6, 3, 5);
  private static final Vec3d NORTH_STAND = new Vec3d(1.5, 2.0, 1.5);
  private static final Vec3d SOUTH_STAND = new Vec3d(1.5, 2.0, 5.5);
  private static final Vec3d ASIDE = new Vec3d(0.5, 2.0, 3.5);
  private static final int SECOND_SHOT_TICK = 20;
  private static final int STRUNG_TICK = 40;

  @BeforeBatch(batchId = BATCH)
  public void forgetZiplinesBeforeBatch(ServerWorld world) {
    PendingAnchorService.forget();
    SpanService.forget();
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void twoBowShotsStringAZiplineBetweenTheBlocksTheyHit(TestContext context) {
    TraversalTestSupport.stone(context, NORTH_POST, SOUTH_POST);
    final ServerPlayerEntity shooter = TraversalTestSupport.playerAt(context, NORTH_STAND);
    MockPlayerSupport.fireEastFromBow(context, shooter, TraversalArrows.ZIPLINE_ARROW.item());
    MockPlayerSupport.moveTo(context, shooter, ASIDE);

    context.runAtTick(
        SECOND_SHOT_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ZiplineArrowEntity.class) != null,
              "The first arrow should stay in its block as the pending anchor");
          context.assertTrue(
              PendingAnchorService.pendingFor(shooter.getUuid()).isPresent(),
              "The first shot should set a pending anchor");
          MockPlayerSupport.moveTo(context, shooter, SOUTH_STAND);
          MockPlayerSupport.fireEastFromBow(context, shooter, TraversalArrows.ZIPLINE_ARROW.item());
          MockPlayerSupport.moveTo(context, shooter, ASIDE);
        });
    context.runAtTick(
        STRUNG_TICK,
        () -> {
          for (int z = 2; z <= 4; z++) {
            context.expectBlock(ModBlocks.ZIPLINE_CABLE, new BlockPos(6, 3, z));
          }
          context.assertTrue(
              FiringRangeSupport.arrowWasSpent(context), "Both arrows went into the span");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aDispensedZiplineArrowAnchorsNothingAndIsRecoverable(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, TraversalArrows.ZIPLINE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ZiplineArrowEntity.class) != null,
              "A dispensed zipline arrow embeds like any arrow");
          context.complete();
        });
  }
}
