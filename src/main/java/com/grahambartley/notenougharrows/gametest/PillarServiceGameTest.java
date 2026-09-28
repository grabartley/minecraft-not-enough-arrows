package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.PillarArrowConfig;
import com.grahambartley.notenougharrows.terrain.PillarService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class PillarServiceGameTest implements FabricGameTest {
  private static final String BATCH = "pillar-service";
  private static final BlockPos STRUCK = new BlockPos(3, 2, 3);
  private static final int SHORT_LIFETIME_TICKS = 5;
  private static final int AFTER_EXPIRY_TICK = 15;
  private static final PillarArrowConfig THREE_HIGH =
      new PillarArrowConfig(true, 3, PillarArrowConfig.DEFAULT_LIFETIME_TICKS);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPillarRaisesDirtToTheConfiguredHeight(TestContext context) {
    final List<BlockPos> raised = raise(context, null, THREE_HIGH);

    context.assertEquals(raised.size(), 3, "Blocks raised");
    context.expectBlock(Blocks.DIRT, STRUCK.up(1));
    context.expectBlock(Blocks.DIRT, STRUCK.up(2));
    context.expectBlock(Blocks.DIRT, STRUCK.up(3));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPillarIsAlwaysDirtWhateverItStruck(TestContext context) {
    context.setBlockState(STRUCK.up(1), Blocks.DIAMOND_BLOCK);
    final BlockPos diamondBase = STRUCK.up(1);

    PillarService.raise(context.getWorld(), context.getAbsolutePos(diamondBase), null, THREE_HIGH);

    context.expectBlock(Blocks.DIRT, diamondBase.up(1));
    context.expectBlock(Blocks.DIAMOND_BLOCK, diamondBase);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPillarStopsAtTheFirstBlockInTheWay(TestContext context) {
    context.setBlockState(STRUCK.up(2), Blocks.STONE);

    final List<BlockPos> raised = raise(context, null, THREE_HIGH);

    context.assertEquals(raised.size(), 1, "Blocks raised beneath the obstruction");
    context.expectBlock(Blocks.DIRT, STRUCK.up(1));
    context.expectBlock(Blocks.STONE, STRUCK.up(2));
    context.expectBlock(Blocks.AIR, STRUCK.up(3));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aPillarNeverBuriesAMobStandingInItsWay(TestContext context) {
    final CowEntity cow = context.spawnEntity(EntityType.COW, STRUCK.up(2));
    cow.setAiDisabled(true);
    cow.setNoGravity(true);

    final List<BlockPos> raised = raise(context, null, THREE_HIGH);

    context.assertEquals(raised.size(), 1, "Blocks raised beneath the cow");
    context.expectBlock(Blocks.AIR, STRUCK.up(2));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT + 10)
  public void aPillarSinksAwayWhenItsTimeRunsOut(TestContext context) {
    raise(context, null, new PillarArrowConfig(true, 2, SHORT_LIFETIME_TICKS));

    context.runAtTick(
        AFTER_EXPIRY_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, STRUCK.up(1));
          context.expectBlock(Blocks.AIR, STRUCK.up(2));
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aLifetimeOfZeroRaisesNothing(TestContext context) {
    context.assertTrue(
        raise(context, null, new PillarArrowConfig(true, 3, 0)).isEmpty(),
        "A zero lifetime should raise nothing");
    context.expectBlock(Blocks.AIR, STRUCK.up(1));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledPillarRaisesNothing(TestContext context) {
    context.assertTrue(
        raise(context, null, THREE_HIGH.withEnabled(false)).isEmpty(),
        "A disabled pillar should raise nothing");
    context.expectBlock(Blocks.AIR, STRUCK.up(1));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotRaiseAPillarWhereTheyMayNotBuild(TestContext context) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                raise(context, shooter, THREE_HIGH).isEmpty(), "Raised past the world border"));
    context.expectBlock(Blocks.AIR, STRUCK.up(1));
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedPillarStopsAtTheWorldBorder(TestContext context) {
    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                raise(context, null, THREE_HIGH).isEmpty(), "Raised past the world border"));
    context.expectBlock(Blocks.AIR, STRUCK.up(1));
    context.complete();
  }

  private static List<BlockPos> raise(
      final TestContext context, final PlayerEntity shooter, final PillarArrowConfig pillar) {
    return PillarService.raise(context.getWorld(), context.getAbsolutePos(STRUCK), shooter, pillar);
  }
}
