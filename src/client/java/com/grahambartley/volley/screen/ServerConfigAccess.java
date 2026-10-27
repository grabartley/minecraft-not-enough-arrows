package com.grahambartley.volley.screen;

import com.grahambartley.volley.config.ConfigPermissions;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;

public enum ServerConfigAccess {
  EDITABLE(true, null),
  NOT_OPERATOR(false, "config.volley.access.not_operator"),
  NOT_IN_WORLD(false, "config.volley.access.not_in_world");

  private final boolean editable;
  private final String messageKey;

  ServerConfigAccess(final boolean editable, final String messageKey) {
    this.editable = editable;
    this.messageKey = messageKey;
  }

  public boolean editable() {
    return editable;
  }

  public Optional<String> messageKey() {
    return Optional.ofNullable(messageKey);
  }

  public static ServerConfigAccess of(final boolean inWorld, final boolean operator) {
    if (!inWorld) {
      return NOT_IN_WORLD;
    }
    return operator ? EDITABLE : NOT_OPERATOR;
  }

  public static ServerConfigAccess of(final MinecraftClient client) {
    if (client == null || client.player == null) {
      return NOT_IN_WORLD;
    }
    return of(true, ConfigPermissions.canEdit(client.player::hasPermissionLevel));
  }
}
