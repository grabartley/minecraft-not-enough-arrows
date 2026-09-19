package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MilkArrowEntity extends BaseArrowEntity {

  public MilkArrowEntity(
      final EntityType<? extends MilkArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public MilkArrowEntity(
      final EntityType<? extends MilkArrowEntity> entityType,
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
    if (entityHitResult.getEntity() instanceof LivingEntity living) {
      living.clearStatusEffects();
    }
    return ArrowImpact.DEFAULT;
  }
}
