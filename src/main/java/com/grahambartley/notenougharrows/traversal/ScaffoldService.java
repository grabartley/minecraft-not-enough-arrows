package com.grahambartley.notenougharrows.traversal;

import com.grahambartley.notenougharrows.config.ScaffoldArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.structure.StructureBlock;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.StructureRemoval;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class ScaffoldService {
  public static final BlockState MATERIAL = Blocks.SCAFFOLDING.getDefaultState();
  private static final int STANDING = 0;

  private ScaffoldService() {}

  public static List<BlockPos> raise(
      final ServerWorld world, final BlockPos impact, @Nullable final PlayerEntity shooter) {
    return raise(world, impact, shooter, ServerConfigService.get().traversal().scaffold());
  }

  public static List<BlockPos> raise(
      final ServerWorld world,
      final BlockPos impact,
      @Nullable final PlayerEntity shooter,
      final ScaffoldArrowConfig scaffold) {
    if (world == null || impact == null || scaffold == null || scaffold.lifetimeTicks() <= 0) {
      return List.of();
    }
    final Optional<BlockPos> footing = footing(world, impact, scaffold.heightBlocks());
    if (footing.isEmpty()) {
      return List.of();
    }
    final List<BlockPos> column =
        OpenRun.leading(
            world, VerticalRun.upFrom(footing.get(), scaffold.heightBlocks()), MATERIAL);
    return TimedStructureService.build(
            world,
            shooter,
            column,
            ScaffoldService::standingScaffold,
            StructureBudget.of(column.size()),
            scaffold.lifetimeTicks())
        .map(TimedStructure::positions)
        .orElse(List.of());
  }

  public static Optional<BlockPos> footing(
      final ServerWorld world, final BlockPos impact, final int maxDrop) {
    final Predicate<BlockPos> isLoaded = StructureRemoval.loadedIn(world);
    BlockPos candidate = impact;
    for (int dropped = 0; dropped <= maxDrop; dropped++) {
      if (!OpenRun.isOpen(world, candidate, MATERIAL, isLoaded)) {
        return Optional.empty();
      }
      if (ScaffoldingBlock.calculateDistance(world, candidate) == STANDING) {
        return Optional.of(candidate);
      }
      candidate = candidate.down();
    }
    return Optional.empty();
  }

  @Nullable
  private static StructureBlock standingScaffold(final ServerWorld world, final BlockPos pos) {
    return ScaffoldingBlock.calculateDistance(world, pos) == STANDING
        ? new StructureBlock(pos, MATERIAL.with(ScaffoldingBlock.DISTANCE, STANDING))
        : null;
  }
}
