package com.grahambartley.notenougharrows.traversal;

import com.grahambartley.notenougharrows.ModBlocks;
import com.grahambartley.notenougharrows.config.TrampolineArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.structure.StructureBlock;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class TrampolineService {

  private TrampolineService() {}

  public static List<BlockPos> place(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return place(world, center, shooter, ServerConfigService.get().traversal().trampoline());
  }

  public static List<BlockPos> place(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final TrampolineArrowConfig trampoline) {
    if (world == null || center == null || trampoline == null || trampoline.lifetimeTicks() <= 0) {
      return List.of();
    }
    final List<BlockPos> pad = TrampolinePad.around(center);
    return TimedStructureService.build(
            world,
            shooter,
            pad,
            TrampolineService::padBlock,
            StructureBudget.of(pad.size()),
            trampoline.lifetimeTicks())
        .map(TimedStructure::positions)
        .orElse(List.of());
  }

  @Nullable
  private static StructureBlock padBlock(final ServerWorld world, final BlockPos pos) {
    final BlockState pad = ModBlocks.TRAMPOLINE.getDefaultState();
    return world.canPlace(pad, pos, ShapeContext.absent()) ? new StructureBlock(pos, pad) : null;
  }
}
