package com.grahambartley.notenougharrows.traversal;

import com.grahambartley.notenougharrows.config.BridgeArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class BridgeService {
  public static final BlockState MATERIAL = Blocks.OAK_PLANKS.getDefaultState();

  private BridgeService() {}

  public static List<BlockPos> lay(
      final ServerWorld world,
      final BlockPos struck,
      final Vec3d toward,
      @Nullable final PlayerEntity shooter) {
    return lay(world, struck, toward, shooter, ServerConfigService.get().traversal().bridge());
  }

  public static List<BlockPos> lay(
      final ServerWorld world,
      final BlockPos struck,
      final Vec3d toward,
      @Nullable final PlayerEntity shooter,
      final BridgeArrowConfig bridge) {
    if (world == null
        || struck == null
        || toward == null
        || bridge == null
        || bridge.lifetimeTicks() <= 0) {
      return List.of();
    }
    final List<BlockPos> walkway =
        OpenRun.leading(
            world,
            BridgeLine.toward(struck, toward.getX(), toward.getZ(), bridge.lengthBlocks()),
            MATERIAL);
    return TimedStructureService.build(
            world,
            shooter,
            walkway,
            StructureBlockSource.of(MATERIAL),
            StructureBudget.of(walkway.size()),
            bridge.lifetimeTicks())
        .map(TimedStructure::positions)
        .orElse(List.of());
  }
}
