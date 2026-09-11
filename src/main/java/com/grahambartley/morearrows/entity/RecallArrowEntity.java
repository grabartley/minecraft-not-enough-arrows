package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.ender.RecallService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RecallArrowEntity extends BaseArrowEntity {

  public RecallArrowEntity(
      final EntityType<? extends RecallArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RecallArrowEntity(
      final EntityType<? extends RecallArrowEntity> entityType,
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
    RecallService.recall(world, shootingPlayer().orElse(null), entityHitResult.getEntity());
    return ArrowImpact.DEFAULT;
  }
}
