package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.AgricultureArrows;
import com.grahambartley.notenougharrows.entity.ShearArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class ShearArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "shear-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFromABowShearsTheSheepWithoutHurtingIt(TestContext context) {
    final SheepEntity sheep = sheepAt(context);
    final float health = sheep.getHealth();
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.SHEAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(sheep.isSheared(), "The sheep should be shorn");
          context.assertEquals(sheep.getHealth(), health, "The sheep's health");
          context.assertTrue(shooter.getInventory().count(Items.WHITE_WOOL) > 0, "Wool granted");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDispensedArrowShearsTheSheepAndLeavesTheWoolOnTheGround(TestContext context) {
    final SheepEntity sheep = sheepAt(context);
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, AgricultureArrows.SHEAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(sheep.isSheared(), "The sheep should be shorn");
          context.assertTrue(
              TerrainTestSupport.droppedCount(context, Items.WHITE_WOOL) > 0, "Wool on the ground");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowGlancesOffACowUnhurtAndCanBePickedUp(TestContext context) {
    final CowEntity cow = ControlTestSupport.stillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.SHEAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(cow.getHealth(), health, "The cow's health");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ShearArrowEntity.class) != null
                  || TerrainTestSupport.droppedCount(context, AgricultureArrows.SHEAR_ARROW.item())
                      > 0,
              "An arrow that sheared nothing should still be there to pick up");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowFromABowCarvesThePumpkinFacingTheShooter(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.PUMPKIN);
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.SHEAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.checkBlockState(
              FiringRangeSupport.BACKSTOP,
              state ->
                  state.isOf(Blocks.CARVED_PUMPKIN)
                      && state.get(CarvedPumpkinBlock.FACING) == Direction.WEST,
              () -> "The pumpkin should be carved on the face the arrow struck");
          context.assertEquals(
              shooter.getInventory().count(Items.PUMPKIN_SEEDS), 4, "Seeds granted");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowOnStoneShearsNothingAndStaysRecoverable(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.STONE);
    TerrainArrowTestSupport.fireFromBow(context, AgricultureArrows.SHEAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, ShearArrowEntity.class) != null,
              "An arrow that sheared nothing should stay recoverable");
          context.complete();
        });
  }

  private static SheepEntity sheepAt(final TestContext context) {
    context.setBlockState(TARGET_STAND.down(), Blocks.STONE);
    final SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, TARGET_STAND);
    sheep.setAiDisabled(true);
    sheep.setVelocity(Vec3d.ZERO);
    sheep.setColor(DyeColor.WHITE);
    return sheep;
  }
}
