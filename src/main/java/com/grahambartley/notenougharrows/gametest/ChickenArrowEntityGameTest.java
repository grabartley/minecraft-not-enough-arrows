package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.ChickenArrowEntity;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class ChickenArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "chicken-arrow";
  private static final String DISABLED_BATCH = "chicken-arrow-disabled";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchTheChickenOffBeforeBatch(ServerWorld world) {
    ChaosTestSupport.useChaos(chaos -> chaos.withChicken(chaos.chicken().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aChickenArrowReleasesOneChickenInFrontOfTheWallAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.CHICKEN_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          final List<ChickenEntity> chickens = chickens(context);
          context.assertEquals(1, chickens.size(), "Chickens released");
          context.assertTrue(
              chickens.get(0).getHealth() == chickens.get(0).getMaxHealth(),
              "The chicken survived its arrival unhurt");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ChickenArrowEntity.class) == null,
              "A chicken arrow that released its chicken is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aChickenArrowReleasesItsChickenAtACreatureWithoutHurtingIt(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.CHICKEN_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(1, chickens(context).size(), "Chickens released");
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedChickenArrowReleasesAChickenToo(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, ChaosArrows.CHICKEN_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(1, chickens(context).size(), "Chickens released");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledChickenArrowReleasesNothingAndCanBeRecovered(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.CHICKEN_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(chickens(context).isEmpty(), "No chicken should be released");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ChickenArrowEntity.class) != null,
              "A switched off chicken arrow stays in the wall to be picked up");
          context.complete();
        });
  }

  private static List<ChickenEntity> chickens(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(ChickenEntity.class, context.getTestBox(), chicken -> true);
  }
}
