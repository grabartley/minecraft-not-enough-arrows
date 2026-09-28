package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.agriculture.HarvestService;
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

public class HarvestArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.0f;

  public HarvestArrowEntity(
      final EntityType<? extends HarvestArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public HarvestArrowEntity(
      final EntityType<? extends HarvestArrowEntity> entityType,
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
    return harvestAround(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()))
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    harvestAround(world, entityHitResult.getEntity().getBlockPos());
    return ArrowImpact.DEFAULT;
  }

  private boolean harvestAround(final ServerWorld world, final BlockPos center) {
    if (HarvestService.harvest(world, center, shootingPlayer().orElse(null)).isEmpty()) {
      return false;
    }
    ModSoundPlayer.playFrom(this, ModSounds.HARVEST_ARROW_REAP, IMPACT_VOLUME, IMPACT_PITCH);
    return true;
  }
}
