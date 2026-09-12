package com.grahambartley.notenougharrows.blast;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public final class BlastBehavior extends ExplosionBehavior {
  private final boolean damagesTerrain;
  private final boolean damagesEntities;

  public BlastBehavior(final boolean damagesTerrain, final boolean damagesEntities) {
    this.damagesTerrain = damagesTerrain;
    this.damagesEntities = damagesEntities;
  }

  @Override
  public boolean canDestroyBlock(
      final Explosion explosion,
      final BlockView world,
      final BlockPos pos,
      final BlockState state,
      final float power) {
    return damagesTerrain && super.canDestroyBlock(explosion, world, pos, state, power);
  }

  @Override
  public boolean shouldDamage(final Explosion explosion, final Entity entity) {
    return damagesEntities && super.shouldDamage(explosion, entity);
  }
}
