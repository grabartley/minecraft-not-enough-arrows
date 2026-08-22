package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BaseArrowEntity extends PersistentProjectileEntity {

  protected BaseArrowEntity(
      final EntityType<? extends BaseArrowEntity> entityType, final World world) {
    super(entityType, world);
  }

  protected BaseArrowEntity(
      final EntityType<? extends BaseArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon) {
    super(entityType, x, y, z, world, stack, weapon);
  }

  @Override
  protected final void onBlockHit(final BlockHitResult blockHitResult) {
    if (!(getWorld() instanceof ServerWorld serverWorld)) {
      super.onBlockHit(blockHitResult);
      return;
    }
    resolve(onArrowHitBlock(serverWorld, blockHitResult), () -> super.onBlockHit(blockHitResult));
  }

  @Override
  protected final void onEntityHit(final EntityHitResult entityHitResult) {
    if (!(getWorld() instanceof ServerWorld serverWorld)) {
      super.onEntityHit(entityHitResult);
      return;
    }
    resolve(
        onArrowHitEntity(serverWorld, entityHitResult), () -> super.onEntityHit(entityHitResult));
  }

  @Override
  public final void tick() {
    super.tick();

    if (getWorld() instanceof ServerWorld serverWorld && !isRemoved()) {
      onArrowTick(serverWorld);
    }
  }

  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return ArrowImpact.DEFAULT;
  }

  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    return ArrowImpact.DEFAULT;
  }

  protected void onArrowTick(final ServerWorld world) {}

  @Override
  protected ItemStack getDefaultItemStack() {
    return new ItemStack(Registries.ITEM.get(Registries.ENTITY_TYPE.getId(getType())));
  }

  private void resolve(final ArrowImpact impact, final Runnable vanillaResolution) {
    if (impact.runsVanillaResolution()) {
      vanillaResolution.run();
    }
    if (impact.removesArrow() && !isRemoved()) {
      discard();
    }
  }
}
