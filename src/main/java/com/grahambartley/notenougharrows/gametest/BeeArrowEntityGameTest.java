package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.config.BeeArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BeeArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "bee-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowIntoACowReleasesBeesAngryAtTheCow(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.BEE_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          final List<BeeEntity> bees = bees(context);
          context.assertEquals(bees.size(), BeeArrowConfig.DEFAULT_COUNT, "Bees released");
          context.assertTrue(
              bees.stream().allMatch(bee -> bee.getTarget() == cow), "Every bee goes for the cow");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowIntoTheGroundStillReleasesItsBees(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.BEE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(bees(context).size(), BeeArrowConfig.DEFAULT_COUNT, "Bees released");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowReleasesBeesWithNobodyToSpare(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, AgricultureArrows.BEE_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(bees(context).size(), BeeArrowConfig.DEFAULT_COUNT, "Bees released");
          context.complete();
        });
  }

  private static List<BeeEntity> bees(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(BeeEntity.class, context.getTestBox(), BeeEntity::isAlive);
  }
}
