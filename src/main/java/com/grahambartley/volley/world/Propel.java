package com.grahambartley.volley.world;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class Propel {

  private Propel() {}

  public static void drive(final Entity entity, final Vec3d velocity) {
    entity.setVelocity(velocity);
    entity.velocityModified = true;
    if (entity instanceof ServerPlayerEntity player && player.networkHandler != null) {
      player.networkHandler.floatingTicks = 0;
    }
  }
}
