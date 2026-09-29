package com.grahambartley.notenougharrows.disguise;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.world.ExpiringLedger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class DisguiseService {
  public static final float CUE_VOLUME = 1.0f;
  public static final float CUE_PITCH = 1.0f;

  private static final int RESTORE_PUFFS = 12;
  private static final Map<RegistryKey<World>, ExpiringLedger<Identifier>> LEDGERS =
      new HashMap<>();

  private DisguiseService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(DisguiseService::tick);
    ServerEntityEvents.ENTITY_UNLOAD.register(DisguiseService::revert);
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (entity, source) -> {
          if (entity.getWorld() instanceof ServerWorld world) {
            revert(entity, world);
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
    final ExpiringLedger<Identifier> ledger = ledgerFor(world);
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
    final ExpiringLedger<Identifier> ledger = LEDGERS.get(world.getRegistryKey());
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
    final ExpiringLedger<Identifier> ledger = LEDGERS.get(world.getRegistryKey());
    return ledger == null ? Optional.empty() : ledger.get(id);
  }

  public static int liveDisguises(final ServerWorld world) {
    final ExpiringLedger<Identifier> ledger = LEDGERS.get(world.getRegistryKey());
    return ledger == null ? 0 : ledger.size();
  }

  private static void revert(final Entity entity, final ServerWorld world) {
    revert(world, entity);
  }

  private static void tick(final ServerWorld world) {
    final ExpiringLedger<Identifier> ledger = LEDGERS.get(world.getRegistryKey());
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
    world.spawnParticles(
        ParticleTypes.POOF,
        entity.getX(),
        entity.getBodyY(0.5),
        entity.getZ(),
        RESTORE_PUFFS,
        entity.getWidth() / 2.0,
        entity.getHeight() / 2.0,
        entity.getWidth() / 2.0,
        0.02);
    ModSoundPlayer.playFrom(entity, ModSounds.POLYMORPH_ARROW_RESTORE, CUE_VOLUME, CUE_PITCH);
  }

  private static ExpiringLedger<Identifier> ledgerFor(final ServerWorld world) {
    return LEDGERS.computeIfAbsent(world.getRegistryKey(), key -> new ExpiringLedger<>());
  }
}
