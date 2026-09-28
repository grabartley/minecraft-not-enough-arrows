package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.agriculture.MatureCrop;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TorchflowerBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Direction;

public final class MatureCropGameTest implements FabricGameTest {
  private static final String BATCH = "mature-crop";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyRipeFieldCropReplantsAsItsOwnSeedling(TestContext context) {
    for (final CropBlock crop :
        new CropBlock[] {
          (CropBlock) Blocks.WHEAT,
          (CropBlock) Blocks.CARROTS,
          (CropBlock) Blocks.POTATOES,
          (CropBlock) Blocks.BEETROOTS
        }) {
      final BlockState replanted =
          MatureCrop.replanted(crop.withAge(crop.getMaxAge())).orElse(null);
      context.assertTrue(
          replanted != null && replanted.isOf(crop) && crop.getAge(replanted) == 0,
          "A ripe " + crop + " should replant as its own seedling");
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aCropShortOfItsLastStageIsNotRipe(TestContext context) {
    final CropBlock wheat = (CropBlock) Blocks.WHEAT;
    for (int age = 0; age < wheat.getMaxAge(); age++) {
      context.assertTrue(
          MatureCrop.replanted(wheat.withAge(age)).isEmpty(),
          "Wheat at age " + age + " is not ripe");
    }
    context.assertTrue(
        MatureCrop.replanted(Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 2))
            .isEmpty(),
        "Nether wart at age 2 is not ripe");
    context.assertTrue(
        MatureCrop.replanted(Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, 1)).isEmpty(),
        "Cocoa at age 1 is not ripe");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void ripeCocoaKeepsItsFacing(TestContext context) {
    final BlockState ripe =
        Blocks.COCOA
            .getDefaultState()
            .with(CocoaBlock.FACING, Direction.WEST)
            .with(CocoaBlock.AGE, CocoaBlock.MAX_AGE);

    final BlockState replanted = MatureCrop.replanted(ripe).orElseThrow();

    context.assertTrue(replanted.get(CocoaBlock.FACING) == Direction.WEST, "Facing kept");
    context.assertEquals(replanted.get(CocoaBlock.AGE), 0, "Cocoa age");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void whatIsNotACropIsNeverHarvested(TestContext context) {
    context.assertTrue(
        MatureCrop.replanted(
                Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, 3))
            .isEmpty(),
        "A berry bush is picked, not harvested");
    context.assertTrue(
        MatureCrop.replanted(
                Blocks.TORCHFLOWER_CROP.getDefaultState().with(TorchflowerBlock.AGE, 1))
            .isEmpty(),
        "A torchflower crop becomes a flower rather than ripening");
    context.assertTrue(MatureCrop.replanted(Blocks.STONE.getDefaultState()).isEmpty(), "Stone");
    context.assertTrue(MatureCrop.replanted(null).isEmpty(), "Nothing");
    context.complete();
  }
}
