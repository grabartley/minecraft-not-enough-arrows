package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class PartyArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final PartyArrowConfig defaults = PartyArrowConfig.defaults();

    assertEquals(PartyArrowConfig.DEFAULT_ENABLED, defaults.enabled());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final PartyArrowConfig defaults = PartyArrowConfig.defaults();

    assertEquals(defaults, new PartyArrowConfig(defaults.enabled()));
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(PartyArrowConfig.defaults(), PartyArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(PartyArrowConfig.defaults(), PartyArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final PartyArrowConfig parsed =
        PartyArrowConfig.fromJson(JsonParser.parseString("{\"enabled\":false}").getAsJsonObject());

    assertEquals(false, parsed.enabled());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final PartyArrowConfig parsed =
        PartyArrowConfig.fromJson(
            JsonParser.parseString("{\"enabled\":\"lots\"}").getAsJsonObject());

    assertEquals(PartyArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void roundTripsThroughJson() {
    final PartyArrowConfig original = new PartyArrowConfig(false);

    assertEquals(original, PartyArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final PartyArrowConfig original = PartyArrowConfig.defaults();

    final PartyArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final PartyArrowConfig updated = PartyArrowConfig.defaults().withEnabled(false);

    assertEquals(new PartyArrowConfig(false), updated);
  }
}
