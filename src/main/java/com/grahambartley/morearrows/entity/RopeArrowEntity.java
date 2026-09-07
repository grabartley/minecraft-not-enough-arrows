package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.rope.RopeService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RopeArrowEntity extends BaseArrowEntity {

  public RopeArrowEntity(
      final EntityType<? extends RopeArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public RopeArrowEntity(
      final EntityType<? extends RopeArrowEntity> entityType,
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
    RopeService.drop(world, blockHitResult.getBlockPos(), shootingPlayer().orElse(null));
    return ArrowImpact.DEFAULT;
  }
}
