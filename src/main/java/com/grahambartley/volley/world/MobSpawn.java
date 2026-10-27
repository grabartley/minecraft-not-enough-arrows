package com.grahambartley.volley.world;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class MobSpawn {

  private MobSpawn() {}

  public static <T extends MobEntity> Optional<T> at(
      final ServerWorld world,
      final EntityType<T> type,
      final Vec3d at,
      @Nullable final PlayerEntity shooter,
      final Consumer<T> setup) {
    if (!SpawnPermission.allows(world, type, at, shooter)) {
      return Optional.empty();
    }
    final T mob = type.create(world);
    if (mob == null) {
      return Optional.empty();
    }
    mob.refreshPositionAndAngles(at.x, at.y, at.z, world.random.nextFloat() * 360f, 0f);
    setup.accept(mob);
    return world.spawnEntity(mob) ? Optional.of(mob) : Optional.empty();
  }
}
