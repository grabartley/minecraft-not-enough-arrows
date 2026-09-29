package com.grahambartley.notenougharrows.reveal;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class RevealAudience {

  private RevealAudience() {}

  public static List<ServerPlayerEntity> around(
      final ServerWorld world, final BlockPos pos, @Nullable final Entity shooter) {
    return withShooter(world, PlayerLookup.tracking(world, pos), shooter);
  }

  public static List<ServerPlayerEntity> watching(
      final Entity tracked, @Nullable final Entity shooter) {
    if (!(tracked.getWorld() instanceof ServerWorld world)) {
      return List.of();
    }
    return withShooter(world, PlayerLookup.tracking(tracked), shooter);
  }

  private static List<ServerPlayerEntity> withShooter(
      final ServerWorld world,
      final Iterable<ServerPlayerEntity> tracking,
      @Nullable final Entity shooter) {
    final Set<ServerPlayerEntity> audience = new LinkedHashSet<>();
    tracking.forEach(audience::add);
    if (shooter instanceof ServerPlayerEntity player && player.getWorld() == world) {
      audience.add(player);
    }
    return List.copyOf(audience);
  }
}
