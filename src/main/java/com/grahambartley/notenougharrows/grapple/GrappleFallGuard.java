package com.grahambartley.notenougharrows.grapple;

import com.grahambartley.notenougharrows.server.PlayerExit;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class GrappleFallGuard {
  private static final Set<UUID> SPARED = ConcurrentHashMap.newKeySet();

  private GrappleFallGuard() {}

  public static void register() {
    ServerLivingEntityEvents.ALLOW_DAMAGE.register(GrappleFallGuard::allowDamage);
    ServerTickEvents.START_WORLD_TICK.register(GrappleFallGuard::releaseWhoeverLandedIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    PlayerExit.whenLeaving(GrappleFallGuard::release);
  }

  public static void spare(@Nullable final UUID playerId) {
    if (playerId != null) {
      SPARED.add(playerId);
    }
  }

  public static boolean spares(@Nullable final UUID playerId) {
    return playerId != null && SPARED.contains(playerId);
  }

  public static void release(@Nullable final UUID playerId) {
    if (playerId != null) {
      SPARED.remove(playerId);
    }
  }

  public static void forget() {
    SPARED.clear();
  }

  static boolean isTheFallAGrappleOwns(@Nullable final DamageSource source) {
    return source != null && source.isOf(DamageTypes.FALL);
  }

  static boolean allowDamage(
      final LivingEntity entity, final DamageSource source, final float amount) {
    if (!spares(entity.getUuid()) || !isTheFallAGrappleOwns(source)) {
      return true;
    }
    release(entity.getUuid());
    return false;
  }

  private static boolean hasLanded(final LivingEntity subject) {
    return subject.isOnGround() || subject.isTouchingWater();
  }

  private static void releaseWhoeverLandedIn(final ServerWorld world) {
    if (SPARED.isEmpty()) {
      return;
    }
    SPARED.removeIf(
        playerId -> world.getEntity(playerId) instanceof LivingEntity landed && hasLanded(landed));
  }
}
