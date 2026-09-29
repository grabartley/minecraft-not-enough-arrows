package com.grahambartley.notenougharrows.discovery;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.traversal.OpenRun;
import com.grahambartley.notenougharrows.traversal.VerticalRun;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class BeaconService {
  public static final int BEAM_HEIGHT_BLOCKS = 64;

  private BeaconService() {}

  public static List<BlockPos> raise(
      final ServerWorld world, final BlockPos base, @Nullable final PlayerEntity shooter) {
    return raise(
        world, base, shooter, ServerConfigService.get().discovery().beacon().lifetimeTicks());
  }

  public static List<BlockPos> raise(
      final ServerWorld world,
      final BlockPos base,
      @Nullable final PlayerEntity shooter,
      final int lifetimeTicks) {
    if (world == null || base == null || lifetimeTicks <= 0) {
      return List.of();
    }
    final BlockState beam = ModBlocks.BEACON_BEAM.getDefaultState();
    final List<BlockPos> column =
        OpenRun.leading(world, VerticalRun.upFrom(base, BEAM_HEIGHT_BLOCKS), beam).stream()
            .takeWhile(pos -> world.getBlockState(pos).isAir())
            .toList();
    return TimedStructureService.build(
            world,
            shooter,
            column,
            StructureBlockSource.of(beam),
            StructureBudget.of(column.size()),
            lifetimeTicks)
        .map(TimedStructure::positions)
        .orElse(List.of());
  }
}
