package com.grahambartley.volley.social;

import com.grahambartley.volley.world.MobSpawn;
import com.grahambartley.volley.world.SpawnPermission;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class SnowGolemBuild {

  private SnowGolemBuild() {}

  public static boolean allows(
      final ServerWorld world, final Vec3d at, @Nullable final PlayerEntity shooter) {
    return SpawnPermission.allows(world, EntityType.SNOW_GOLEM, at, shooter);
  }

  public static Optional<SnowGolemEntity> build(
      final ServerWorld world,
      final Vec3d at,
      @Nullable final PlayerEntity shooter,
      final int lifetimeTicks) {
    return MobSpawn.at(
        world,
        EntityType.SNOW_GOLEM,
        at,
        shooter,
        golem -> {
          golem.setHasPumpkin(false);
          SnowGolemMelt.schedule(golem, world.getTime() + lifetimeTicks);
        });
  }
}
