package com.grahambartley.morearrows.entity;

import com.grahambartley.morearrows.arrow.ArrowImpact;
import com.grahambartley.morearrows.blast.BlastService;
import com.grahambartley.morearrows.explosive.ExplosiveTier;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class ExplosiveArrowEntity extends BaseArrowEntity {
  private final Set<Integer> struck = new HashSet<>();
  private final ExplosiveTier tier;

  protected ExplosiveArrowEntity(
      final EntityType<? extends ExplosiveArrowEntity> entityType,
      final World world,
      final ExplosiveTier tier) {
    super(entityType, world);
    this.tier = Objects.requireNonNull(tier, "tier");
  }

  protected ExplosiveArrowEntity(
      final EntityType<? extends ExplosiveArrowEntity> entityType,
      final World world,
      final double x,
      final double y,
      final double z,
      final ItemStack stack,
      @Nullable final ItemStack weapon,
      final ExplosiveTier tier) {
    super(entityType, world, x, y, z, stack, weapon);
    this.tier = Objects.requireNonNull(tier, "tier");
  }

  public ExplosiveTier tier() {
    return tier;
  }

  @Override
  protected ArrowImpact onArrowHitBlock(
      final ServerWorld world, final BlockHitResult blockHitResult) {
    return arm(world, ArrowImpact.DEFAULT);
  }

  @Override
  protected ArrowImpact onArrowHitEntity(
      final ServerWorld world, final EntityHitResult entityHitResult) {
    struck.add(entityHitResult.getEntity().getId());
    final ArrowImpact impact = arm(world, ArrowImpact.RETAIN);
    // Vanilla would discard the arrow here and the fuse would die with it, so the arrow is
    // stopped by hand instead. Without this it keeps full velocity through an unclamped hit
    // and can pass into the block behind its target.
    setVelocity(Vec3d.ZERO);
    velocityModified = true;
    return impact;
  }

  @Override
  protected boolean canHit(final Entity entity) {
    return !struck.contains(entity.getId()) && super.canHit(entity);
  }

  private ArrowImpact arm(final ServerWorld world, final ArrowImpact armed) {
    if (FuseService.fuseOn(world, getUuid()) != null) {
      return armed;
    }

    final int delayTicks = tier.in(ServerConfigService.get().explosive()).delayTicks();
    if (delayTicks <= 0) {
      BlastService.detonate(world, this);
      return ArrowImpact.RETAIN;
    }

    FuseService.light(world, this, delayTicks);
    return armed;
  }
}
