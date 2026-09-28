package com.grahambartley.notenougharrows.agriculture;

import com.grahambartley.notenougharrows.config.TillArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.terrain.SphereSweep;
import com.grahambartley.notenougharrows.world.BlockDisc;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public final class TillService {
  private static final Set<Block> TILLABLE =
      Set.of(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT);
  private static final BlockState HYDRATED =
      Blocks.FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, FarmlandBlock.MAX_MOISTURE);

  private TillService() {}

  public static List<BlockPos> till(
      final ServerWorld world, final BlockPos center, @Nullable final PlayerEntity shooter) {
    return till(world, center, shooter, ServerConfigService.get().agriculture().till());
  }

  public static List<BlockPos> till(
      final ServerWorld world,
      final BlockPos center,
      @Nullable final PlayerEntity shooter,
      final TillArrowConfig till) {
    if (world == null || center == null || till == null) {
      return List.of();
    }
    return SphereSweep.sweep(
        world,
        BlockDisc.blocks(center, till.radius()),
        Integer.MAX_VALUE,
        shooter,
        (target, pos) -> tillAt(target, pos, shooter));
  }

  private static boolean tillAt(
      final ServerWorld world, final BlockPos pos, @Nullable final PlayerEntity shooter) {
    final BlockState state = world.getBlockState(pos);
    final boolean tillable = TILLABLE.contains(state.getBlock());
    final boolean dryFarmland =
        state.isOf(Blocks.FARMLAND)
            && state.get(FarmlandBlock.MOISTURE) < FarmlandBlock.MAX_MOISTURE;
    if (!(tillable && world.getBlockState(pos.up()).isAir()) && !dryFarmland) {
      return false;
    }
    if (!world.setBlockState(pos, HYDRATED, Block.NOTIFY_ALL_AND_REDRAW)) {
      return false;
    }
    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(shooter, HYDRATED));
    return true;
  }
}
