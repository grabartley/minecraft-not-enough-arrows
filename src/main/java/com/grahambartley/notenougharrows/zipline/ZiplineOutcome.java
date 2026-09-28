package com.grahambartley.notenougharrows.zipline;

import java.util.Optional;

public enum ZiplineOutcome {
  NOTHING(false, null),
  ANCHOR_SET(false, "anchored"),
  STRUNG(true, "strung"),
  TOO_FAR(false, "too_far"),
  TOO_SHORT(false, "too_short"),
  BLOCKED(false, "blocked");

  private static final String MESSAGE_PREFIX = "message.not-enough-arrows.zipline.";

  private final boolean spendsTheArrow;
  private final String message;

  ZiplineOutcome(final boolean spendsTheArrow, final String message) {
    this.spendsTheArrow = spendsTheArrow;
    this.message = message;
  }

  public static ZiplineOutcome of(final SpanRefusal refusal) {
    if (refusal == null) {
      return BLOCKED;
    }
    return switch (refusal) {
      case TOO_FAR -> TOO_FAR;
      case TOO_SHORT -> TOO_SHORT;
      case NOT_AN_ANCHOR, BLOCKED, NOT_STRUNG -> BLOCKED;
    };
  }

  public boolean spendsTheArrow() {
    return spendsTheArrow;
  }

  public Optional<String> messageKey() {
    return Optional.ofNullable(message).map(suffix -> MESSAGE_PREFIX + suffix);
  }
}
