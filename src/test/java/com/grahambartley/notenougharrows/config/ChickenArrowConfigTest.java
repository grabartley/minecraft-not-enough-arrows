package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class ChickenArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final ChickenArrowConfig defaults = ChickenArrowConfig.defaults();

    assertEquals(ChickenArrowConfig.DEFAULT_ENABLED, defaults.enabled());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final ChickenArrowConfig defaults = ChickenArrowConfig.defaults();

    assertEquals(defaults, new ChickenArrowConfig(defaults.enabled()));
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(ChickenArrowConfig.defaults(), ChickenArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(ChickenArrowConfig.defaults(), ChickenArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ChickenArrowConfig parsed =
        ChickenArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":false}").getAsJsonObject());

    assertEquals(false, parsed.enabled());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final ChickenArrowConfig parsed =
        ChickenArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":\"lots\"}").getAsJsonObject());

    assertEquals(ChickenArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void roundTripsThroughJson() {
    final ChickenArrowConfig original = new ChickenArrowConfig(false);

    assertEquals(original, ChickenArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final ChickenArrowConfig original = ChickenArrowConfig.defaults();

    final ChickenArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final ChickenArrowConfig updated = ChickenArrowConfig.defaults().withEnabled(false);

    assertEquals(new ChickenArrowConfig(false), updated);
  }
}
