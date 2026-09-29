package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.arrow.FaceClearance;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.chaos.StinkCloudService;
import com.grahambartley.notenougharrows.config.StinkArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StinkArrowEntity extends BaseArrowEntity {
  private static final float RELEASE_PITCH = 1.0f;

  public StinkArrowEntity(
      final EntityType<? extends StinkArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public StinkArrowEntity(
      final EntityType<? extends StinkArrowEntity> entityType,
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
    final Vec3d inFront =
        FaceClearance.inFrontOf(blockHitResult.getPos(), blockHitResult.getSide());
    return release(world, inFront) ? ArrowImpact.DISCARD : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (!config().enabled()) {
      return ArrowImpact.DEFAULT;
    }
    return release(world, entityHitResult.getPos())
        ? ArrowImpact.DISCARD
        : glanceOff(entityHitResult.getEntity());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !config().enabled();
  }

  private boolean release(final ServerWorld world, final Vec3d at) {
    if (!StinkCloudService.open(world, at, config())) {
      return false;
    }
    ModSoundPlayer.playFrom(
        this, ModSounds.STINK_ARROW_RELEASE, ModSoundPlayer.LANDING_VOLUME, RELEASE_PITCH);
    return true;
  }

  private static StinkArrowConfig config() {
    return ServerConfigService.get().chaos().stink();
  }
}
