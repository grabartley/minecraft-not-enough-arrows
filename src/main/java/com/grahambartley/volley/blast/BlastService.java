package com.grahambartley.volley.blast;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.audio.ModExplosion;
import com.grahambartley.volley.config.ExplosiveArrowConfig;
import com.grahambartley.volley.entity.ExplosiveArrowEntity;
import com.grahambartley.volley.explosive.ExplosiveTier;
import com.grahambartley.volley.fire.FirePatchService;
import com.grahambartley.volley.fuse.Fuse;
import com.grahambartley.volley.fuse.FuseService;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.world.PerWorld;
import com.grahambartley.volley.world.SessionTracker;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class BlastService {

  private static final PerWorld<SessionTracker<BlastCharge>> TRACKERS =
      new PerWorld<>(() -> new SessionTracker<>(BlastCharge::carrierId));

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
    TRACKERS.of(world).add(charge);
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
          shooter instanceof PlayerEntity player ? player : null,
          explosive.firePatchRadius(),
          explosive.firePatchDurationTicks());
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
    final SessionTracker<BlastCharge> tracker = TRACKERS.in(world);
    final BlastCharge charge = tracker == null ? null : tracker.remove(fuse.hostId());
    detonate(world, carrier, charge);
  }

  private static void dropChargesWithoutFuses(final ServerWorld world) {
    final SessionTracker<BlastCharge> tracker = TRACKERS.in(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    final List<UUID> lit = FuseService.fusesIn(world).stream().map(Fuse::hostId).toList();
    tracker.removeIf(charge -> !lit.contains(charge.carrierId()));
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
    ModExplosion.create(
        world,
        shooter,
        new BlastBehavior(explosive.damageTerrain(), explosive.damageEntities()),
        carrier.getPos(),
        power,
        World.ExplosionSourceType.TNT,
        ParticleTypes.EXPLOSION,
        ParticleTypes.EXPLOSION_EMITTER,
        ModSounds.EXPLOSIVE_ARROW_BLAST);
  }
}
