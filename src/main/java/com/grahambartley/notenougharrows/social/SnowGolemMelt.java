package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.world.ExpiringLedger;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class SnowGolemMelt {
  public static final AttachmentType<Long> MELTS_AT =
      AttachmentRegistry.createPersistent(
          Identifier.of(NotEnoughArrows.MOD_ID, "snow_golem_melts_at"), Codec.LONG);

  private static final int MELT_PARTICLES = 24;
  private static final double MELT_SPREAD = 0.4;
  private static final double MELT_SPEED = 0.05;
  private static final float MELT_VOLUME = 1.0f;
  private static final float MELT_PITCH = 1.0f;

  private static final Map<RegistryKey<World>, ExpiringLedger<Boolean>> LEDGERS = new HashMap<>();

  private SnowGolemMelt() {}

  public static void register() {
    ServerEntityEvents.ENTITY_LOAD.register(SnowGolemMelt::track);
    ServerEntityEvents.ENTITY_UNLOAD.register(SnowGolemMelt::forget);
    ServerTickEvents.END_WORLD_TICK.register(SnowGolemMelt::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> LEDGERS.clear());
  }

  public static void schedule(final SnowGolemEntity golem, final long meltsAt) {
    golem.setAttached(MELTS_AT, meltsAt);
  }

  public static OptionalLong meltsAt(final Entity entity) {
    final Long meltsAt = entity.getAttached(MELTS_AT);
    return meltsAt == null ? OptionalLong.empty() : OptionalLong.of(meltsAt);
  }

  public static boolean isTracked(final ServerWorld world, final UUID id) {
    final ExpiringLedger<Boolean> ledger = LEDGERS.get(world.getRegistryKey());
    return ledger != null && ledger.contains(id);
  }

  public static void track(final Entity entity, final ServerWorld world) {
    if (entity instanceof SnowGolemEntity golem) {
      meltsAt(golem)
          .ifPresent(
              meltsAt ->
                  LEDGERS
                      .computeIfAbsent(world.getRegistryKey(), key -> new ExpiringLedger<>())
                      .put(golem.getUuid(), Boolean.TRUE, meltsAt));
    }
  }

  private static void forget(final Entity entity, final ServerWorld world) {
    final ExpiringLedger<Boolean> ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger != null) {
      ledger.remove(entity.getUuid());
    }
  }

  private static void tick(final ServerWorld world) {
    final ExpiringLedger<Boolean> ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger == null || ledger.isEmpty()) {
      return;
    }
    for (final UUID expired : ledger.removeExpired(world.getTime())) {
      if (world.getEntity(expired) instanceof SnowGolemEntity golem && golem.isAlive()) {
        melt(world, golem);
      }
    }
  }

  private static void melt(final ServerWorld world, final SnowGolemEntity golem) {
    world.spawnParticles(
        ParticleTypes.SNOWFLAKE,
        golem.getX(),
        golem.getBodyY(0.5),
        golem.getZ(),
        MELT_PARTICLES,
        MELT_SPREAD,
        MELT_SPREAD,
        MELT_SPREAD,
        MELT_SPEED);
    ModSoundPlayer.playFrom(golem, ModSounds.SNOW_GOLEM_ARROW_MELT, MELT_VOLUME, MELT_PITCH);
    golem.discard();
  }
}
