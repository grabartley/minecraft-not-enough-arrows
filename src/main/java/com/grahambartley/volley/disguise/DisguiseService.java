package com.grahambartley.volley.disguise;

import com.grahambartley.volley.ModSounds;
import com.grahambartley.volley.audio.ModSoundPlayer;
import com.grahambartley.volley.world.ExpiringLedger;
import com.grahambartley.volley.world.ParticleBursts;
import com.grahambartley.volley.world.PerWorld;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class DisguiseService {

  private static final int RESTORE_PUFFS = 12;
  private static final PerWorld<ExpiringLedger<Identifier>> LEDGERS =
      new PerWorld<>(ExpiringLedger::new);

  private DisguiseService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(DisguiseService::tick);
    ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> revert(world, entity));
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (entity, source) -> {
          if (entity.getWorld() instanceof ServerWorld world) {
            revert(world, entity);
          }
        });
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> LEDGERS.clear());
    DisguiseBroadcaster.register();
  }

  public static boolean disguise(
      final ServerWorld world, final Entity target, final int durationTicks) {
    if (world == null || durationTicks <= 0 || !DisguiseEligibility.canDisguise(target)) {
      return false;
    }
    final ExpiringLedger<Identifier> ledger = LEDGERS.of(world);
    final long expiry = world.getTime() + durationTicks;
    final Optional<Identifier> worn = ledger.get(target.getUuid());
    if (worn.isPresent()) {
      ledger.put(target.getUuid(), worn.get(), expiry);
      return true;
    }
    final Identifier form = DisguiseForms.pick(world.random);
    ledger.put(target.getUuid(), form, expiry);
    DisguisedBehaviour.begin((MobEntity) target);
    DisguiseBroadcaster.wearing(target, form);
    return true;
  }

  public static boolean revert(final ServerWorld world, final Entity target) {
    final ExpiringLedger<Identifier> ledger = LEDGERS.in(world);
    if (ledger == null || ledger.remove(target.getUuid()).isEmpty()) {
      return false;
    }
    DisguiseBroadcaster.restored(target);
    return true;
  }

  public static boolean isDisguised(final Entity entity) {
    return entity.getWorld() instanceof ServerWorld world
        && formOf(world, entity.getUuid()).isPresent();
  }

  public static Optional<Identifier> formOf(final ServerWorld world, final UUID id) {
    return LEDGERS.map(world, ledger -> ledger.get(id), Optional.empty());
  }

  private static void tick(final ServerWorld world) {
    final ExpiringLedger<Identifier> ledger = LEDGERS.in(world);
    if (ledger == null || ledger.isEmpty()) {
      return;
    }
    for (final UUID expired : ledger.removeExpired(world.getTime())) {
      final Entity entity = world.getEntity(expired);
      if (entity != null) {
        DisguiseBroadcaster.restored(entity);
        showRestore(world, entity);
      }
    }
  }

  private static void showRestore(final ServerWorld world, final Entity entity) {
    ParticleBursts.aroundBody(world, entity, ParticleTypes.POOF, RESTORE_PUFFS, 0.02);
    ModSoundPlayer.playFrom(entity, ModSounds.POLYMORPH_ARROW_RESTORE);
  }
}
