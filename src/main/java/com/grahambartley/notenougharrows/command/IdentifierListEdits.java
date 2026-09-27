package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.Identifier;

public final class IdentifierListEdits {
  private IdentifierListEdits() {}

  public enum Outcome {
    ADDED,
    REMOVED,
    CLEARED,
    INVALID_ID,
    TOO_LONG,
    ALREADY_PRESENT,
    NOT_PRESENT,
    LIST_FULL;

    public boolean succeeded() {
      return this == ADDED || this == REMOVED || this == CLEARED;
    }
  }

  public record Result(Outcome outcome, List<String> updated) {}

  public static Result add(final List<String> current, final String id, final int maxEntries) {
    final String normalized = normalize(id);
    if (normalized == null) {
      return new Result(Outcome.INVALID_ID, current);
    }
    if (normalized.length() > ConfigValues.MAX_IDENTIFIER_LENGTH) {
      return new Result(Outcome.TOO_LONG, current);
    }
    if (current.contains(normalized)) {
      return new Result(Outcome.ALREADY_PRESENT, current);
    }
    if (current.size() >= maxEntries) {
      return new Result(Outcome.LIST_FULL, current);
    }
    final List<String> updated = new ArrayList<>(current);
    updated.add(normalized);
    return new Result(Outcome.ADDED, List.copyOf(updated));
  }

  public static Result remove(final List<String> current, final String id) {
    final String normalized = normalize(id);
    if (normalized == null) {
      return new Result(Outcome.INVALID_ID, current);
    }
    if (!current.contains(normalized)) {
      return new Result(Outcome.NOT_PRESENT, current);
    }
    final List<String> updated = new ArrayList<>(current);
    updated.remove(normalized);
    return new Result(Outcome.REMOVED, List.copyOf(updated));
  }

  public static Result clear() {
    return new Result(Outcome.CLEARED, List.of());
  }

  public static String normalize(final String id) {
    if (id == null) {
      return null;
    }
    final Identifier identifier = Identifier.tryParse(id.trim().toLowerCase(Locale.ROOT));
    return identifier == null ? null : identifier.toString();
  }
}
