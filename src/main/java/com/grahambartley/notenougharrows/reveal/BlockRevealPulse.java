package com.grahambartley.notenougharrows.reveal;

import com.grahambartley.notenougharrows.network.RevealPayloads.BlockOutlineS2CPayload;
import com.grahambartley.notenougharrows.world.LoadedGround;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class BlockRevealPulse {

  private BlockRevealPulse() {}

  public static List<BlockPos> fire(
      final ServerWorld world,
      final BlockPos center,
      final int radius,
      final int durationTicks,
      final Predicate<BlockState> reveals,
      @Nullable final Entity shooter) {
    final List<BlockPos> found = scan(world, center, radius, durationTicks, reveals);
    if (found.isEmpty()) {
      return found;
    }
    final BlockOutlineS2CPayload payload = new BlockOutlineS2CPayload(found, durationTicks);
    RevealAudience.around(world, center, shooter).stream()
        .filter(player -> ServerPlayNetworking.canSend(player, BlockOutlineS2CPayload.ID))
        .forEach(player -> ServerPlayNetworking.send(player, payload));
    return found;
  }

  public static List<BlockPos> scan(
      final ServerWorld world,
      final BlockPos center,
      final int radius,
      final int durationTicks,
      final Predicate<BlockState> reveals) {
    if (world == null || center == null || reveals == null || durationTicks <= 0) {
      return List.of();
    }
    return RevealScan.matching(
        center,
        radius,
        BlockOutlineS2CPayload.MAX_BLOCKS,
        LoadedGround.in(world),
        pos -> reveals.test(world.getBlockState(pos)));
  }
}
