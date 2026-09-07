package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.wind.WindBurstService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WindArrowEntity extends BaseArrowEntity {
  private static final double DISPLACING_DAMAGE = 0.5;

  public WindArrowEntity(
      final EntityType<? extends WindArrowEntity> entityType, final World world) {
    super(entityType, world);
    setDamage(DISPLACING_DAMAGE);
  }

  public WindArrowEntity(
      final EntityType<? extends WindArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, world, x, y, z, stack, weapon);
    setDamage(DISPLACING_DAMAGE);
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    WindBurstService.burst(world, blockHitResult.getPos(), this);
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    WindBurstService.burst(world, entityHitResult.getPos(), this);
    return ArrowImpact.DEFAULT;
  }
}
