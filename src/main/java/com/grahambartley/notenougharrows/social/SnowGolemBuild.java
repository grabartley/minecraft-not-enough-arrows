package com.grahambartley.notenougharrows.social;

import com.grahambartley.notenougharrows.world.SpawnPermission;
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
    if (!allows(world, at, shooter)) {
      return Optional.empty();
    }
    final SnowGolemEntity golem = EntityType.SNOW_GOLEM.create(world);
    if (golem == null) {
      return Optional.empty();
    }
    golem.refreshPositionAndAngles(at.x, at.y, at.z, world.random.nextFloat() * 360f, 0f);
    SnowGolemMelt.schedule(golem, world.getTime() + lifetimeTicks);
    return world.spawnEntity(golem) ? Optional.of(golem) : Optional.empty();
  }
}
