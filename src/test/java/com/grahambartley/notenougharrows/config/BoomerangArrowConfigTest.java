package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class BoomerangArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final BoomerangArrowConfig defaults = BoomerangArrowConfig.defaults();

    assertEquals(BoomerangArrowConfig.DEFAULT_ENABLED, defaults.enabled());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final BoomerangArrowConfig defaults = BoomerangArrowConfig.defaults();

    assertEquals(defaults, new BoomerangArrowConfig(defaults.enabled()));
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(BoomerangArrowConfig.defaults(), BoomerangArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(BoomerangArrowConfig.defaults(), BoomerangArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final BoomerangArrowConfig parsed =
        BoomerangArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":false}").getAsJsonObject());

    assertEquals(false, parsed.enabled());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final BoomerangArrowConfig parsed =
        BoomerangArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":\"lots\"}").getAsJsonObject());

    assertEquals(BoomerangArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void roundTripsThroughJson() {
    final BoomerangArrowConfig original = new BoomerangArrowConfig(false);

    assertEquals(original, BoomerangArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final BoomerangArrowConfig original = BoomerangArrowConfig.defaults();

    final BoomerangArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final BoomerangArrowConfig updated = BoomerangArrowConfig.defaults().withEnabled(false);

    assertEquals(new BoomerangArrowConfig(false), updated);
  }
}
