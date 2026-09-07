package com.grahambartley.morearrows.block;

import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RopeBlock extends Block {
  public static final MapCodec<RopeBlock> CODEC = createCodec(RopeBlock::new);
  public static final int DECAY_INTERVAL_TICKS = 20 * 60 * 5;

  private static final VoxelShape STRAND = createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

  public RopeBlock(final Settings settings) {
    super(settings);
  }

  public static Settings settings() {
    return Settings.create()
        .noCollision()
        .strength(0.2f)
        .sounds(BlockSoundGroup.WOOL)
        .pistonBehavior(PistonBehavior.DESTROY)
        .dropsNothing();
  }

  @Override
  protected MapCodec<? extends Block> getCodec() {
    return CODEC;
  }

  @Override
  protected VoxelShape getOutlineShape(
      final BlockState state,
      final BlockView world,
      final BlockPos pos,
      final ShapeContext context) {
    return STRAND;
  }

  @Override
  protected boolean canPlaceAt(final BlockState state, final WorldView world, final BlockPos pos) {
    final BlockPos above = pos.up();
    return world.getBlockState(above).isOf(this)
        || sideCoversSmallSquare(world, above, Direction.DOWN);
  }

  @Override
  protected BlockState getStateForNeighborUpdate(
      final BlockState state,
      final Direction direction,
      final BlockState neighborState,
      final WorldAccess world,
      final BlockPos pos,
      final BlockPos neighborPos) {
    if (direction == Direction.UP && !canPlaceAt(state, world, pos)) {
      return Blocks.AIR.getDefaultState();
    }
    return super.getStateForNeighborUpdate(
        state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  protected void onBlockAdded(
      final BlockState state,
      final World world,
      final BlockPos pos,
      final BlockState oldState,
      final boolean notify) {
    world.scheduleBlockTick(pos, this, DECAY_INTERVAL_TICKS);
  }

  @Override
  protected void scheduledTick(
      final BlockState state, final ServerWorld world, final BlockPos pos, final Random random) {
    if (ServerConfigService.get().grapple().ropesDecay()) {
      world.removeBlock(pos, false);
      return;
    }
    world.scheduleBlockTick(pos, this, DECAY_INTERVAL_TICKS);
  }
}
