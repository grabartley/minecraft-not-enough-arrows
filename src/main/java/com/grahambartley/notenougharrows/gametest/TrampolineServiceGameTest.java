package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.TrampolineArrowConfig;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.traversal.TrampolinePad;
import com.grahambartley.notenougharrows.traversal.TrampolineService;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class TrampolineServiceGameTest implements FabricGameTest {
  private static final String BATCH = "trampoline";
  private static final BlockPos CENTER = new BlockPos(3, 3, 3);
  private static final int LONG_LIFETIME_TICKS = 400;
  private static final int SHORT_LIFETIME_TICKS = 10;

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPadIsAThreeByThreeSquareOfTrampoline(TestContext context) {
    final List<BlockPos> pad = place(context, null, LONG_LIFETIME_TICKS);

    context.assertEquals(9, pad.size(), "Pad blocks");
    TrampolinePad.around(CENTER).forEach(pos -> context.expectBlock(ModBlocks.TRAMPOLINE, pos));
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPadGrowsAroundWhateverIsAlreadyThere(TestContext context) {
    context.setBlockState(CENTER.west(), Blocks.STONE);

    context.assertEquals(8, place(context, null, LONG_LIFETIME_TICKS).size(), "Pad blocks");
    context.expectBlock(Blocks.STONE, CENTER.west());
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPadNeverEntombsSomethingStandingWhereItWouldGo(TestContext context) {
    context.spawnEntity(EntityType.ARMOR_STAND, CENTER.east());

    context.assertEquals(8, place(context, null, LONG_LIFETIME_TICKS).size(), "Pad blocks");
    context.expectBlock(Blocks.AIR, CENTER.east());
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPadMeltsAwayWithoutLeavingAnythingBehind(TestContext context) {
    place(context, null, SHORT_LIFETIME_TICKS);

    context.runAtTick(
        SHORT_LIFETIME_TICKS + 3,
        () -> {
          TrampolinePad.around(CENTER).forEach(pos -> context.expectBlock(Blocks.AIR, pos));
          context.assertTrue(
              context
                  .getWorld()
                  .getEntitiesByClass(ItemEntity.class, context.getTestBox(), drop -> true)
                  .isEmpty(),
              "A pad leaves nothing to pick up");
          context.complete();
        });
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPadIsTruncatedAtAProtectionBoundary(TestContext context) {
    final PlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final List<BlockPos>[] pad = new List[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context, CENTER.getX() + 1, () -> pad[0] = place(context, shooter, LONG_LIFETIME_TICKS));

    context.assertTrue(pad[0].size() < 9, "Part of the pad lies beyond the boundary");
    context.expectBlock(Blocks.AIR, CENTER.east());
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPadWithNoLifetimeIsNeverPlaced(TestContext context) {
    context.assertTrue(place(context, null, 0).isEmpty(), "Nothing placed");
    context.complete();
  }

  private static List<BlockPos> place(
      final TestContext context, final PlayerEntity shooter, final int lifetime) {
    return TrampolineService.place(
        context.getWorld(),
        context.getAbsolutePos(CENTER),
        shooter,
        new TrampolineArrowConfig(TrampolineArrowConfig.DEFAULT_STRENGTH, lifetime));
  }
}
