package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.grapple.GrappleService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrappleArrowEntity extends BaseArrowEntity {

  public GrappleArrowEntity(
      final EntityType<? extends GrappleArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public GrappleArrowEntity(
      final EntityType<? extends GrappleArrowEntity> entityType,
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
    shootingPlayer()
        .ifPresent(player -> GrappleService.start(world, player, blockHitResult.getBlockPos()));
    return ArrowImpact.DEFAULT;
  }
}
