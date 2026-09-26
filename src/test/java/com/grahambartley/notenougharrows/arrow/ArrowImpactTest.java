package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class ArrowImpactTest {

  @ParameterizedTest
  @CsvSource({
    "DEFAULT, true, false",
    "CONSUME, true, true",
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
  void skipsVanillaResolutionWhereTheArrowResolvesTheHitItself(final ArrowImpact impact) {
    assertEquals(false, impact.runsVanillaResolution());
  }

  @ParameterizedTest
  @CsvSource({"DEFAULT", "CONSUME"})
  void keepsVanillaResolutionWhereTheArrowLandsAnOrdinaryHit(final ArrowImpact impact) {
    assertTrue(impact.runsVanillaResolution());
  }

  @ParameterizedTest
  @CsvSource({
    "DEFAULT, DISCARD",
    "CONSUME, DISCARD",
    "DISCARD, DISCARD",
    "RETAIN, RETAIN",
  })
  void sparingWhatItHitsSkipsVanillasHitButKeepsTheArrowsOwnChoice(
      final ArrowImpact impact, final ArrowImpact spared) {
    assertEquals(spared, impact.sparingWhatItHits());
  }

  @Test
  void offersAnOutcomeForEveryCombinationOfResolutionAndRemoval() {
    assertEquals(4, ArrowImpact.values().length);
  }

  @ParameterizedTest
  @EnumSource(ArrowImpact.class)
  void everyOutcomeIsDistinctInWhatItPromises(final ArrowImpact impact) {
    final long matching =
        java.util.Arrays.stream(ArrowImpact.values())
            .filter(
                other ->
                    other.runsVanillaResolution() == impact.runsVanillaResolution()
                        && other.removesArrow() == impact.removesArrow())
            .count();

    assertEquals(1, matching, impact + " shares its promises with another outcome");
  }
}
