package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.discovery.BeaconService;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BeaconServiceGameTest implements FabricGameTest {
  private static final String BATCH = "beacon";
  private static final BlockPos BASE = new BlockPos(3, 3, 3);
  private static final int OPEN_AIR_ABOVE_THE_FLOOR = 4;
  private static final int LONG_LIFETIME_TICKS = 400;
  private static final int SHORT_LIFETIME_TICKS = 10;

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBeamRisesThroughAllTheOpenAirAboveItsBase(TestContext context) {
    final List<BlockPos> beam = raise(context, BASE, null, LONG_LIFETIME_TICKS);

    context.assertEquals(OPEN_AIR_ABOVE_THE_FLOOR, beam.size(), "Beam blocks raised");
    for (int up = 0; up < OPEN_AIR_ABOVE_THE_FLOOR; up++) {
      context.expectBlock(ModBlocks.BEACON_BEAM, BASE.up(up));
    }
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBeamStopsAtTheFirstBlockAboveIt(TestContext context) {
    context.setBlockState(BASE.up(2), Blocks.STONE);

    context.assertEquals(
        2, raise(context, BASE, null, LONG_LIFETIME_TICKS).size(), "Beam blocks raised");
    context.expectBlock(Blocks.STONE, BASE.up(2));
    context.expectBlock(Blocks.AIR, BASE.up(3));
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBeamNeverReplacesAPlantItCouldHaveGrownThrough(TestContext context) {
    context.setBlockState(BASE, Blocks.SHORT_GRASS);

    context.assertTrue(
        raise(context, BASE, null, LONG_LIFETIME_TICKS).isEmpty(),
        "A beam marks a place without changing it");
    context.expectBlock(Blocks.SHORT_GRASS, BASE);
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBeamNeverRisesThroughWater(TestContext context) {
    context.setBlockState(BASE, Blocks.WATER);

    context.assertTrue(
        raise(context, BASE, null, LONG_LIFETIME_TICKS).isEmpty(), "No beam in water");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aZeroLifetimeRaisesNoBeam(TestContext context) {
    context.assertTrue(raise(context, BASE, null, 0).isEmpty(), "A zero lifetime raises nothing");
    context.expectBlock(Blocks.AIR, BASE);
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aBeamClearsAwayWithoutLeavingAnything(TestContext context) {
    raise(context, BASE, null, SHORT_LIFETIME_TICKS);

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 5,
        () -> {
          for (int up = 0; up < OPEN_AIR_ABOVE_THE_FLOOR; up++) {
            context.expectBlock(Blocks.AIR, BASE.up(up));
          }
          context.assertTrue(
              context
                  .getWorld()
                  .getEntitiesByClass(ItemEntity.class, context.getTestBox(), drop -> true)
                  .isEmpty(),
              "An expired beam leaves nothing to pick up");
          context.complete();
        });
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSecondBeamNeverTakesOverTheFirst(TestContext context) {
    raise(context, BASE, null, LONG_LIFETIME_TICKS);

    context.assertTrue(
        raise(context, BASE, null, LONG_LIFETIME_TICKS).isEmpty(),
        "The column already belongs to the first beam");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBeamNeverRisesWhereTheShooterMayNotBuild(TestContext context) {
    final PlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final List<BlockPos>[] beam = new List[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context, BASE.getX(), () -> beam[0] = raise(context, BASE, shooter, LONG_LIFETIME_TICKS));

    context.assertTrue(beam[0].isEmpty(), "No beam outside the world border");
    context.expectBlock(Blocks.AIR, BASE);
    context.complete();
  }

  private static List<BlockPos> raise(
      final TestContext context,
      final BlockPos relativeBase,
      final PlayerEntity shooter,
      final int lifetimeTicks) {
    return BeaconService.raise(
        context.getWorld(), context.getAbsolutePos(relativeBase), shooter, lifetimeTicks);
  }
}
