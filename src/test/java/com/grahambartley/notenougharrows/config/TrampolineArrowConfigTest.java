package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrampolineArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TrampolineArrowConfig defaults = TrampolineArrowConfig.defaults();

    assertEquals(TrampolineArrowConfig.DEFAULT_STRENGTH, defaults.strength());
    assertEquals(TrampolineArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TrampolineArrowConfig defaults = TrampolineArrowConfig.defaults();

    assertEquals(
        defaults, new TrampolineArrowConfig(defaults.strength(), defaults.lifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "4.0, 4.0", "5.0, 4.0"})
  void clampsStrength(final float given, final float expected) {
    assertEquals(expected, TrampolineArrowConfig.defaults().withStrength(given).strength());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected, TrampolineArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        TrampolineArrowConfig.defaults(), TrampolineArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TrampolineArrowConfig.defaults(), TrampolineArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TrampolineArrowConfig parsed =
        TrampolineArrowConfig.fromJson(
            JsonParser.parseString("{\"strength\":3.5}").getAsJsonObject());

    assertEquals(3.5f, parsed.strength());
    assertEquals(TrampolineArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final TrampolineArrowConfig parsed =
        TrampolineArrowConfig.fromJson(
            JsonParser.parseString("{\"strength\":104.0}").getAsJsonObject());

    assertEquals(TrampolineArrowConfig.STRENGTH_MAX, parsed.strength());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TrampolineArrowConfig parsed =
        TrampolineArrowConfig.fromJson(
            JsonParser.parseString("{\"strength\":\"lots\"}").getAsJsonObject());

    assertEquals(TrampolineArrowConfig.DEFAULT_STRENGTH, parsed.strength());
  }

  @Test
  void roundTripsThroughJson() {
    final TrampolineArrowConfig original = new TrampolineArrowConfig(3.5f, 11999);

    assertEquals(original, TrampolineArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TrampolineArrowConfig original = TrampolineArrowConfig.defaults();

    final TrampolineArrowConfig updated = original.withStrength(3.5f);

    assertEquals(3.5f, updated.strength());
    assertEquals(original.lifetimeTicks(), updated.lifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TrampolineArrowConfig updated =
        TrampolineArrowConfig.defaults().withStrength(3.5f).withLifetimeTicks(11999);

    assertEquals(new TrampolineArrowConfig(3.5f, 11999), updated);
  }
}
