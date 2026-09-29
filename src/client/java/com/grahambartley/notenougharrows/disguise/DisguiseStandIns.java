package com.grahambartley.notenougharrows.disguise;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class DisguiseStandIns {
  private static final Map<Integer, StandIn> STAND_INS = new HashMap<>();

  private DisguiseStandIns() {}

  public static Optional<LivingEntity> standInFor(final Entity disguised) {
    final Optional<Identifier> form = DisguiseSync.formOf(disguised.getId());
    if (form.isEmpty() || !(disguised instanceof LivingEntity living)) {
      STAND_INS.remove(disguised.getId());
      return Optional.empty();
    }
    StandIn standIn = STAND_INS.get(disguised.getId());
    if (standIn == null || !standIn.form().equals(form.get())) {
      standIn = create(disguised, form.get());
      if (standIn == null) {
        return Optional.empty();
      }
      STAND_INS.put(disguised.getId(), standIn);
    }
    mirror(living, standIn.body());
    return Optional.of(standIn.body());
  }

  public static void forget(final int entityId) {
    STAND_INS.remove(entityId);
  }

  public static void clear() {
    STAND_INS.clear();
  }

  private static StandIn create(final Entity disguised, final Identifier form) {
    return Registries.ENTITY_TYPE.get(form).create(disguised.getWorld())
            instanceof LivingEntity body
        ? new StandIn(form, body)
        : null;
  }

  static void mirror(final LivingEntity real, final LivingEntity standIn) {
    standIn.setPosition(real.getX(), real.getY(), real.getZ());
    standIn.prevX = real.prevX;
    standIn.prevY = real.prevY;
    standIn.prevZ = real.prevZ;
    standIn.lastRenderX = real.lastRenderX;
    standIn.lastRenderY = real.lastRenderY;
    standIn.lastRenderZ = real.lastRenderZ;
    standIn.setYaw(real.getYaw());
    standIn.prevYaw = real.prevYaw;
    standIn.setPitch(real.getPitch());
    standIn.prevPitch = real.prevPitch;
    standIn.bodyYaw = real.bodyYaw;
    standIn.prevBodyYaw = real.prevBodyYaw;
    standIn.headYaw = real.headYaw;
    standIn.prevHeadYaw = real.prevHeadYaw;
    standIn.age = real.age;
    standIn.hurtTime = real.hurtTime;
    standIn.maxHurtTime = real.maxHurtTime;
    standIn.deathTime = real.deathTime;
    standIn.setOnGround(real.isOnGround());
    standIn.setCustomName(real.getCustomName());
    standIn.setCustomNameVisible(real.isCustomNameVisible());
    standIn.setInvisible(real.isInvisible());
    standIn.setGlowing(real.isGlowing());
    standIn.limbAnimator.prevSpeed = real.limbAnimator.prevSpeed;
    standIn.limbAnimator.speed = real.limbAnimator.speed;
    standIn.limbAnimator.pos = real.limbAnimator.pos;
  }

  private record StandIn(Identifier form, LivingEntity body) {}
}
