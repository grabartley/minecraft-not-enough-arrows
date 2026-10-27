package com.grahambartley.volley.discovery;

import com.grahambartley.volley.ModBlocks;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.structure.OpenRun;
import com.grahambartley.volley.structure.StructureBlockSource;
import com.grahambartley.volley.structure.TimedStructureService;
import com.grahambartley.volley.world.VerticalRun;
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
    return TimedStructureService.placeAll(
        world, shooter, column, StructureBlockSource.of(beam), lifetimeTicks);
  }
}
