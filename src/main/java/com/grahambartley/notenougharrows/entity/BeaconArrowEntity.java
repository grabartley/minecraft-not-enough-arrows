package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.discovery.BeaconService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeaconArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.0f;
  private boolean fired;

  public BeaconArrowEntity(
      final EntityType<? extends BeaconArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public BeaconArrowEntity(
      final EntityType<? extends BeaconArrowEntity> entityType,
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
    return raiseAt(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()))
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    raiseAt(world, getBlockPos());
  }

  private boolean raiseAt(final ServerWorld world, final BlockPos base) {
    if (fired) {
      return false;
    }
    fired = true;
    if (BeaconService.raise(world, base, shootingPlayer().orElse(null)).isEmpty()) {
      return false;
    }
    ModSoundPlayer.playFrom(this, ModSounds.BEACON_ARROW_RAISE, IMPACT_VOLUME, IMPACT_PITCH);
    return true;
  }
}
