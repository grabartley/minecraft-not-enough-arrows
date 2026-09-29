package com.grahambartley.notenougharrows.chaos;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class ChickenRelease {

  private ChickenRelease() {}

  public static boolean allows(
      final ServerWorld world, final Vec3d at, @Nullable final PlayerEntity shooter) {
    if (world == null || at == null) {
      return false;
    }
    final BlockPos pos = BlockPos.ofFloored(at);
    if (!world.isInBuildLimit(pos) || !world.getWorldBorder().contains(pos)) {
      return false;
    }
    if (shooter != null && (!shooter.canModifyBlocks() || !world.canPlayerModifyAt(shooter, pos))) {
      return false;
    }
    return world.isSpaceEmpty(EntityType.CHICKEN.getSpawnBox(at.x, at.y, at.z));
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
