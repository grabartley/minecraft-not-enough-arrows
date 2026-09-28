package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class FreezeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "freeze-arrow";
  private static final String SWITCHED_OFF_BATCH = "freeze-arrow-off";
  private static final BlockPos OFF_TARGET_STAND = new BlockPos(5, 3, 3);

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFreezesTheWaterWhereItLands(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE, Blocks.WATER);
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.FREEZE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.ICE, FiringRangeSupport.IMPACT_FACE);
          context.expectBlock(Blocks.STONE, FiringRangeSupport.BACKSTOP);
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowFreezesLavaToObsidian(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    context.setBlockState(FiringRangeSupport.IMPACT_FACE.up(), Blocks.LAVA);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, ModArrows.FREEZE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.OBSIDIAN, FiringRangeSupport.IMPACT_FACE.up());
          context.complete();
        });
  }

  @BeforeBatch(batchId = SWITCHED_OFF_BATCH)
  public void switchTheArrowOffBeforeBatch(ServerWorld world) {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();
    ServerConfigHolder.set(
        defaults.withTerrain(
            defaults.terrain().withFreeze(defaults.terrain().freeze().withEnabled(false))));
  }

  @AfterBatch(batchId = SWITCHED_OFF_BATCH)
  public void restoreDefaultConfigAfterSwitchedOffBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = SWITCHED_OFF_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aSwitchedOffArrowHurtsLikeAPlainArrow(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, OFF_TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ModArrows.FREEZE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(cow.getHealth() < health, "A switched off arrow should hurt the cow");
          context.complete();
        });
  }
}
