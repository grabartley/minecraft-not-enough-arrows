package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.agriculture.TillService;
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

public class TillArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_PITCH = 1.0f;

  public TillArrowEntity(
      final EntityType<? extends TillArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public TillArrowEntity(
      final EntityType<? extends TillArrowEntity> entityType,
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
    return tillAround(world, blockHitResult.getBlockPos())
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    tillAround(world, entityHitResult.getEntity().getBlockPos().down());
    return ArrowImpact.DEFAULT;
  }

  private boolean tillAround(final ServerWorld world, final BlockPos center) {
    if (TillService.till(world, center, shootingPlayer().orElse(null)).isEmpty()) {
      return false;
    }
    ModSoundPlayer.playFrom(
        this, ModSounds.TILL_ARROW_TILL, ModSoundPlayer.LANDING_VOLUME, IMPACT_PITCH);
    return true;
  }
}
