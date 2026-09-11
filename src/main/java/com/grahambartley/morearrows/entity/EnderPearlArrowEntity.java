package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.ender.PearlArrivalService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderPearlArrowEntity extends BaseArrowEntity {

  public EnderPearlArrowEntity(
      final EntityType<? extends EnderPearlArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public EnderPearlArrowEntity(
      final EntityType<? extends EnderPearlArrowEntity> entityType,
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
    return teleportShooterTo(world, blockHitResult.getPos())
        ? ArrowImpact.DISCARD
        : ArrowImpact.DEFAULT;
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    teleportShooterTo(world, entityHitResult.getEntity().getPos());
    return ArrowImpact.DEFAULT;
  }

  private boolean teleportShooterTo(final ServerWorld world, final Vec3d destination) {
    return PearlArrivalService.arrive(world, shootingPlayer().orElse(null), destination);
  }
}
