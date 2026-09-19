package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LifestealArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.5, 0.5", "1.0, 1.0", "2.0, 1.0"})
  void clampsShareToAFraction(final float given, final float expected) {
    assertEquals(expected, new LifestealArrowConfig(given, 4.0f).share());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "4.0, 4.0", "20.0, 20.0", "99.0, 20.0"})
  void clampsTheCapPerHit(final float given, final float expected) {
    assertEquals(expected, new LifestealArrowConfig(0.5f, given).maxHealPerHit());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(LifestealArrowConfig.defaults(), LifestealArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final LifestealArrowConfig parsed =
        LifestealArrowConfig.fromJson(JsonParser.parseString("{\"share\":0.25}").getAsJsonObject());

    assertEquals(0.25f, parsed.share());
    assertEquals(LifestealArrowConfig.DEFAULT_MAX_HEAL_PER_HIT, parsed.maxHealPerHit());
  }

  @Test
  void roundTripsThroughJson() {
    final LifestealArrowConfig original = new LifestealArrowConfig(0.75f, 6.5f);

    assertEquals(original, LifestealArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheOtherAlone() {
    final LifestealArrowConfig updated = LifestealArrowConfig.defaults().withShare(0.1f);

    assertEquals(0.1f, updated.share());
    assertEquals(LifestealArrowConfig.DEFAULT_MAX_HEAL_PER_HIT, updated.maxHealPerHit());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        LifestealArrowConfig.SHARE_MAX, LifestealArrowConfig.defaults().withShare(9.0f).share());
  }

  @Test
  void everyLifestealFieldCanBeChangedOnItsOwn() {
    assertEquals(
        new LifestealArrowConfig(0.2f, 1.0f),
        LifestealArrowConfig.defaults().withShare(0.2f).withMaxHealPerHit(1.0f));
  }
}
