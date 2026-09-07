package com.grahambartley.morearrows.rope;

import com.grahambartley.morearrows.anchor.AnchorSite;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class RopeService {

  private RopeService() {}

  public static List<BlockPos> drop(
      final ServerWorld world, final BlockPos anchorPos, @Nullable final PlayerEntity shooter) {
    return drop(world, anchorPos, shooter, ServerConfigService.get().grapple().ropeLengthBlocks());
  }

  public static List<BlockPos> drop(
      final ServerWorld world,
      final BlockPos anchorPos,
      @Nullable final PlayerEntity shooter,
      final int maxLength) {
    if (!AnchorSite.isSuitable(world, anchorPos)) {
      return List.of();
    }
    return RopePlacer.place(world, RopeShape.below(anchorPos, maxLength), shooter);
  }
}
