package com.grahambartley.notenougharrows.gametest;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class SteadyDispensers {
  private static final double MUZZLE_REACH = 1.0;
  private static final float NO_SPREAD = 0.0f;
  private static final Set<BlockPos> AIMED = new HashSet<>();

  static {
    ServerEntityEvents.ENTITY_LOAD.register(SteadyDispensers::straighten);
  }

  private SteadyDispensers() {}

  static void steadyEast(final BlockPos absoluteDispenser) {
    AIMED.add(absoluteDispenser.toImmutable());
  }

  private static void straighten(final Entity entity, final ServerWorld world) {
    if (!(entity instanceof PersistentProjectileEntity arrow)) {
      return;
    }
    AIMED.stream()
        .filter(dispenser -> Vec3d.ofCenter(dispenser).isInRange(arrow.getPos(), MUZZLE_REACH))
        .findFirst()
        .ifPresent(
            dispenser -> {
              AIMED.remove(dispenser);
              final Vec3d east = Vec3d.of(Direction.EAST.getVector());
              arrow.setVelocity(east.x, east.y, east.z, powerOf(arrow), NO_SPREAD);
            });
  }

  private static float powerOf(final PersistentProjectileEntity arrow) {
    return arrow.getItemStack().getItem() instanceof ProjectileItem projectile
        ? projectile.getProjectileSettings().power()
        : (float) arrow.getVelocity().length();
  }
}
