package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import com.grahambartley.notenougharrows.combat.ShockStrike;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShockArrowEntity extends BaseArrowEntity {

  public ShockArrowEntity(
      final EntityType<? extends ShockArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  public ShockArrowEntity(
      final EntityType<? extends ShockArrowEntity> entityType,
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
    return ArrowImpact.CONSUME;
  }

  @Override
  protected void afterArrowHitBlock(final ServerWorld world, final BlockHitResult blockHitResult) {
    ShockStrike.strike(
        world, blockHitResult.getPos(), null, ServerConfigService.get().combat().shock(), this);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    return ArrowImpact.CONSUME;
  }

  @Override
  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    ShockStrike.strike(
        world,
        entityHitResult.getEntity().getPos(),
        entityHitResult.getEntity(),
        ServerConfigService.get().combat().shock(),
        this);
  }
}
