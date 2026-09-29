package com.grahambartley.notenougharrows.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BeaconBeamBlock extends Block {
  public static final MapCodec<BeaconBeamBlock> CODEC = createCodec(BeaconBeamBlock::new);

  public BeaconBeamBlock(final Settings settings) {
    super(settings);
  }

  public static Settings settings() {
    return Settings.create()
        .noCollision()
        .nonOpaque()
        .replaceable()
        .breakInstantly()
        .dropsNothing()
        .luminance(state -> 0)
        .emissiveLighting((state, world, pos) -> true)
        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
        .pistonBehavior(PistonBehavior.DESTROY);
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
    return VoxelShapes.empty();
  }
}
