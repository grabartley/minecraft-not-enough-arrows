package com.grahambartley.volley.control;

import com.grahambartley.volley.Volley;
import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public final class ControlPersistence {
  public static final AttachmentType<ControlHold> HOLD =
      AttachmentRegistry.createPersistent(
          Identifier.of(Volley.MOD_ID, "control_hold"), ControlHold.CODEC);
  public static final AttachmentType<DisarmFetch> FETCH =
      AttachmentRegistry.createPersistent(
          Identifier.of(Volley.MOD_ID, "disarm_fetch"), DisarmFetch.CODEC);
  public static final AttachmentType<Long> FROST_UNTIL =
      AttachmentRegistry.createPersistent(Identifier.of(Volley.MOD_ID, "frost_until"), Codec.LONG);

  private ControlPersistence() {}

  public static void register() {
    ServerEntityEvents.ENTITY_LOAD.register(ControlPersistence::restore);
  }

  public static void restore(final Entity entity, final ServerWorld world) {
    final long now = world.getTime();
    if (entity instanceof MobEntity mob) {
      restoreIfLive(
          mob, HOLD, hold -> hold.hasExpired(now), hold -> ControlHoldService.resume(world, hold));
      restoreIfLive(
          mob,
          FETCH,
          fetch -> fetch.hasExpired(now),
          fetch -> DisarmFetchService.resume(world, fetch));
    }
    if (entity instanceof LivingEntity living) {
      restoreIfLive(
          living,
          FROST_UNTIL,
          until -> until <= now,
          until -> FrostGripService.resume(world, living, until));
    }
  }

  private static <T> void restoreIfLive(
      final Entity entity,
      final AttachmentType<T> type,
      final Predicate<T> hasExpired,
      final Consumer<T> resume) {
    final T saved = entity.getAttached(type);
    if (saved == null) {
      return;
    }
    if (hasExpired.test(saved)) {
      entity.removeAttached(type);
    } else {
      resume.accept(saved);
    }
  }
}
