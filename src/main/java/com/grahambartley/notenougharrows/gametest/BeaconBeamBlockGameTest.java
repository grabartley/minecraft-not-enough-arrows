package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class BeaconBeamBlockGameTest implements FabricGameTest {
  private static final String BATCH = "beacon-beam-block";
  private static final BlockPos BEAM = new BlockPos(3, 3, 3);

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aBeamHasNoCollisionAndCannotBeTargeted(TestContext context) {
    context.setBlockState(BEAM, ModBlocks.BEACON_BEAM);
    final BlockPos pos = context.getAbsolutePos(BEAM);
    final BlockState state = context.getWorld().getBlockState(pos);

    context.assertTrue(
        state.getCollisionShape(context.getWorld(), pos, ShapeContext.absent()).isEmpty(),
        "Nothing bumps into a beam");
    context.assertTrue(
        state.getOutlineShape(context.getWorld(), pos).isEmpty(),
        "A beam cannot be looked at, so it cannot be broken by hand");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aBeamGivesOffNoLight(TestContext context) {
    context.assertEquals(
        0, ModBlocks.BEACON_BEAM.getDefaultState().getLuminance(), "Light level of a beam");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aBeamHasNoItemForm(TestContext context) {
    final Item item = ModBlocks.BEACON_BEAM.asItem();

    context.assertTrue(item == Items.AIR, "A beam has no item");
    context.assertFalse(
        Registries.ITEM.containsId(ModBlocks.BEACON_BEAM_ID), "No item is registered for a beam");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aBlockPlacedIntoABeamReplacesIt(TestContext context) {
    context.assertTrue(
        ModBlocks.BEACON_BEAM.getDefaultState().isReplaceable(),
        "A beam never stops a player building");
    context.complete();
  }
}
