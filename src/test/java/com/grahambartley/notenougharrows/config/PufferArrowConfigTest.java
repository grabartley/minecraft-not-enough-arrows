package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PufferArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final PufferArrowConfig defaults = PufferArrowConfig.defaults();

    assertEquals(PufferArrowConfig.DEFAULT_ENABLED, defaults.enabled());
    assertEquals(PufferArrowConfig.DEFAULT_DURATION_TICKS, defaults.durationTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final PufferArrowConfig defaults = PufferArrowConfig.defaults();

    assertEquals(defaults, new PufferArrowConfig(defaults.enabled(), defaults.durationTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsDurationTicks(final int given, final int expected) {
    assertEquals(expected, PufferArrowConfig.defaults().withDurationTicks(given).durationTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(PufferArrowConfig.defaults(), PufferArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(PufferArrowConfig.defaults(), PufferArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final PufferArrowConfig parsed =
        PufferArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":1199}").getAsJsonObject());

    assertEquals(1199, parsed.durationTicks());
    assertEquals(PufferArrowConfig.DEFAULT_ENABLED, parsed.enabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final PufferArrowConfig parsed =
        PufferArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":1300}").getAsJsonObject());

    assertEquals(PufferArrowConfig.DURATION_TICKS_MAX, parsed.durationTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final PufferArrowConfig parsed =
        PufferArrowConfig.fromJson(
            JsonParser.parseString("{\"durationTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(PufferArrowConfig.DEFAULT_DURATION_TICKS, parsed.durationTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final PufferArrowConfig original = new PufferArrowConfig(false, 1199);

    assertEquals(original, PufferArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final PufferArrowConfig original = PufferArrowConfig.defaults();

    final PufferArrowConfig updated = original.withEnabled(false);

    assertEquals(false, updated.enabled());
    assertEquals(original.durationTicks(), updated.durationTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final PufferArrowConfig updated =
        PufferArrowConfig.defaults().withEnabled(false).withDurationTicks(1199);

    assertEquals(new PufferArrowConfig(false, 1199), updated);
  }
}
