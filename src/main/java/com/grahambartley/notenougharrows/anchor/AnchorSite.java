package com.grahambartley.notenougharrows.anchor;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class AnchorSite {

  private AnchorSite() {}

  public static boolean isSuitable(
      @Nullable final ServerWorld world, @Nullable final BlockPos pos) {
    if (world == null || pos == null || !world.isInBuildLimit(pos)) {
      return false;
    }

    final BlockState state = world.getBlockState(pos);
    if (state.isAir() || state.isReplaceable() || !state.getFluidState().isEmpty()) {
      return false;
    }
    return !state.getCollisionShape(world, pos).isEmpty();
  }

  @Nullable
  public static Identifier blockIdAt(
      @Nullable final ServerWorld world, @Nullable final BlockPos pos) {
    if (world == null || pos == null) {
      return null;
    }
    return Registries.BLOCK.getId(world.getBlockState(pos).getBlock());
  }
}
