package com.grahambartley.notenougharrows.traversal;

import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.world.LoadedGround;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public final class OpenRun {

  private OpenRun() {}

  public static List<BlockPos> leading(
      final ServerWorld world, final List<BlockPos> candidates, final BlockState material) {
    final Predicate<BlockPos> isLoaded = LoadedGround.in(world);
    final List<BlockPos> open = new ArrayList<>();
    for (final BlockPos pos : candidates) {
      if (!isOpen(world, pos, material, isLoaded)) {
        break;
      }
      open.add(pos);
    }
    return List.copyOf(open);
  }

  public static boolean isOpen(
      final ServerWorld world,
      final BlockPos pos,
      final BlockState material,
      final Predicate<BlockPos> isLoaded) {
    if (!isLoaded.test(pos) || !world.isInBuildLimit(pos)) {
      return false;
    }
    final BlockState current = world.getBlockState(pos);
    return current.isReplaceable()
        && current.getFluidState().isEmpty()
        && !TimedStructureService.holds(world, pos)
        && world.canPlace(material, pos, ShapeContext.absent());
  }
}
