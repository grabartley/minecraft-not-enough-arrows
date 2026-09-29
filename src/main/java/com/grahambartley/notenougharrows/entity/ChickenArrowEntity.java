package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.chaos.ChickenRelease;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChickenArrowEntity extends BaseArrowEntity {
  private static final float HATCH_VOLUME = 1.0f;
  private static final float HATCH_PITCH = 1.2f;

  public ChickenArrowEntity(
      final EntityType<? extends ChickenArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public ChickenArrowEntity(
      final EntityType<? extends ChickenArrowEntity> entityType,
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
    if (!isHatching()) {
      return ArrowImpact.DEFAULT;
    }
    final Vec3d inFront =
        Vec3d.ofBottomCenter(blockHitResult.getBlockPos().offset(blockHitResult.getSide()));
    return hatchAt(world, inFront) ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (!isHatching()) {
      return ArrowImpact.DEFAULT;
    }
    if (hatchAt(world, entityHitResult.getEntity().getPos())) {
      return ArrowImpact.DISCARD;
    }
    return glanceOff(entityHitResult.getEntity());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !isHatching();
  }

  private boolean hatchAt(final ServerWorld world, final Vec3d at) {
    if (ChickenRelease.release(world, at, shootingPlayer().orElse(null)).isEmpty()) {
      return false;
    }
    ModSoundPlayer.play(
        world, at, ModSounds.CHICKEN_ARROW_HATCH, getSoundCategory(), HATCH_VOLUME, HATCH_PITCH);
    return true;
  }

  private static boolean isHatching() {
    return ServerConfigService.get().chaos().chicken().enabled();
  }
}
