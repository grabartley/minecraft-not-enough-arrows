package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.WebArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.world.BlockSphere;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class WebService {
  public static final BlockState MATERIAL = Blocks.COBWEB.getDefaultState();

  private WebService() {}

  public static List<BlockPos> spin(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return spin(world, center, shooter, ServerConfigService.get().terrain().web());
  }

  public static List<BlockPos> spin(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final WebArrowConfig web) {
    if (world == null || center == null || web == null || !web.enabled()) {
      return List.of();
    }
    final List<BlockPos> patch = BlockSphere.blocks(center, web.patchRadius());
    return TimedStructureService.build(
            world,
            shooter,
            patch,
            StructureBlockSource.of(MATERIAL),
            StructureBudget.of(patch.size()),
            web.lifetimeTicks())
        .map(TimedStructure::positions)
        .orElse(List.of());
  }
}
