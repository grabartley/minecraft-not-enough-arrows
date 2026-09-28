package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.PillarArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.world.BlockPlacement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class PillarService {
  public static final BlockState MATERIAL = Blocks.DIRT.getDefaultState();

  private PillarService() {}

  public static List<BlockPos> raise(
      final ServerWorld world, final BlockPos struck, @Nullable final PlayerEntity shooter) {
    return raise(world, struck, shooter, ServerConfigService.get().terrain().pillar());
  }

  public static List<BlockPos> raise(
      final ServerWorld world,
      final BlockPos struck,
      @Nullable final PlayerEntity shooter,
      final PillarArrowConfig pillar) {
    if (world == null || struck == null || pillar == null || !pillar.enabled()) {
      return List.of();
    }
    final List<BlockPos> column = openRun(world, PillarColumn.above(struck, pillar.heightBlocks()));
    return TimedStructureService.build(
            world,
            shooter,
            column,
            StructureBlockSource.of(MATERIAL),
            StructureBudget.of(column.size()),
            pillar.lifetimeTicks())
        .map(TimedStructure::positions)
        .orElse(List.of());
  }

  private static List<BlockPos> openRun(final ServerWorld world, final List<BlockPos> column) {
    final List<BlockPos> open = new ArrayList<>();
    for (final BlockPos pos : column) {
      if (!BlockPlacement.canOccupy(world, pos, MATERIAL)
          || !world.canPlace(MATERIAL, pos, ShapeContext.absent())) {
        break;
      }
      open.add(pos);
    }
    return open;
  }
}
