package com.grahambartley.volley.terrain;

import com.grahambartley.volley.config.PillarArrowConfig;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.structure.StructureBlockSource;
import com.grahambartley.volley.structure.TimedStructureService;
import com.grahambartley.volley.traversal.OpenRun;
import com.grahambartley.volley.traversal.VerticalRun;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
    final List<BlockPos> column =
        OpenRun.leading(world, VerticalRun.upFrom(struck.up(), pillar.heightBlocks()), MATERIAL);
    return TimedStructureService.placeAll(
        world, shooter, column, StructureBlockSource.of(MATERIAL), pillar.lifetimeTicks());
  }
}
