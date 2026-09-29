package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.agriculture.BeeSwarmRelease;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeeArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_PITCH = 1.0f;
  private static final double CLEAR_OF_THE_FACE = 0.5;

  public BeeArrowEntity(final EntityType<? extends BeeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public BeeArrowEntity(
      final EntityType<? extends BeeArrowEntity> entityType,
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
    final Vec3d clear =
        blockHitResult
            .getPos()
            .add(Vec3d.of(blockHitResult.getSide().getVector()).multiply(CLEAR_OF_THE_FACE));
    release(world, clear, null);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    release(world, getPos(), entityHitResult.getEntity());
  }

  private void release(final ServerWorld world, final Vec3d at, @Nullable final Entity struck) {
    if (!BeeSwarmRelease.release(world, at, shooter().orElse(null), struck).isEmpty()) {
      ModSoundPlayer.playFrom(
          this, ModSounds.BEE_ARROW_RELEASE, ModSoundPlayer.LANDING_VOLUME, IMPACT_PITCH);
    }
  }
}
