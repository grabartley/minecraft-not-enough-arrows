package com.grahambartley.notenougharrows.updraft;

import java.util.Objects;

public record LiveUpdraft(UpdraftColumn column, UpdraftRiders riders) {

  public LiveUpdraft {
    Objects.requireNonNull(column, "column");
    Objects.requireNonNull(riders, "riders");
  }

  public static LiveUpdraft opening(final UpdraftColumn column) {
    return new LiveUpdraft(column, new UpdraftRiders());
  }
}
