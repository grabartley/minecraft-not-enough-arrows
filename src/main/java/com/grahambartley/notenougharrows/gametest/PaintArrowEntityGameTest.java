package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.entity.PaintArrowEntity;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import com.grahambartley.notenougharrows.tint.ArrowChoice;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class PaintArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "paint-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int A_QUIVER = 8;

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aRedArrowPaintsWhiteWoolRed(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.WHITE_WOOL);
    TerrainArrowTestSupport.fireFromBow(context, redQuiver());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.RED_WOOL, FiringRangeSupport.BACKSTOP);
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowOnConcreteChangesNothingAndStaysRecoverable(TestContext context) {
    context.setBlockState(FiringRangeSupport.BACKSTOP, Blocks.WHITE_CONCRETE);
    TerrainArrowTestSupport.fireFromBow(context, redQuiver());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.expectBlock(Blocks.WHITE_CONCRETE, FiringRangeSupport.BACKSTOP);
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, PaintArrowEntity.class) != null,
              "An arrow that painted nothing should stay recoverable");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aRedArrowPaintsASheepRedWithoutHurtingIt(TestContext context) {
    context.setBlockState(TARGET_STAND.down(), Blocks.STONE);
    final SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, TARGET_STAND);
    sheep.setAiDisabled(true);
    sheep.setVelocity(Vec3d.ZERO);
    sheep.setColor(DyeColor.WHITE);
    final float health = sheep.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, redQuiver());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertEquals(sheep.getColor(), DyeColor.RED, "The sheep's colour");
          context.assertEquals(sheep.getHealth(), health, "The sheep's health");
          context.assertTrue(FiringRangeSupport.arrowWasSpent(context), "The arrow is spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = TerrainArrowTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void anArrowGlancesOffAMobThatIsNotASheepAndStaysRecoverable(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, redQuiver());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(cow.getHealth(), health, "The cow's health");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, PaintArrowEntity.class) != null,
              "An arrow that painted nothing should stay recoverable");
          context.complete();
        });
  }

  private static ItemStack redQuiver() {
    final TintedArrowItem paint = (TintedArrowItem) ModArrows.PAINT_ARROW.item();
    final ItemStack quiver = paint.stackOf(paint.palette().resolve(new ArrowChoice("red")));
    quiver.setCount(A_QUIVER);
    return quiver;
  }
}
