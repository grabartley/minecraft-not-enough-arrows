package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.world.SpawnPermission;
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

  public static boolean allows(
      final ServerWorld world, final Vec3d at, @Nullable final PlayerEntity shooter) {
    return SpawnPermission.allows(world, EntityType.CHICKEN, at, shooter);
  }

  public static Optional<ChickenEntity> release(
      final ServerWorld world, final Vec3d at, @Nullable final PlayerEntity shooter) {
    if (!allows(world, at, shooter)) {
      return Optional.empty();
    }
    final ChickenEntity chicken = EntityType.CHICKEN.create(world);
    if (chicken == null) {
      return Optional.empty();
    }
    chicken.refreshPositionAndAngles(at.x, at.y, at.z, world.random.nextFloat() * 360f, 0f);
    chicken.setVelocity(Vec3d.ZERO);
    chicken.fallDistance = 0.0f;
    chicken.initialize(
        world, world.getLocalDifficulty(chicken.getBlockPos()), SpawnReason.SPAWN_EGG, null);
    chicken.setBreedingAge(0);
    return world.spawnEntity(chicken) ? Optional.of(chicken) : Optional.empty();
  }
}
