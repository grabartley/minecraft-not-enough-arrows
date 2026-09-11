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
  private static final double MARKING_DAMAGE = 0.5;

  public GlowInkArrowEntity(
      final EntityType<? extends GlowInkArrowEntity> entityType, final World world) {
    super(entityType, world);
    setDamage(MARKING_DAMAGE);
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
    setDamage(MARKING_DAMAGE);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    GlowService.mark(world, entityHitResult.getEntity(), this);
    return ArrowImpact.DEFAULT;
  }
}
