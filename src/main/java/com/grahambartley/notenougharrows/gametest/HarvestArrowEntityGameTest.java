package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.entity.HarvestArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class HarvestArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "harvest-arrow";
  private static final BlockPos NEAR_CROP = FiringRangeSupport.IMPACT_FACE;
  private static final BlockPos FAR_CROP = FiringRangeSupport.IMPACT_FACE.west();
  private static final int A_QUIVER = 8;

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFromABowHarvestsAndReplantsTheRipeField(TestContext context) {
    plantField(context, CropBlock.MAX_AGE);
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.HARVEST_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          assertAged(context, 0);
          context.assertEquals(shooter.getInventory().count(Items.WHEAT), 2, "Wheat granted");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowOnAHalfGrownFieldChangesNothingAndStaysRecoverable(TestContext context) {
    plantField(context, 4);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.HARVEST_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          assertAged(context, 4);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, HarvestArrowEntity.class) != null,
              "An arrow that harvested nothing should stay recoverable");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aShooterWithAFullInventoryLosesNoWheat(TestContext context) {
    plantField(context, CropBlock.MAX_AGE);
    final ServerPlayerEntity shooter =
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND);
    shooter.changeGameMode(GameMode.SURVIVAL);
    AgricultureTestSupport.fillInventory(shooter);
    MockPlayerSupport.fireEastFromBow(
        context, shooter, new ItemStack(AgricultureArrows.HARVEST_ARROW.item(), A_QUIVER));
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          assertAged(context, 0);
          context.assertEquals(
              shooter.getInventory().count(Items.WHEAT)
                  + TerrainTestSupport.droppedCount(context, Items.WHEAT),
              2,
              "Every wheat harvested is either held or on the ground");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowHarvestsAndDropsTheWheatAtTheCrop(TestContext context) {
    plantField(context, CropBlock.MAX_AGE);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, AgricultureArrows.HARVEST_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          assertAged(context, 0);
          context.assertEquals(
              TerrainTestSupport.droppedCount(context, Items.WHEAT), 2, "Wheat on the ground");
          context.complete();
        });
  }

  private static void plantField(final TestContext context, final int age) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    AgricultureTestSupport.plantWheat(context, NEAR_CROP, age);
    AgricultureTestSupport.plantWheat(context, FAR_CROP, age);
  }

  private static void assertAged(final TestContext context, final int age) {
    for (final BlockPos crop : new BlockPos[] {NEAR_CROP, FAR_CROP}) {
      context.checkBlockState(
          crop,
          state -> state.isOf(Blocks.WHEAT) && state.get(CropBlock.AGE) == age,
          () -> "The wheat at " + crop + " should be at age " + age);
    }
  }
}
