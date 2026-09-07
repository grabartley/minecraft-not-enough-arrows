package com.grahambartley.morearrows.block;

import com.grahambartley.morearrows.redstone.RedstoneChargeService;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;

public class RedstoneChargeBlock extends Block {
  public static final MapCodec<RedstoneChargeBlock> CODEC = createCodec(RedstoneChargeBlock::new);
  public static final IntProperty POWER = Properties.POWER;

  public RedstoneChargeBlock(final Settings settings) {
    super(settings);
    setDefaultState(getStateManager().getDefaultState().with(POWER, 0));
  }

  public static Settings settings() {
    return Settings.create()
        .noCollision()
        .replaceable()
        .breakInstantly()
        .dropsNothing()
        .pistonBehavior(PistonBehavior.DESTROY);
  }

  public BlockState stateWith(final int power) {
    return getDefaultState().with(POWER, clampPower(power));
  }

  private static int clampPower(final int power) {
    return Math.clamp(power, 0, Properties.POWER.getValues().size() - 1);
  }

  @Override
  protected MapCodec<? extends Block> getCodec() {
    return CODEC;
  }

  @Override
  protected void appendProperties(final StateManager.Builder<Block, BlockState> builder) {
    builder.add(POWER);
  }

  @Override
  protected BlockRenderType getRenderType(final BlockState state) {
    return BlockRenderType.INVISIBLE;
  }

  @Override
  protected boolean emitsRedstonePower(final BlockState state) {
    return true;
  }

  @Override
  protected int getWeakRedstonePower(
      final BlockState state,
      final BlockView world,
      final BlockPos pos,
      final Direction direction) {
    return state.get(POWER);
  }

  @Override
  protected int getStrongRedstonePower(
      final BlockState state,
      final BlockView world,
      final BlockPos pos,
      final Direction direction) {
    return state.get(POWER);
  }

  @Override
  protected void scheduledTick(
      final BlockState state, final ServerWorld world, final BlockPos pos, final Random random) {
    final int remaining = RedstoneChargeService.remainingTicksAt(world, pos);
    if (remaining > 0) {
      world.scheduleBlockTick(pos, this, remaining);
      return;
    }
    world.removeBlock(pos, false);
  }
}
