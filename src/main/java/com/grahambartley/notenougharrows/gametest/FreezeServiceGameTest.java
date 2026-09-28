package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.FreezeArrowConfig;
import com.grahambartley.notenougharrows.fire.FirePatchService;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.terrain.FreezeService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class FreezeServiceGameTest implements FabricGameTest {
  private static final String BATCH = "freeze-service";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;
  private static final BlockPos FIRE_LEVEL = new BlockPos(3, 3, 3);
  private static final int PATCH_LIFETIME_TICKS = 400;
  private static final FreezeArrowConfig RADIUS_ONE = new FreezeArrowConfig(true, 1, 65);

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFreezeTurnsWaterToIce(TestContext context) {
    TerrainTestSupport.fill(context, Blocks.WATER.getDefaultState(), CENTER, CENTER.east());

    freeze(context, CENTER, null, RADIUS_ONE);

    context.expectBlock(Blocks.ICE, CENTER);
    context.expectBlock(Blocks.ICE, CENTER.east());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFreezeTurnsLavaToObsidian(TestContext context) {
    context.setBlockState(CENTER, Blocks.LAVA);

    freeze(context, CENTER, null, RADIUS_ONE);

    context.expectBlock(Blocks.OBSIDIAN, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFreezeNeverReplacesASolidBlock(TestContext context) {
    context.setBlockState(CENTER, Blocks.STONE);
    context.setBlockState(CENTER.east(), Blocks.WATER);

    freeze(context, CENTER, null, RADIUS_ONE);

    context.expectBlock(Blocks.STONE, CENTER);
    context.expectBlock(Blocks.ICE, CENTER.east());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFreezeNeverEncasesACreatureInTheWater(TestContext context) {
    TerrainTestSupport.fill(context, Blocks.WATER.getDefaultState(), CENTER, CENTER.east());
    final CodEntity cod = context.spawnEntity(EntityType.COD, CENTER);
    cod.setAiDisabled(true);

    freeze(context, CENTER, null, RADIUS_ONE);

    context.expectBlock(Blocks.WATER, CENTER);
    context.expectBlock(Blocks.ICE, CENTER.east());
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFreezeRetiresTheModFirePatchItPutsOut(TestContext context) {
    final List<BlockPos> burning =
        FirePatchService.ignite(
            context.getWorld(), context.getAbsolutePos(FIRE_LEVEL), null, 1, PATCH_LIFETIME_TICKS);
    context.assertFalse(burning.isEmpty(), "The fire patch should have been lit");

    freeze(context, FIRE_LEVEL, null, RADIUS_ONE);

    for (final BlockPos pos : burning) {
      context.assertTrue(
          context.getWorld().getBlockState(pos).isAir(), "Fire at " + pos + " should be out");
      context.assertFalse(
          TimedStructureService.holds(context.getWorld(), pos),
          "The patch should no longer claim " + pos);
    }
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFreezeStopsAtItsVolumeCap(TestContext context) {
    TerrainTestSupport.fill(
        context, Blocks.WATER.getDefaultState(), CENTER, CENTER.east(), CENTER.west());

    context.assertEquals(
        freeze(context, CENTER, null, new FreezeArrowConfig(true, 1, 1)).size(),
        1,
        "Blocks frozen");
    context.expectBlock(Blocks.ICE, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDisabledFreezeConvertsNothing(TestContext context) {
    context.setBlockState(CENTER, Blocks.WATER);

    context.assertTrue(
        freeze(context, CENTER, null, RADIUS_ONE.withEnabled(false)).isEmpty(), "Frozen");
    context.expectBlock(Blocks.WATER, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aShooterCannotMakeObsidianWhereTheyMayNotBuild(TestContext context) {
    context.setBlockState(CENTER, Blocks.LAVA);
    final PlayerEntity shooter = context.createMockPlayer(GameMode.SURVIVAL);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                freeze(context, CENTER, shooter, RADIUS_ONE).isEmpty(),
                "Froze past the world border"));
    context.expectBlock(Blocks.LAVA, CENTER);
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDispensedFreezeStopsAtTheWorldBorder(TestContext context) {
    context.setBlockState(CENTER, Blocks.WATER);

    TerrainTestSupport.withTheBorderElsewhere(
        context,
        () ->
            context.assertTrue(
                freeze(context, CENTER, null, RADIUS_ONE).isEmpty(),
                "Froze past the world border"));
    context.expectBlock(Blocks.WATER, CENTER);
    context.complete();
  }

  private static List<BlockPos> freeze(
      final TestContext context,
      final BlockPos relative,
      final PlayerEntity shooter,
      final FreezeArrowConfig freeze) {
    return FreezeService.freeze(
        context.getWorld(), context.getAbsolutePos(relative), shooter, freeze);
  }
}
