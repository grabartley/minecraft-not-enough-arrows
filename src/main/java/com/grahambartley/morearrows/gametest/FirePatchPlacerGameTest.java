package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.fire.FirePatchPlacer;
import com.grahambartley.morearrows.fire.FirePatchShape;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class FirePatchPlacerGameTest implements FabricGameTest {
  private static final String BATCH = "fire-patch-placement";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos CENTRE = new BlockPos(3, 3, 3);
  private static final BlockPos SURFACE = CENTRE.down();
  private static final int RADIUS = 2;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPatchOnFlatGroundCoversEveryColumnOfItsShape(TestContext context) {
    final List<BlockPos> placed = placePatch(context);

    context.assertEquals(placed.size(), patchColumns().size(), "Fire blocks placed on flat ground");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void everyColumnOfAPatchOnFlatGroundBurnsAtTheCentreHeight(TestContext context) {
    placePatch(context);

    for (final BlockPos column : patchColumns()) {
      context.expectBlock(Blocks.FIRE, column);
    }
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void waterHoldsNoFire(TestContext context) {
    context.setBlockState(CENTRE, Blocks.WATER);

    final List<BlockPos> placed = placePatch(context);

    context.assertFalse(
        placed.contains(context.getAbsolutePos(CENTRE)), "Fire should not be placed in water");
    context.assertEquals(
        placed.size(), patchColumns().size() - 1, "Fire blocks placed around a water column");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSurfaceWithNoSolidTopFaceHoldsNoFire(TestContext context) {
    final BlockPos overSlab = CENTRE.add(2, 0, 0);
    context.setBlockState(overSlab.down(), Blocks.STONE_SLAB);

    final List<BlockPos> placed = placePatch(context);

    context.assertFalse(
        placed.contains(context.getAbsolutePos(overSlab)),
        "A bottom slab has no solid top face, so it should hold no fire");
    context.expectBlock(Blocks.AIR, overSlab);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void fireSettlesOnTopOfARaisedBlockRatherThanInsideIt(TestContext context) {
    final BlockPos raised = CENTRE.add(1, 0, 0);
    context.setBlockState(raised, Blocks.STONE);

    final List<BlockPos> placed = placePatch(context);

    context.assertTrue(
        placed.contains(context.getAbsolutePos(raised.up())),
        "Fire should settle on top of a raised block");
    context.assertFalse(
        placed.contains(context.getAbsolutePos(raised)), "Fire should never replace a solid block");
    context.expectBlock(Blocks.STONE, raised);
    context.expectBlock(Blocks.FIRE, raised.up());
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void soulSoilBurnsWithSoulFireTheWayVanillaDoes(TestContext context) {
    final BlockPos overSoulSoil = CENTRE.add(0, 0, 1);
    context.setBlockState(overSoulSoil.down(), Blocks.SOUL_SOIL);

    placePatch(context);

    context.expectBlock(Blocks.SOUL_FIRE, overSoulSoil);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingRemovesTheFireItPlaced(TestContext context) {
    for (final BlockPos pos : placePatch(context)) {
      context.assertTrue(
          FirePatchPlacer.clear(context.getWorld(), pos), "Clearing a fire block should report it");
    }

    for (final BlockPos column : patchColumns()) {
      context.expectBlock(Blocks.AIR, column);
    }
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void clearingLeavesABlockThatIsNoLongerFireAlone(TestContext context) {
    final BlockPos replaced = CENTRE.add(0, 0, 2);
    context.setBlockState(replaced, Blocks.STONE);

    context.assertFalse(
        FirePatchPlacer.clear(context.getWorld(), context.getAbsolutePos(replaced)),
        "Clearing should refuse a block that is not fire");
    context.expectBlock(Blocks.STONE, replaced);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSolidSurfaceIsNeverReplacedByFire(TestContext context) {
    context.assertFalse(
        FirePatchPlacer.canPlaceAt(context.getWorld(), context.getAbsolutePos(SURFACE), null),
        "Fire should never be placed inside the block it burns on");
    context.complete();
  }

  private static List<BlockPos> placePatch(final TestContext context) {
    return FirePatchPlacer.place(
        context.getWorld(), FirePatchShape.columns(context.getAbsolutePos(CENTRE), RADIUS), null);
  }

  private static List<BlockPos> patchColumns() {
    return FirePatchShape.columns(CENTRE, RADIUS);
  }
}
