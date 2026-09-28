package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.WebArrowConfig;
import com.grahambartley.notenougharrows.terrain.WebService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class WebServiceGameTest implements FabricGameTest {
  private static final String BATCH = "web-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER.up();
  private static final int SHORT_LIFETIME_TICKS = 5;
  private static final int AFTER_EXPIRY_TICK = 15;
  private static final WebArrowConfig RADIUS_ONE =
      new WebArrowConfig(true, 1, WebArrowConfig.DEFAULT_LIFETIME_TICKS);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRadiusOfZeroSpinsOneCobweb(TestContext context) {
    final List<BlockPos> spun =
        spin(context, null, new WebArrowConfig(true, 0, WebArrowConfig.DEFAULT_LIFETIME_TICKS));

    context.assertEquals(spun.size(), 1, "Cobwebs spun");
    context.expectBlock(Blocks.COBWEB, CENTER);
    context.expectBlock(Blocks.AIR, CENTER.east());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aRadiusOfOneSpinsASphereOfSeven(TestContext context) {
    context.assertEquals(spin(context, null, RADIUS_ONE).size(), 7, "Cobwebs spun");
    for (final BlockPos pos :
        List.of(CENTER, CENTER.up(), CENTER.down(), CENTER.east(), CENTER.west())) {
      context.expectBlock(Blocks.COBWEB, pos);
    }
    context.expectBlock(Blocks.AIR, CENTER.east().up());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aWebNeverReplacesABlockThatWasThere(TestContext context) {
    context.setBlockState(CENTER.east(), Blocks.STONE);

    spin(context, null, RADIUS_ONE);

    context.expectBlock(Blocks.STONE, CENTER.east());
    context.expectBlock(Blocks.COBWEB, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT + 10)
  public void aWebClearsAwayWhenItsTimeRunsOut(TestContext context) {
    spin(context, null, new WebArrowConfig(true, 1, SHORT_LIFETIME_TICKS));

    context.runAtTick(
        AFTER_EXPIRY_TICK,
        () -> {
          context.expectBlock(Blocks.AIR, CENTER);
          context.expectBlock(Blocks.AIR, CENTER.up());
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aLifetimeOfZeroSpinsNothing(TestContext context) {
    context.assertTrue(
        spin(context, null, new WebArrowConfig(true, 1, 0)).isEmpty(), "Spun with no lifetime");
    context.expectBlock(Blocks.AIR, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledWebSpinsNothing(TestContext context) {
    context.assertTrue(
        spin(context, null, RADIUS_ONE.withEnabled(false)).isEmpty(), "Spun while disabled");
    context.expectBlock(Blocks.AIR, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotSpinAWebWhereTheyMayNotBuild(TestContext context) {
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                spin(context, shooter, RADIUS_ONE).isEmpty(), "Spun past the world border"));
    context.expectBlock(Blocks.AIR, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedWebStopsAtTheWorldBorder(TestContext context) {
    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                spin(context, null, RADIUS_ONE).isEmpty(), "Spun past the world border"));
    context.expectBlock(Blocks.AIR, CENTER);
    context.complete();
  }

  private static List<BlockPos> spin(
      final TestContext context, final PlayerEntity shooter, final WebArrowConfig web) {
    return WebService.spin(context.getWorld(), context.getAbsolutePos(CENTER), shooter, web);
  }
}
