package com.grahambartley.notenougharrows.terrain;

import com.grahambartley.notenougharrows.config.PaintArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.world.BlockEditPermission;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class PaintService {

  private PaintService() {}

  public static boolean paintBlock(
      final ServerWorld world,
      final BlockPos pos,
      final DyeColor colour,
      @Nullable final PlayerEntity shooter) {
    return paintBlock(world, pos, colour, shooter, ServerConfigService.get().terrain().paint());
  }

  public static boolean paintBlock(
      final ServerWorld world,
      final BlockPos pos,
      final DyeColor colour,
      @Nullable final PlayerEntity shooter,
      final PaintArrowConfig paint) {
    if (world == null || pos == null || colour == null || paint == null || !paint.enabled()) {
      return false;
    }
    if (!world.isInBuildLimit(pos) || !BlockEditPermission.allows(world, pos, shooter)) {
      return false;
    }
    final BlockState state = world.getBlockState(pos);
    final Optional<Block> painted =
        PaintRecolour.recoloured(Registries.BLOCK.getId(state.getBlock()), colour.getName())
            .flatMap(Registries.BLOCK::getOrEmpty);
    return painted
        .map(block -> world.setBlockState(pos, block.getStateWithProperties(state)))
        .orElse(false);
  }

  public static boolean paintSheep(
      final SheepEntity sheep, final DyeColor colour, @Nullable final PlayerEntity shooter) {
    return paintSheep(sheep, colour, shooter, ServerConfigService.get().terrain().paint());
  }

  public static boolean paintSheep(
      final SheepEntity sheep,
      final DyeColor colour,
      @Nullable final PlayerEntity shooter,
      final PaintArrowConfig paint) {
    if (sheep == null || colour == null || paint == null || !paint.enabled()) {
      return false;
    }
    if (!sheep.isAlive() || sheep.isSheared() || sheep.getColor() == colour) {
      return false;
    }
    if (!(sheep.getWorld() instanceof ServerWorld world)
        || !BlockEditPermission.allows(world, sheep.getBlockPos(), shooter)) {
      return false;
    }
    sheep.setColor(colour);
    return true;
  }
}
