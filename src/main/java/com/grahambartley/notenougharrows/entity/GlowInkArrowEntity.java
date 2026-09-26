package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.glow.GlowService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlowInkArrowEntity extends BaseArrowEntity {

  public GlowInkArrowEntity(
      final EntityType<? extends GlowInkArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public GlowInkArrowEntity(
      final EntityType<? extends GlowInkArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    GlowService.mark(world, entityHitResult.getEntity(), this);
    return ArrowImpact.DEFAULT;
  }

  @Override
  protected boolean hurtsWhatItHits() {
    return false;
  }
}
