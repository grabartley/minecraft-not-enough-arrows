package com.grahambartley.notenougharrows.tint;

import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.util.Identifier;

public record TintChoice(String key, Identifier ingredient, int color, String labelKey) {
  public static final int MAX_COLOR = 0xFFFFFF;

  private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9_]+");

  public TintChoice {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(ingredient, "ingredient");
    Objects.requireNonNull(labelKey, "labelKey");

    if (!VALID_KEY.matcher(key).matches()) {
      throw new IllegalArgumentException(
          "Tint choice key must match " + VALID_KEY.pattern() + " but was '" + key + "'");
    }
    if (color < 0 || color > MAX_COLOR) {
      throw new IllegalArgumentException(
          "Tint choice colour must be an RGB value but was " + Integer.toHexString(color));
    }
    if (labelKey.isBlank()) {
      throw new IllegalArgumentException("Tint choice '" + key + "' must carry a label key");
    }
  }

  public ArrowChoice asComponent() {
    return new ArrowChoice(key);
  }
}
