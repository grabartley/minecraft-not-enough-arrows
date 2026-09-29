package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.social.MagnetPull;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MagnetArrowEntity extends BaseArrowEntity {
  private static final float PULL_VOLUME = 1.0f;
  private static final float PULL_PITCH = 1.0f;

  public MagnetArrowEntity(
      final EntityType<? extends MagnetArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public MagnetArrowEntity(
      final EntityType<? extends MagnetArrowEntity> entityType,
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
    pullToward(world, blockHitResult.getPos());
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    pullToward(world, entityHitResult.getEntity().getPos());
    return ArrowImpact.DISCARD;
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }

  private void pullToward(final ServerWorld world, final Vec3d impact) {
    final int radius = ServerConfigService.get().social().magnet().radius();
    if (MagnetPull.pull(world, impact, shooter().orElse(null), radius).isEmpty()) {
      return;
    }
    ModSoundPlayer.play(
        world, impact, ModSounds.MAGNET_ARROW_PULL, getSoundCategory(), PULL_VOLUME, PULL_PITCH);
  }
}
