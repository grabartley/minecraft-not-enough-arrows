package com.grahambartley.notenougharrows.entity;

import com.grahambartley.notenougharrows.arrow.ArrowImpact;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
    resolve(
        onArrowHitBlock(serverWorld, blockHitResult),
        () -> super.onBlockHit(blockHitResult),
        () -> afterArrowHitBlock(serverWorld, blockHitResult));
  }

  @Override
  protected final void onEntityHit(final EntityHitResult entityHitResult) {
    if (!(getWorld() instanceof ServerWorld serverWorld)) {
      super.onEntityHit(entityHitResult);
      return;
    }
    final ArrowImpact impact = onArrowHitEntity(serverWorld, entityHitResult);
    resolve(
        hurtsWhatItHits() ? impact : impact.sparingWhatItHits(),
        () -> super.onEntityHit(entityHitResult),
        () -> afterArrowHitEntity(serverWorld, entityHitResult));
  }

  @Override
  public final void tick() {
    super.tick();

    if (getWorld() instanceof ServerWorld serverWorld && !isRemoved()) {
      onArrowTick(serverWorld);
    }
  }

  public Optional<LivingEntity> shooter() {
    return getOwner() instanceof LivingEntity living ? Optional.of(living) : Optional.empty();
  }

  public Optional<PlayerEntity> shootingPlayer() {
    return getOwner() instanceof PlayerEntity player ? Optional.of(player) : Optional.empty();
  }

  protected boolean hurtsWhatItHits() {
    return true;
  }

  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return ArrowImpact.DEFAULT;
  }

  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    return ArrowImpact.DEFAULT;
  }

  protected void afterArrowHitBlock(final ServerWorld world, final BlockHitResult blockHitResult) {}

  protected void afterArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {}

  protected void onArrowTick(final ServerWorld world) {}

  @Override
  protected ItemStack getDefaultItemStack() {
    return new ItemStack(Registries.ITEM.get(Registries.ENTITY_TYPE.getId(getType())));
  }

  private void resolve(
      final ArrowImpact impact, final Runnable vanillaResolution, final Runnable afterResolution) {
    if (impact.runsVanillaResolution()) {
      vanillaResolution.run();
    }
    afterResolution.run();
    if (impact.removesArrow() && !isRemoved()) {
      discard();
    }
  }
}
