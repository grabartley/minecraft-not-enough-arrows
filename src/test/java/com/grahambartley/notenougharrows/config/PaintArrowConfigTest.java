package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class PaintArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final PaintArrowConfig defaults = PaintArrowConfig.defaults();

    assertEquals(PaintArrowConfig.DEFAULT_ENABLED, defaults.enabled());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final PaintArrowConfig defaults = PaintArrowConfig.defaults();

    assertEquals(defaults, new PaintArrowConfig(defaults.enabled()));
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(PaintArrowConfig.defaults(), PaintArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(PaintArrowConfig.defaults(), PaintArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final PaintArrowConfig parsed =
        PaintArrowConfig.fromJson(JsonParser.parseString("{\"enabled\":false}").getAsJsonObject());

    assertEquals(false, parsed.enabled());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final PaintArrowConfig parsed =
        PaintArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":\"lots\"}").getAsJsonObject());

    assertEquals(PaintArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void roundTripsThroughJson() {
    final PaintArrowConfig original = new PaintArrowConfig(false);

    assertEquals(original, PaintArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final PaintArrowConfig original = PaintArrowConfig.defaults();

    final PaintArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final PaintArrowConfig updated = PaintArrowConfig.defaults().withEnabled(false);

    assertEquals(new PaintArrowConfig(false), updated);
  }
}
