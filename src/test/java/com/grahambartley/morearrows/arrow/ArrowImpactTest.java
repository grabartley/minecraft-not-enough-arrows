package com.grahambartley.morearrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArrowImpactTest {

  @ParameterizedTest
  @CsvSource({
    "DEFAULT, true, false",
    "DISCARD, false, true",
    "RETAIN, false, false",
  })
  void exposesTheResolutionAndRemovalItPromises(
      final ArrowImpact impact, final boolean runsVanillaResolution, final boolean removesArrow) {
    assertEquals(runsVanillaResolution, impact.runsVanillaResolution());
    assertEquals(removesArrow, impact.removesArrow());
  }

  @ParameterizedTest
  @CsvSource({"DISCARD", "RETAIN"})
  void suppressesVanillaResolutionForEveryNonDefaultImpact(final ArrowImpact impact) {
    assertEquals(false, impact.runsVanillaResolution());
  }
}
