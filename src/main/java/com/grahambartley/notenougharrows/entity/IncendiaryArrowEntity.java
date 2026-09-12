package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.incendiary.IncendiaryService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class IncendiaryArrowEntity extends BaseArrowEntity {

  public IncendiaryArrowEntity(
      final EntityType<? extends IncendiaryArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public IncendiaryArrowEntity(
      final EntityType<? extends IncendiaryArrowEntity> entityType,
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
    IncendiaryService.ignite(world, blockHitResult.getPos(), this, shootingPlayer().orElse(null));
    return ArrowImpact.DISCARD;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    IncendiaryService.ignite(world, entityHitResult.getPos(), this, shootingPlayer().orElse(null));
    return ArrowImpact.DEFAULT;
  }
}
