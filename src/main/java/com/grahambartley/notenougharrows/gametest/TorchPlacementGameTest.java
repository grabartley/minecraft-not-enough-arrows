package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.discovery.TorchPlacement;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

public final class TorchPlacementGameTest implements FabricGameTest {
  private static final String BATCH = "torch-placement";
  private static final BlockPos WALL = new BlockPos(3, 4, 3);
  private static final BlockPos FLOOR = new BlockPos(3, 2, 3);

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aTorchHangsOnTheSideOfAWallFacingAwayFromIt(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);

    final Optional<BlockPos> placed = place(context, WALL, Direction.WEST, null);

    context.assertTrue(placed.isPresent(), "A torch should hang on the west face");
    context.checkBlockState(
        WALL.west(),
        state ->
            state.isOf(Blocks.WALL_TORCH) && state.get(WallTorchBlock.FACING) == Direction.WEST,
        () -> "A wall torch facing out of the struck face");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aTorchStandsOnTopOfAFloor(TestContext context) {
    context.assertTrue(
        place(context, FLOOR, Direction.UP, null).isPresent(), "A torch should stand on the floor");
    context.expectBlock(Blocks.TORCH, FLOOR.up());
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aTorchIsNeverHungFromACeiling(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);

    context.assertTrue(
        place(context, WALL, Direction.DOWN, null).isEmpty(), "No torch goes on a ceiling");
    context.expectBlock(Blocks.AIR, WALL.down());
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aTorchIsNeverPlacedInWater(TestContext context) {
    context.setBlockState(WALL, Blocks.STONE);
    context.setBlockState(WALL.west(), Blocks.WATER);

    context.assertTrue(
        place(context, WALL, Direction.WEST, null).isEmpty(), "No torch goes into water");
    context.expectBlock(Blocks.WATER, WALL.west());
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aTorchNeverReplacesWhatIsAlreadyThere(TestContext context) {
    context.setBlockState(FLOOR.up(), Blocks.SHORT_GRASS);

    context.assertTrue(
        place(context, FLOOR, Direction.UP, null).isEmpty(), "No torch replaces the grass");
    context.expectBlock(Blocks.SHORT_GRASS, FLOOR.up());
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anAdventureModeShooterPlacesNoTorch(TestContext context) {
    final PlayerEntity adventurer = context.createMockPlayer(GameMode.ADVENTURE);
    adventurer.getAbilities().allowModifyWorld = false;

    context.assertTrue(
        place(context, FLOOR, Direction.UP, adventurer).isEmpty(),
        "A shooter who could not place a torch by hand places none by arrow");
    context.expectBlock(Blocks.AIR, FLOOR.up());
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aTorchIsNeverPlacedWhereTheShooterMayNotBuild(TestContext context) {
    final PlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final boolean[] placed = new boolean[1];

    TraversalTestSupport.withTheBorderEastEdgeAt(
        context,
        FLOOR.getX(),
        () -> placed[0] = place(context, FLOOR, Direction.UP, shooter).isPresent());

    context.assertFalse(placed[0], "No torch outside the world border");
    context.expectBlock(Blocks.AIR, FLOOR.up());
    context.complete();
  }

  private static Optional<BlockPos> place(
      final TestContext context,
      final BlockPos relative,
      final Direction face,
      final PlayerEntity shooter) {
    return TorchPlacement.place(
        context.getWorld(), context.getAbsolutePos(relative), face, shooter);
  }
}
