package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.anchor.AnchorSite;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class AnchorSiteGameTest implements FabricGameTest {
  private static final String BATCH = "anchor-site";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final BlockPos TARGET = new BlockPos(3, 3, 3);
  private static final BlockPos SUPPORT = TARGET.down();

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aFullBlockIsSolidEnoughToAnchorTo(TestContext context) {
    assertSuitability(context, Blocks.STONE, true, "A full block should hold an anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSlabIsSolidEnoughToAnchorTo(TestContext context) {
    assertSuitability(context, Blocks.STONE_SLAB, true, "A slab should hold an anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void glassIsSolidEnoughToAnchorTo(TestContext context) {
    assertSuitability(context, Blocks.GLASS, true, "Glass should hold an anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void thereIsNothingToAnchorToInOpenAir(TestContext context) {
    assertSuitability(context, Blocks.AIR, false, "Open air should hold no anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void waterIsNotSolidEnoughToAnchorTo(TestContext context) {
    assertSuitability(context, Blocks.WATER, false, "Water should hold no anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void grassIsNotSolidEnoughToAnchorTo(TestContext context) {
    assertSuitability(context, Blocks.SHORT_GRASS, false, "Grass should hold no anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aTorchIsNotSolidEnoughToAnchorTo(TestContext context) {
    assertSuitability(context, Blocks.TORCH, false, "A torch should hold no anchor");
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPositionOutsideTheWorldHoldsNoAnchor(TestContext context) {
    context.assertFalse(
        AnchorSite.isSuitable(context.getWorld(), new BlockPos(0, Integer.MAX_VALUE, 0)),
        "A position outside the build limit should hold no anchor");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSiteReportsTheBlockStandingThere(TestContext context) {
    context.setBlockState(TARGET, Blocks.STONE);

    context.assertTrue(
        Registries.BLOCK.getId(Blocks.STONE).equals(blockIdAt(context)),
        "The site should report the block standing there");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSiteWithNothingStandingThereReportsAir(TestContext context) {
    context.assertTrue(
        Registries.BLOCK.getId(Blocks.AIR).equals(blockIdAt(context)),
        "An empty site should report air rather than nothing at all");
    context.complete();
  }

  private static void assertSuitability(
      final TestContext context, final Block block, final boolean suitable, final String message) {
    context.setBlockState(SUPPORT, Blocks.STONE);
    context.setBlockState(TARGET, block);

    context.assertEquals(
        AnchorSite.isSuitable(context.getWorld(), context.getAbsolutePos(TARGET)),
        suitable,
        message);
    context.complete();
  }

  private static Identifier blockIdAt(final TestContext context) {
    return AnchorSite.blockIdAt(context.getWorld(), context.getAbsolutePos(TARGET));
  }
}
