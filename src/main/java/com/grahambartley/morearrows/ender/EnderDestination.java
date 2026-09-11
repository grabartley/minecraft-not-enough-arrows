package com.grahambartley.morearrows.ender;

import com.grahambartley.morearrows.world.Reach;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

public final class EnderDestination {

  private EnderDestination() {}

  public static boolean isReachable(
      @Nullable final WorldBorder border,
      @Nullable final Vec3d origin,
      @Nullable final Vec3d destination,
      final int maxRangeBlocks) {
    if (border == null || origin == null || destination == null) {
      return false;
    }
    return Reach.isWithin(origin, destination, maxRangeBlocks)
        && isInsideBorder(border, destination);
  }

  public static boolean isInsideBorder(
      @Nullable final WorldBorder border, @Nullable final Vec3d destination) {
    return border != null
        && destination != null
        && border.contains(BlockPos.ofFloored(destination));
  }
}
