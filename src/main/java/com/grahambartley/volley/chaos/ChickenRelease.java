package com.grahambartley.volley.chaos;

import com.grahambartley.volley.world.MobSpawn;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class ChickenRelease {

  private ChickenRelease() {}

  public static Optional<ChickenEntity> release(
      final ServerWorld world, final Vec3d at, @Nullable final PlayerEntity shooter) {
    return MobSpawn.at(
        world,
        EntityType.CHICKEN,
        at,
        shooter,
        chicken -> {
          chicken.setVelocity(Vec3d.ZERO);
          chicken.fallDistance = 0.0f;
          chicken.initialize(
              world, world.getLocalDifficulty(chicken.getBlockPos()), SpawnReason.SPAWN_EGG, null);
          chicken.setBreedingAge(0);
        });
  }
}
