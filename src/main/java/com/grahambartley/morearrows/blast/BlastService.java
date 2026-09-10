package com.grahambartley.morearrows.blast;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.entity.ExplosiveArrowEntity;
import com.grahambartley.morearrows.explosive.ExplosiveTier;
import com.grahambartley.morearrows.fire.FirePatchService;
import com.grahambartley.morearrows.fuse.Fuse;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class BlastService {

  private static final Map<RegistryKey<World>, BlastChargeTracker> TRACKERS = new HashMap<>();

  private BlastService() {}

  public static void register() {
    FuseService.whenExpired(BlastService::onFuseExpired);
    ServerTickEvents.END_WORLD_TICK.register(BlastService::dropChargesWithoutFuses);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static void arm(
      @Nullable final ServerWorld world,
      @Nullable final Entity carrier,
      @Nullable final ExplosiveTier tier,
      @Nullable final UUID shooterId,
      final int delayTicks) {
    if (world == null || carrier == null || tier == null || carrier.isRemoved()) {
      return;
    }

    final BlastCharge charge = new BlastCharge(carrier.getUuid(), tier, shooterId);
    if (delayTicks <= 0) {
      detonate(world, carrier, charge);
      return;
    }
    trackerFor(world).add(charge);
    FuseService.light(world, carrier, delayTicks);
  }

  private static void detonate(
      @Nullable final ServerWorld world,
      @Nullable final Entity carrier,
      @Nullable final BlastCharge charge) {
    if (world == null || carrier == null || charge == null || carrier.isRemoved()) {
      return;
    }

    final ExplosiveArrowConfig explosive = ServerConfigService.get().explosive();
    final LivingEntity shooter = shooterIn(world, charge);
    blast(world, carrier, shooter, charge.tier().in(explosive).power(), explosive);
    if (charge.tier().leavesFire()) {
      FirePatchService.ignite(
          world,
          BlockPos.ofFloored(carrier.getPos()),
          shooter instanceof PlayerEntity player ? player : null);
    }
    if (carrier instanceof ExplosiveArrowEntity arrow) {
      arrow.discard();
    }
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void onFuseExpired(
      final ServerWorld world, final Entity carrier, final Fuse fuse) {
    final BlastChargeTracker tracker = trackerIn(world);
    final BlastCharge charge = tracker == null ? null : tracker.remove(fuse.hostId());
    detonate(world, carrier, charge);
  }

  private static void dropChargesWithoutFuses(final ServerWorld world) {
    final BlastChargeTracker tracker = trackerIn(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.retainOnly(FuseService.fusesIn(world).stream().map(Fuse::hostId).toList());
  }

  @Nullable
  private static LivingEntity shooterIn(final ServerWorld world, final BlastCharge charge) {
    return charge
        .shooter()
        .map(world::getEntity)
        .filter(LivingEntity.class::isInstance)
        .map(LivingEntity.class::cast)
        .orElse(null);
  }

  private static void blast(
      final ServerWorld world,
      final Entity carrier,
      @Nullable final LivingEntity shooter,
      final float power,
      final ExplosiveArrowConfig explosive) {
    if (power <= 0f) {
      return;
    }
    world.createExplosion(
        shooter,
        null,
        new BlastBehavior(explosive.damageTerrain(), explosive.damageEntities()),
        carrier.getX(),
        carrier.getY(),
        carrier.getZ(),
        power,
        false,
        World.ExplosionSourceType.TNT);
  }

  @Nullable
  private static BlastChargeTracker trackerIn(@Nullable final ServerWorld world) {
    return world == null ? null : TRACKERS.get(world.getRegistryKey());
  }

  private static BlastChargeTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new BlastChargeTracker());
  }
}
