package com.grahambartley.notenougharrows.chaos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InflationStepsTest {

  @Test
  void aTargetWithRoomDoublesInSize() {
    assertEquals(Optional.of(2.0), InflationSteps.largestThatFits(factor -> true));
  }

  @Test
  void aCrampedTargetGrowsOnlyAsFarAsTheSpaceAllows() {
    assertEquals(Optional.of(1.5), InflationSteps.largestThatFits(factor -> factor <= 1.5));
  }

  @Test
  void aTargetWithNoRoomAtAllDoesNotInflate() {
    assertTrue(InflationSteps.largestThatFits(factor -> false).isEmpty());
  }

  @Test
  void triesTheLargestSizeFirstAndNeverShrinks() {
    final List<Double> tried = new ArrayList<>();
    InflationSteps.largestThatFits(
        factor -> {
          tried.add(factor);
          return false;
        });

    assertEquals(InflationSteps.FACTORS, tried);
    for (int i = 1; i < tried.size(); i++) {
      assertTrue(tried.get(i) < tried.get(i - 1));
      assertTrue(tried.get(i) > 1.0);
    }
  }
}
