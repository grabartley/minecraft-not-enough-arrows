package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.FreezeArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.FreezeService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FreezeArrowEntity extends BaseArrowEntity {
  private static final float IMPACT_VOLUME = 1.0f;
  private static final float IMPACT_PITCH = 1.0f;

  public FreezeArrowEntity(
      final EntityType<? extends FreezeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public FreezeArrowEntity(
      final EntityType<? extends FreezeArrowEntity> entityType,
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
    final FreezeArrowConfig freeze = ServerConfigService.get().terrain().freeze();
    if (!freeze.enabled()) {
      return ArrowImpact.DEFAULT;
    }
    freezeAround(world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), freeze);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    freezeAround(
        world,
        entityHitResult.getEntity().getBlockPos(),
        ServerConfigService.get().terrain().freeze());
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !ServerConfigService.get().terrain().freeze().enabled();
  }

  private void freezeAround(
      final ServerWorld world, final BlockPos center, final FreezeArrowConfig freeze) {
    if (!FreezeService.freeze(world, center, shootingPlayer().orElse(null), freeze).isEmpty()) {
      ModSoundPlayer.playFrom(this, ModSounds.FREEZE_ARROW_FREEZE, IMPACT_VOLUME, IMPACT_PITCH);
    }
  }
}
