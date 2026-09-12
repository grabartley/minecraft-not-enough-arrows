package com.grahambartley.notenougharrows.grapple;

import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class GrappleArrival {

  private GrappleArrival() {}

  public static boolean cancelsFallDamage(
      @Nullable final GrappleEnding ending, @Nullable final GrappleArrowConfig config) {
    return ending != null
        && config != null
        && ending.ownsTheFall()
        && config.cancelFallDamageOnArrival();
  }

  public static boolean returnsArrow(
      @Nullable final GrappleEnding ending, @Nullable final GrappleArrowConfig config) {
    return ending != null
        && config != null
        && ending.returnsTheArrow()
        && config.returnArrowOnArrival();
  }

  public static void settle(
      @Nullable final ServerWorld world,
      @Nullable final GrappleSession session,
      @Nullable final GrappleEnding ending) {
    if (world == null
        || session == null
        || !(world.getEntity(session.playerId()) instanceof ServerPlayerEntity player)) {
      return;
    }

    final GrappleArrowConfig config = ServerConfigService.get().grapple();
    if (cancelsFallDamage(ending, config)) {
      player.onLanding();
      GrappleFallGuard.spare(player.getUuid());
    }
    if (returnsArrow(ending, config) && session.arrowId() != null) {
      GrappleArrowReturn.toShooter(player, world.getEntity(session.arrowId()));
    }
  }
}
