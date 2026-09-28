package com.grahambartley.notenougharrows.block;

import com.grahambartley.notenougharrows.traversal.TrampolineLaunch;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TrampolineBlock extends TranslucentBlock {
  public static final MapCodec<TrampolineBlock> CODEC = createCodec(TrampolineBlock::new);
  private static final float NO_DAMAGE_MULTIPLIER = 0.0f;

  public TrampolineBlock(final Settings settings) {
    super(settings);
  }

  public static Settings settings() {
    return Settings.create()
        .mapColor(MapColor.PALE_GREEN)
        .slipperiness(0.8f)
        .sounds(BlockSoundGroup.SLIME)
        .nonOpaque()
        .pistonBehavior(PistonBehavior.DESTROY)
        .dropsNothing();
  }

  @Override
  protected MapCodec<? extends TranslucentBlock> getCodec() {
    return CODEC;
  }

  @Override
  public void onLandedUpon(
      final World world,
      final BlockState state,
      final BlockPos pos,
      final Entity entity,
      final float fallDistance) {
    entity.handleFallDamage(fallDistance, NO_DAMAGE_MULTIPLIER, world.getDamageSources().fall());
  }

  @Override
  public void onEntityLand(final BlockView world, final Entity entity) {
    if (entity.bypassesLandingEffects() || entity.getWorld().isClient()) {
      super.onEntityLand(world, entity);
      return;
    }
    TrampolineLaunch.launch(entity);
  }
}
