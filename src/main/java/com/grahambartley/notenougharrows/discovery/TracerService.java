package com.grahambartley.notenougharrows.discovery;

import com.grahambartley.notenougharrows.network.RevealPayloads.TracerPathS2CPayload;
import com.grahambartley.notenougharrows.reveal.RevealAudience;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public final class TracerService {

  private TracerService() {}

  public static List<ServerPlayerEntity> draw(
      final Entity arrow, final TracerPath path, @Nullable final Entity shooter) {
    return draw(
        arrow, path, shooter, ServerConfigService.get().discovery().tracer().pathLifetimeTicks());
  }

  public static List<ServerPlayerEntity> draw(
      final Entity arrow,
      final TracerPath path,
      @Nullable final Entity shooter,
      final int lifetimeTicks) {
    if (arrow == null || path == null || !path.isDrawable() || lifetimeTicks <= 0) {
      return List.of();
    }
    final TracerPathS2CPayload payload = new TracerPathS2CPayload(path.points(), lifetimeTicks);
    final List<ServerPlayerEntity> audience = RevealAudience.watching(arrow, shooter);
    audience.forEach(player -> ServerPlayNetworking.send(player, payload));
    return audience;
  }
}
