package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.Identifier;

public final class GravityExclusions {
  private GravityExclusions() {}

  public enum Outcome {
    ADDED,
    REMOVED,
    CLEARED,
    INVALID_ID,
    ALREADY_PRESENT,
    NOT_PRESENT,
    LIST_FULL;

    public boolean succeeded() {
      return this == ADDED || this == REMOVED || this == CLEARED;
    }
  }

  public record Result(Outcome outcome, List<String> updated) {}

  public static Result add(final List<String> current, final String blockId) {
    final String normalized = normalize(blockId);
    if (normalized == null) {
      return new Result(Outcome.INVALID_ID, current);
    }
    if (current.contains(normalized)) {
      return new Result(Outcome.ALREADY_PRESENT, current);
    }
    if (current.size() >= PhysicsArrowConfig.GRAVITY_BLOCK_EXCLUSIONS_MAX) {
      return new Result(Outcome.LIST_FULL, current);
    }
    final List<String> updated = new ArrayList<>(current);
    updated.add(normalized);
    return new Result(Outcome.ADDED, List.copyOf(updated));
  }

  public static Result remove(final List<String> current, final String blockId) {
    final String normalized = normalize(blockId);
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

  public static Result clear(final List<String> current) {
    return new Result(Outcome.CLEARED, List.of());
  }

  public static String normalize(final String blockId) {
    if (blockId == null) {
      return null;
    }
    final Identifier identifier = Identifier.tryParse(blockId.trim().toLowerCase(Locale.ROOT));
    return identifier == null ? null : identifier.toString();
  }
}
