package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.agriculture.BlossomService;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlossomArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_PITCH = 1.0f;

  public BlossomArrowEntity(
      final EntityType<? extends BlossomArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public BlossomArrowEntity(
      final EntityType<? extends BlossomArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return bloomAround(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()))
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    bloomAround(world, entityHitResult.getEntity().getBlockPos());
    return ArrowImpact.DEFAULT;
  }

  private boolean bloomAround(final ServerWorld world, final BlockPos center) {
    if (BlossomService.bloom(world, center, shootingPlayer().orElse(null)).isEmpty()) {
      return false;
    }
    ModSoundPlayer.playFrom(
        this, ModSounds.BLOSSOM_ARROW_BLOOM, ModSoundPlayer.LANDING_VOLUME, IMPACT_PITCH);
    return true;
  }
}
