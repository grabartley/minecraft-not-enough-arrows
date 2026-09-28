package com.grahambartley.notenougharrows.tint;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public final class TintPalette {
  private final Map<String, TintChoice> byKey = new LinkedHashMap<>();
  private final TintChoice fallback;

  public TintPalette(final List<TintChoice> choices, final String fallbackKey) {
    Objects.requireNonNull(choices, "choices");
    Objects.requireNonNull(fallbackKey, "fallbackKey");

    for (final TintChoice choice : choices) {
      if (byKey.putIfAbsent(Objects.requireNonNull(choice, "choice").key(), choice) != null) {
        throw new IllegalArgumentException("Tint choice '" + choice.key() + "' is listed twice");
      }
    }
    if (byKey.isEmpty()) {
      throw new IllegalArgumentException("A tint palette must offer at least one choice");
    }
    this.fallback = byKey.get(fallbackKey);
    if (fallback == null) {
      throw new IllegalArgumentException(
          "Fallback '" + fallbackKey + "' is not one of " + byKey.keySet());
    }
  }

  public List<TintChoice> choices() {
    return List.copyOf(byKey.values());
  }

  public TintChoice fallback() {
    return fallback;
  }

  public Optional<TintChoice> find(final String key) {
    return Optional.ofNullable(byKey.get(key));
  }

  public TintChoice resolve(@Nullable final ArrowChoice choice) {
    return choice == null ? fallback : byKey.getOrDefault(choice.key(), fallback);
  }
}
