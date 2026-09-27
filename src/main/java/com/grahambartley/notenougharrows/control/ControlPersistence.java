package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.mojang.serialization.Codec;
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
          Identifier.of(NotEnoughArrows.MOD_ID, "control_hold"), ControlHold.CODEC);
  public static final AttachmentType<DisarmFetch> FETCH =
      AttachmentRegistry.createPersistent(
          Identifier.of(NotEnoughArrows.MOD_ID, "disarm_fetch"), DisarmFetch.CODEC);
  public static final AttachmentType<Long> FROST_UNTIL =
      AttachmentRegistry.createPersistent(
          Identifier.of(NotEnoughArrows.MOD_ID, "frost_until"), Codec.LONG);

  private ControlPersistence() {}

  public static void register() {
    ServerEntityEvents.ENTITY_LOAD.register(ControlPersistence::restore);
  }

  public static void restore(final Entity entity, final ServerWorld world) {
    final long now = world.getTime();
    if (entity instanceof MobEntity mob) {
      final ControlHold hold = mob.getAttached(HOLD);
      if (hold != null) {
        if (hold.hasExpired(now)) {
          mob.removeAttached(HOLD);
        } else {
          ControlHoldService.resume(world, hold);
        }
      }
      final DisarmFetch fetch = mob.getAttached(FETCH);
      if (fetch != null) {
        if (fetch.hasExpired(now)) {
          mob.removeAttached(FETCH);
        } else {
          DisarmFetchService.resume(world, fetch);
        }
      }
    }
    if (entity instanceof LivingEntity living) {
      final Long frostUntil = living.getAttached(FROST_UNTIL);
      if (frostUntil != null) {
        if (frostUntil <= now) {
          living.removeAttached(FROST_UNTIL);
        } else {
          FrostGripService.resume(world, living, frostUntil);
        }
      }
    }
  }
}
