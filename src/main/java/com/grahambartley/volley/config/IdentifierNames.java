package com.grahambartley.volley.config;

import java.util.Locale;
import java.util.Optional;
import net.minecraft.util.Identifier;

public final class IdentifierNames {
  private IdentifierNames() {}

  public static Optional<String> normalize(final String raw) {
    if (raw == null) {
      return Optional.empty();
    }
    final String trimmed = raw.trim().toLowerCase(Locale.ROOT);
    if (trimmed.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(Identifier.tryParse(trimmed)).map(Identifier::toString);
  }
}
