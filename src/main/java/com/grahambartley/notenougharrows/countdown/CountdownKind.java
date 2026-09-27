package com.grahambartley.notenougharrows.countdown;

public enum CountdownKind {
  FUSE,
  ALLEGIANCE;

  public boolean showsOnlyWhenLookedAt() {
    return this == FUSE;
  }

  public boolean sitsAboveTheHead() {
    return this == ALLEGIANCE;
  }

  public static CountdownKind fromOrdinal(final int ordinal) {
    final CountdownKind[] kinds = values();
    return ordinal >= 0 && ordinal < kinds.length ? kinds[ordinal] : FUSE;
  }
}
