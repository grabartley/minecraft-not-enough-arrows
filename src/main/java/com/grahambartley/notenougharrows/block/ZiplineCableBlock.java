package com.grahambartley.notenougharrows.block;

import com.grahambartley.notenougharrows.zipline.RideService;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ZiplineCableBlock extends PillarBlock {
  public static final MapCodec<ZiplineCableBlock> CODEC = createCodec(ZiplineCableBlock::new);
  private static final VoxelShape Y_SHAPE = createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
  private static final VoxelShape Z_SHAPE = createCuboidShape(6.5, 6.5, 0.0, 9.5, 9.5, 16.0);
  private static final VoxelShape X_SHAPE = createCuboidShape(0.0, 6.5, 6.5, 16.0, 9.5, 9.5);

  public ZiplineCableBlock(final Settings settings) {
    super(settings);
  }

  public static Settings settings() {
    return Settings.create()
        .noCollision()
        .nonOpaque()
        .strength(0.3f)
        .sounds(BlockSoundGroup.CHAIN)
        .pistonBehavior(PistonBehavior.DESTROY)
        .dropsNothing();
  }

  @Override
  public MapCodec<? extends PillarBlock> getCodec() {
    return CODEC;
  }

  @Override
  protected VoxelShape getOutlineShape(
      final BlockState state,
      final BlockView world,
      final BlockPos pos,
      final ShapeContext context) {
    return switch (state.get(AXIS)) {
      case X -> X_SHAPE;
      case Y -> Y_SHAPE;
      case Z -> Z_SHAPE;
    };
  }

  @Override
  protected ActionResult onUse(
      final BlockState state,
      final World world,
      final BlockPos pos,
      final PlayerEntity player,
      final BlockHitResult hit) {
    if (!(world instanceof ServerWorld serverWorld)) {
      return ActionResult.SUCCESS;
    }
    return player instanceof ServerPlayerEntity rider && RideService.board(serverWorld, rider, pos)
        ? ActionResult.SUCCESS
        : ActionResult.PASS;
  }
}
