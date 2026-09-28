package com.grahambartley.notenougharrows.structure;

public record StructureBudget(int maxPositions) {

  public StructureBudget {
    maxPositions = Math.max(0, maxPositions);
  }

  public static StructureBudget of(final int maxPositions) {
    return new StructureBudget(maxPositions);
  }

  public boolean hasRoomAfter(final int placed) {
    return placed < maxPositions;
  }

  public boolean isNone() {
    return maxPositions == 0;
  }
}
