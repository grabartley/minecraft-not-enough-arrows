package com.grahambartley.notenougharrows.world;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class SpawnPermission {

  private SpawnPermission() {}

  public static boolean allows(
      final ServerWorld world,
      final EntityType<?> type,
      final Vec3d at,
      @Nullable final PlayerEntity shooter) {
    if (world == null || type == null || at == null) {
      return false;
    }
    final BlockPos pos = BlockPos.ofFloored(at);
    if (!world.isInBuildLimit(pos) || !world.getWorldBorder().contains(pos)) {
      return false;
    }
    if (shooter != null && (!shooter.canModifyBlocks() || !world.canPlayerModifyAt(shooter, pos))) {
      return false;
    }
    return world.isSpaceEmpty(type.getSpawnBox(at.x, at.y, at.z));
  }
}
