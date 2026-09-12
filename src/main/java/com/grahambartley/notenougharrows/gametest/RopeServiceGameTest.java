package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.rope.RopeService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RopeServiceGameTest implements FabricGameTest {
  private static final String BATCH = "rope-service";
  private static final String CONFIG_BATCH = "rope-service-config";
  private static final int LONGER_THAN_THE_SHAFT = 32;
  private static final int CONFIGURED_LENGTH = 4;

  @BeforeBatch(batchId = CONFIG_BATCH)
  public void setRopeLengthBeforeBatch(ServerWorld world) {
    ServerConfigHolder.set(configuredRopeLength(CONFIGURED_LENGTH));
  }

  @AfterBatch(batchId = CONFIG_BATCH)
  public void restoreDefaultConfigAfterBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aRopeDropsBeneathABlockWorthAnchoringTo(TestContext context) {
    RopeTestSupport.raiseCeiling(context);

    final List<BlockPos> placed = drop(context, LONGER_THAN_THE_SHAFT);

    context.assertFalse(placed.isEmpty(), "A solid ceiling should hold a rope");
    context.expectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD);
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingHangsFromOpenAir(TestContext context) {
    context.assertTrue(
        drop(context, LONGER_THAN_THE_SHAFT).isEmpty(),
        "Open air is not worth anchoring to, so no rope should drop from it");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingHangsFromABlockNotWorthAnchoringTo(TestContext context) {
    context.setBlockState(RopeTestSupport.CEILING, Blocks.SHORT_GRASS);

    context.assertTrue(
        drop(context, LONGER_THAN_THE_SHAFT).isEmpty(),
        "Grass holds no anchor, so it should hold no rope either");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingHangsFromABlockWithNoUndersideToHangFrom(TestContext context) {
    context.setBlockState(
        RopeTestSupport.CEILING,
        Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP));

    context.assertTrue(
        drop(context, LONGER_THAN_THE_SHAFT).isEmpty(),
        "A top slab is worth anchoring an arrow into but has no underside a rope can hang from,"
            + " so the arrow should embed and no rope should appear");
    context.complete();
  }

  @GameTest(templateName = RopeTestSupport.TEMPLATE, batchId = CONFIG_BATCH, tickLimit = 20)
  public void aRopeIsAsLongAsTheServerConfigSays(TestContext context) {
    RopeTestSupport.raiseCeiling(context);

    final List<BlockPos> placed =
        RopeService.drop(context.getWorld(), context.getAbsolutePos(RopeTestSupport.CEILING), null);

    context.assertEquals(
        placed.size(), CONFIGURED_LENGTH, "Rope segments hung for the configured rope length");
    context.dontExpectBlock(ModBlocks.ROPE, RopeTestSupport.ROPE_HEAD.down(CONFIGURED_LENGTH));
    context.complete();
  }

  private static NotEnoughArrowsConfig configuredRopeLength(final int length) {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();
    final GrappleArrowConfig grapple = defaults.grapple().withRopeLengthBlocks(length);
    return defaults.withGrapple(grapple);
  }

  private static List<BlockPos> drop(final TestContext context, final int maxLength) {
    return RopeService.drop(
        context.getWorld(), context.getAbsolutePos(RopeTestSupport.CEILING), null, maxLength);
  }
}
