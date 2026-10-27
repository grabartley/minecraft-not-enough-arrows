package com.grahambartley.volley.server;

import com.grahambartley.volley.config.ConfigPermissions;
import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
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
    APPLIED("config.volley.update.applied"),
    REJECTED("config.volley.update.rejected"),
    FAILED("config.volley.update.failed"),
    MALFORMED("config.volley.update.malformed");

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
      final ServerPlayerEntity player, final Optional<VolleyConfig> config) {
    if (player == null) {
      return Outcome.FAILED;
    }
    if (!ConfigPermissions.canEdit(player::hasPermissionLevel)) {
      LOGGER.warn(
          "Rejected a Volley config update from {}, who is not an operator",
          player.getGameProfile().getName());
      ServerConfigService.syncTo(player);
      return Outcome.REJECTED;
    }
    if (config.isEmpty()) {
      LOGGER.warn(
          "Ignored a Volley config update from {} that could not be decoded",
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
