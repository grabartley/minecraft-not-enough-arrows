package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.PaintService;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PaintArrowEntity extends TintedArrowEntity {
  public PaintArrowEntity(
      final EntityType<? extends PaintArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public PaintArrowEntity(
      final EntityType<? extends PaintArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  public DyeColor colour() {
    return DyeColor.byName(tint().key(), DyeColor.WHITE);
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return PaintService.paintBlock(
            world, blockHitResult.getBlockPos(), colour(), shootingPlayer().orElse(null))
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    if (!isPainting()) {
      return ArrowImpact.DEFAULT;
    }
    if (entityHitResult.getEntity() instanceof SheepEntity sheep
        && PaintService.paintSheep(sheep, colour(), shootingPlayer().orElse(null))) {
      return ArrowImpact.DISCARD;
    }
    return glanceOff(entityHitResult.getEntity());
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return !isPainting();
  }

  private static boolean isPainting() {
    return ServerConfigService.get().terrain().paint().enabled();
  }
}
