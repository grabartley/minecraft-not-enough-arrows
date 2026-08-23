package com.grahambartley.morearrows.compat.info;

import java.util.Objects;
import net.minecraft.util.Identifier;

public final class InfoKeys {
  public static final String PREFIX = "info.";
  public static final String FIRING_KEY = "info.more-arrows.shared.firing";

  private InfoKeys() {}

  public static String description(final Identifier itemId) {
    Objects.requireNonNull(itemId, "itemId");
    return PREFIX + itemId.getNamespace() + "." + itemId.getPath();
  }
}
