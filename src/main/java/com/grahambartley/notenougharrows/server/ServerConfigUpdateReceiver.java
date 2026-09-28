package com.grahambartley.notenougharrows.server;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerConfigUpdateReceiver {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfigUpdateReceiver.class);

  private ServerConfigUpdateReceiver() {}

  public enum Outcome {
    APPLIED("config.not-enough-arrows.update.applied"),
    REJECTED("config.not-enough-arrows.update.rejected"),
    FAILED("config.not-enough-arrows.update.failed"),
    MALFORMED("config.not-enough-arrows.update.malformed");

    private final String messageKey;

    Outcome(final String messageKey) {
      this.messageKey = messageKey;
    }

    public String messageKey() {
      return messageKey;
    }
  }

  public static void register() {
    ServerPlayNetworking.registerGlobalReceiver(
        UpdateServerConfigC2SPayload.ID, ServerConfigUpdateReceiver::handle);
  }

  public static Outcome apply(
      final ServerPlayerEntity player, final Optional<NotEnoughArrowsConfig> config) {
    if (player == null) {
      return Outcome.FAILED;
    }
    if (!player.hasPermissionLevel(ServerConfigService.OP_PERMISSION_LEVEL)) {
      LOGGER.warn(
          "Rejected a Not Enough Arrows config update from {}, who is not an operator",
          player.getGameProfile().getName());
      ServerConfigService.syncTo(player);
      return Outcome.REJECTED;
    }
    if (config.isEmpty()) {
      LOGGER.warn(
          "Ignored a Not Enough Arrows config update from {} that could not be decoded",
          player.getGameProfile().getName());
      ServerConfigService.syncTo(player);
      return Outcome.MALFORMED;
    }
    if (!ServerConfigService.update(player.getServer(), config.get())) {
      ServerConfigService.syncTo(player);
      return Outcome.FAILED;
    }
    return Outcome.APPLIED;
  }

  private static void handle(
      final UpdateServerConfigC2SPayload payload, final ServerPlayNetworking.Context context) {
    final ServerPlayerEntity player = context.player();
    context
        .server()
        .execute(
            () ->
                player.sendMessage(
                    Text.translatable(apply(player, payload.config()).messageKey()), false));
  }
}
