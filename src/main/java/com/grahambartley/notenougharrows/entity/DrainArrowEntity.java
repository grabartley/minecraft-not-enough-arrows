package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.DrainArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.DrainService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DrainArrowEntity extends BaseArrowEntity {

  public DrainArrowEntity(
      final EntityType<? extends DrainArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public DrainArrowEntity(
      final EntityType<? extends DrainArrowEntity> entityType,
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
    final DrainArrowConfig drain = ServerConfigService.get().terrain().drain();
    if (!drain.enabled()) {
      return ArrowImpact.DEFAULT;
    }
    drainAround(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), drain);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    drainAround(
        world,
        entityHitResult.getEntity().getBlockPos(),
        ServerConfigService.get().terrain().drain());
    return ArrowImpact.DEFAULT;
  }

  private void drainAround(
      final ServerWorld world, final BlockPos center, final DrainArrowConfig drain) {
    if (!DrainService.drain(world, center, shootingPlayer().orElse(null), drain).isEmpty()) {
      ModSoundPlayer.playLanding(this, ModSounds.DRAIN_ARROW_ABSORB);
    }
  }
}
