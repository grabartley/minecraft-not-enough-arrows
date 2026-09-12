package com.grahambartley.notenougharrows.grapple;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class GrappleFlightCheck {

  private GrappleFlightCheck() {}

  public static void clearFloatingCountFor(@Nullable final ServerPlayerEntity player) {
    if (player == null || player.networkHandler == null) {
      return;
    }
    player.networkHandler.floatingTicks = 0;
  }
}
