package com.grahambartley.notenougharrows.chaos;

import java.util.List;
import java.util.Optional;
import java.util.function.DoublePredicate;

public final class InflationSteps {
  public static final List<Double> FACTORS = List.of(2.0, 1.75, 1.5, 1.25);

  private InflationSteps() {}

  public static Optional<Double> largestThatFits(final DoublePredicate fits) {
    return FACTORS.stream().filter(fits::test).findFirst();
  }
}
