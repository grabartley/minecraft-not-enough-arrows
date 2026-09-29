package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.PufferArrowConfig;
import com.grahambartley.notenougharrows.world.ExpiringLedger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class PufferService {

  private static final Map<RegistryKey<World>, ExpiringLedger<Boolean>> LEDGERS = new HashMap<>();

  private PufferService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(PufferService::tick);
    ServerEntityEvents.ENTITY_UNLOAD.register(PufferService::forget);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> LEDGERS.clear());
  }

  public static boolean inflate(
      final ServerWorld world, final LivingEntity target, final PufferArrowConfig puffer) {
    if (world == null || target == null || puffer == null || puffer.durationTicks() <= 0) {
      return false;
    }
    if (!PufferInflation.isInflated(target) && PufferInflation.inflate(target).isEmpty()) {
      return false;
    }
    ledgerFor(world).put(target.getUuid(), Boolean.TRUE, world.getTime() + puffer.durationTicks());
    return true;
  }

  public static boolean isTracked(final ServerWorld world, final UUID id) {
    final ExpiringLedger<Boolean> ledger = LEDGERS.get(world.getRegistryKey());
    return ledger != null && ledger.contains(id);
  }

  private static void tick(final ServerWorld world) {
    final ExpiringLedger<Boolean> ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger == null || ledger.isEmpty()) {
      return;
    }
    for (final UUID expired : ledger.removeExpired(world.getTime())) {
      if (world.getEntity(expired) instanceof LivingEntity target
          && PufferInflation.deflate(target)) {
        ModSoundPlayer.playLanding(target, ModSounds.PUFFER_ARROW_DEFLATE);
      }
    }
  }

  private static void forget(final Entity entity, final ServerWorld world) {
    final ExpiringLedger<Boolean> ledger = LEDGERS.get(world.getRegistryKey());
    if (ledger != null
        && ledger.remove(entity.getUuid()).isPresent()
        && entity instanceof LivingEntity living) {
      PufferInflation.deflate(living);
    }
  }

  private static ExpiringLedger<Boolean> ledgerFor(final ServerWorld world) {
    return LEDGERS.computeIfAbsent(world.getRegistryKey(), key -> new ExpiringLedger<>());
  }
}
