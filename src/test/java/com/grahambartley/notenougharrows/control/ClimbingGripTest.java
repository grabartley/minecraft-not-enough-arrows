package com.grahambartley.notenougharrows.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ClimbingGripTest {

  @ParameterizedTest
  @CsvSource({"9.0, 3.0, true", "3.0, 9.0, false", "3.0, 3.0, false"})
  void letsGoOfAWallOnlyWhenClimbingAboveWhereItIsHeaded(
      final double climberY, final double destinationY, final boolean expected) {
    assertEquals(expected, ClimbingGrip.shouldLetGo(climberY, destinationY));
  }
}
