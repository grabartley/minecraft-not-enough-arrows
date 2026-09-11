package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.gravity.FallingBlocks;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;

public final class FallingBlocksGameTest implements FabricGameTest {
  private static final String BATCH = "falling-blocks";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos CANDIDATE = new BlockPos(3, 4, 3);
  private static final int TICK_LIMIT = 20;
  private static final int FAR_ABOVE_THE_BUILD_LIMIT = 40_000;

  private record Candidate(String name, BlockState state, boolean canFall) {
    static Candidate of(final String name, final Block block, final boolean canFall) {
      return new Candidate(name, block.getDefaultState(), canFall);
    }
  }

  private static List<Candidate> candidates() {
    return List.of(
        Candidate.of("solidbreakableblock", Blocks.STONE, true),
        Candidate.of("openair", Blocks.AIR, false),
        Candidate.of("fluid", Blocks.WATER, false),
        new Candidate(
            "blockholdingwater",
            Blocks.OAK_FENCE.getDefaultState().with(Properties.WATERLOGGED, true),
            false),
        Candidate.of("replaceableplant", Blocks.SHORT_GRASS, false),
        Candidate.of("blockwithnocollisionshape", Blocks.TORCH, false),
        Candidate.of("unbreakableblock", Blocks.BEDROCK, false),
        Candidate.of("blockholdingitems", Blocks.CHEST, false),
        Candidate.of("shulkerbox", Blocks.SHULKER_BOX, false),
        Candidate.of("blockentitythatholdsnothing", Blocks.BELL, false),
        Candidate.of("blockentitycarryingplayertext", Blocks.OAK_SIGN, false));
  }

  @CustomTestProvider
  public Collection<TestFunction> onlyWhatAPlayerCouldHaveBrokenMayFall() {
    return candidates().stream()
        .map(
            candidate ->
                new TestFunction(
                    BATCH,
                    "notenougharrows.mayfall." + candidate.name(),
                    TEMPLATE,
                    TICK_LIMIT,
                    0L,
                    true,
                    context -> assertCanFall(context, candidate)))
        .toList();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aPositionOutsideTheBuildLimitIsNeverDropped(TestContext context) {
    final BlockPos aboveTheWorld =
        context.getAbsolutePos(CANDIDATE).withY(FAR_ABOVE_THE_BUILD_LIMIT);

    context.assertFalse(
        FallingBlocks.canFall(
            context.getWorld(), aboveTheWorld, Blocks.STONE.getDefaultState(), null),
        "A position outside the build limit should never be dropped");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aDroppedBlockLeavesItsPositionEmptyAndTakesItsStateWithIt(TestContext context) {
    context.setBlockState(CANDIDATE, Blocks.STONE);
    final BlockPos target = context.getAbsolutePos(CANDIDATE);

    final FallingBlockEntity falling =
        FallingBlocks.drop(context.getWorld(), target, context.getWorld().getBlockState(target));

    context.assertTrue(
        falling.getBlockState().isOf(Blocks.STONE),
        "The falling block should carry the state it replaced");
    context.expectBlock(Blocks.AIR, CANDIDATE);
    context.expectEntity(EntityType.FALLING_BLOCK);
    context.complete();
  }

  private static void assertCanFall(final TestContext context, final Candidate candidate) {
    context.setBlockState(CANDIDATE, candidate.state());
    final ServerWorld world = context.getWorld();
    final BlockPos target = context.getAbsolutePos(CANDIDATE);

    context.assertTrue(
        FallingBlocks.canFall(world, target, world.getBlockState(target), null)
            == candidate.canFall(),
        "Whether "
            + candidate.name()
            + " may be dropped should be "
            + candidate.canFall()
            + ", state was "
            + world.getBlockState(target));
    context.complete();
  }
}
