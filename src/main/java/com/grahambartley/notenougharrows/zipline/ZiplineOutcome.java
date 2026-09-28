package com.grahambartley.notenougharrows.zipline;

public enum ZiplineOutcome {
  NOTHING(false),
  ANCHOR_SET(false),
  STRUNG(true),
  TOO_FAR(false),
  TOO_SHORT(false),
  BLOCKED(false);

  private final boolean spendsTheArrow;

  ZiplineOutcome(final boolean spendsTheArrow) {
    this.spendsTheArrow = spendsTheArrow;
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
}
