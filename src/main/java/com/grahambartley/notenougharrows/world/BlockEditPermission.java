package com.grahambartley.notenougharrows.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class BlockEditPermission {

  private BlockEditPermission() {}

  public static boolean allows(
      @Nullable final ServerWorld world,
      @Nullable final BlockPos pos,
      @Nullable final PlayerEntity editor) {
    if (world == null || pos == null) {
      return false;
    }
    return editor == null
        ? world.getWorldBorder().contains(pos)
        : world.canPlayerModifyAt(editor, pos);
  }
}
