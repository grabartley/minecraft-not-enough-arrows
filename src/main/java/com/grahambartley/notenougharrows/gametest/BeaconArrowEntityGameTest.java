package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.BeaconArrowEntity;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BeaconArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "beacon-arrow";
  private static final String NO_LIFETIME_BATCH = "beacon-arrow-no-lifetime";

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aBeaconArrowRaisesABeamInFrontOfTheWallItHitsAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.BEACON_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(ModBlocks.BEACON_BEAM, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(ModBlocks.BEACON_BEAM, FiringRangeSupport.IMPACT_FACE.up());
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, BeaconArrowEntity.class) == null,
              "A beacon arrow that raised a beam is spent");
          context.complete();
        });
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 40)
  public void aBeaconArrowPiercingThroughACreatureRaisesOneBeamAndIsSpent(TestContext context) {
    DiscoveryTestSupport.piercingRange(context);
    final PersistentProjectileEntity arrow =
        DiscoveryTestSupport.firePiercingEast(context, DiscoveryArrows.BEACON_ARROW.item());
    final double[] furthest = {arrow.getX()};
    context.runAtEveryTick(() -> furthest[0] = Math.max(furthest[0], arrow.getX()));

    context.runAtTick(
        30,
        () -> {
          final double wall =
              context.getAbsolute(
                      Vec3d.of(new BlockPos(DiscoveryTestSupport.PIERCING_WALL_X, 0, 0)))
                  .x;
          context.assertTrue(
              furthest[0] > wall - 2,
              "The arrow should have pierced the zombie and reached the wall");
          final List<BlockPos> bases =
              BlockPos.stream(context.getTestBox())
                  .filter(pos -> context.getWorld().getBlockState(pos).isOf(ModBlocks.BEACON_BEAM))
                  .filter(
                      pos ->
                          !context.getWorld().getBlockState(pos.down()).isOf(ModBlocks.BEACON_BEAM))
                  .map(BlockPos::toImmutable)
                  .toList();
          context.assertEquals(1, bases.size(), "Beams raised by one piercing arrow");
          context.assertTrue(
              Math.abs(
                      bases.getFirst().getX()
                          - context.getAbsolutePos(DiscoveryTestSupport.PIERCED).getX())
                  <= 1,
              "The one beam rose where the arrow hit the zombie, not at the wall: " + bases);
          context.assertTrue(arrow.isRemoved(), "A beacon arrow that raised its beam is spent");
          context.complete();
        });
  }

  @BeforeBatch(batchId = NO_LIFETIME_BATCH)
  public void zeroTheBeamLifetimeBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
    DiscoveryTestSupport.useDiscovery(
        discovery -> discovery.withBeacon(discovery.beacon().withLifetimeTicks(0)));
  }

  @AfterBatch(batchId = NO_LIFETIME_BATCH)
  public void restoreDefaultConfigAfterNoLifetimeBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = NO_LIFETIME_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aBeaconArrowWithNoLifetimeEmbedsAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.BEACON_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, FiringRangeSupport.IMPACT_FACE);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, BeaconArrowEntity.class) != null,
              "A beacon arrow that raised nothing stays in the wall to be picked up");
          context.complete();
        });
  }
}
