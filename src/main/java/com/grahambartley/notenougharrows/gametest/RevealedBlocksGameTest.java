package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ProspectorArrowConfig;
import com.grahambartley.notenougharrows.reveal.RevealedBlocks;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class RevealedBlocksGameTest implements FabricGameTest {
  private static final String BATCH = "revealed-blocks";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void revealsExactlyTheListedBlocks(TestContext context) {
    final Predicate<BlockState> reveals =
        RevealedBlocks.of(List.of("minecraft:diamond_ore", "minecraft:gold_block"));

    context.assertTrue(reveals.test(Blocks.DIAMOND_ORE.getDefaultState()), "Diamond ore listed");
    context.assertTrue(reveals.test(Blocks.GOLD_BLOCK.getDefaultState()), "Gold block listed");
    context.assertFalse(reveals.test(Blocks.STONE.getDefaultState()), "Stone is not listed");
    context.assertFalse(
        reveals.test(Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState()),
        "Only what an operator lists is revealed, not its relatives");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void anUnknownOrMalformedIdRevealsNothing(TestContext context) {
    final Predicate<BlockState> reveals =
        RevealedBlocks.of(List.of("not-enough-arrows:no_such_block", "Not An Id!"));

    context.assertFalse(reveals.test(Blocks.AIR.getDefaultState()), "Air is never revealed");
    context.assertFalse(reveals.test(Blocks.STONE.getDefaultState()), "Nothing is revealed");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void anEmptyListRevealsNothing(TestContext context) {
    context.assertFalse(
        RevealedBlocks.of(List.of()).test(Blocks.DIAMOND_ORE.getDefaultState()),
        "An operator who cleared the list reveals nothing");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theDefaultListRevealsEveryVanillaOre(TestContext context) {
    final Predicate<BlockState> reveals = RevealedBlocks.of(ProspectorArrowConfig.DEFAULT_BLOCKS);

    for (final BlockState ore :
        List.of(
            Blocks.COAL_ORE.getDefaultState(),
            Blocks.DEEPSLATE_IRON_ORE.getDefaultState(),
            Blocks.NETHER_QUARTZ_ORE.getDefaultState(),
            Blocks.ANCIENT_DEBRIS.getDefaultState())) {
      context.assertTrue(reveals.test(ore), ore.getBlock() + " should be revealed by default");
    }
    context.complete();
  }
}
