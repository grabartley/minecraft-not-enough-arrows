package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.ender.PearlArrivalService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderPearlArrowEntity extends BaseArrowEntity {
  private static final double NO_DAMAGE = 0.0;

  public EnderPearlArrowEntity(
      final EntityType<? extends EnderPearlArrowEntity> entityType, final World world) {
    super(entityType, world);
    setDamage(NO_DAMAGE);
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
    setDamage(NO_DAMAGE);
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
    return ArrowImpact.DISCARD;
  }

  private boolean teleportShooterTo(final ServerWorld world, final Vec3d destination) {
    return PearlArrivalService.arrive(world, shootingPlayer().orElse(null), destination);
  }
}
