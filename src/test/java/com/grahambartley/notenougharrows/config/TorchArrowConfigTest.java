package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class TorchArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TorchArrowConfig defaults = TorchArrowConfig.defaults();

    assertEquals(TorchArrowConfig.DEFAULT_ENABLED, defaults.enabled());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TorchArrowConfig defaults = TorchArrowConfig.defaults();

    assertEquals(defaults, new TorchArrowConfig(defaults.enabled()));
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TorchArrowConfig.defaults(), TorchArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TorchArrowConfig.defaults(), TorchArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TorchArrowConfig parsed =
        TorchArrowConfig.fromJson(JsonParser.parseString("{\"enabled\":false}").getAsJsonObject());

    assertEquals(false, parsed.enabled());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TorchArrowConfig parsed =
        TorchArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":\"lots\"}").getAsJsonObject());

    assertEquals(TorchArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void roundTripsThroughJson() {
    final TorchArrowConfig original = new TorchArrowConfig(false);

    assertEquals(original, TorchArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TorchArrowConfig original = TorchArrowConfig.defaults();

    final TorchArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TorchArrowConfig updated = TorchArrowConfig.defaults().withEnabled(false);

    assertEquals(new TorchArrowConfig(false), updated);
  }
}
