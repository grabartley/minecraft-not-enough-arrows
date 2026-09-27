package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TripwireArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TripwireArrowConfig defaults = TripwireArrowConfig.defaults();

    assertEquals(TripwireArrowConfig.DEFAULT_LIFETIME_TICKS, defaults.lifetimeTicks());
    assertEquals(TripwireArrowConfig.DEFAULT_REPORT_INTERVAL_TICKS, defaults.reportIntervalTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TripwireArrowConfig defaults = TripwireArrowConfig.defaults();

    assertEquals(
        defaults,
        new TripwireArrowConfig(defaults.lifetimeTicks(), defaults.reportIntervalTicks()));
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "24000, 24000", "24001, 24000"})
  void clampsLifetimeTicks(final int given, final int expected) {
    assertEquals(expected, TripwireArrowConfig.defaults().withLifetimeTicks(given).lifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "1200, 1200", "1201, 1200"})
  void clampsReportIntervalTicks(final int given, final int expected) {
    assertEquals(
        expected,
        TripwireArrowConfig.defaults().withReportIntervalTicks(given).reportIntervalTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TripwireArrowConfig.defaults(), TripwireArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TripwireArrowConfig.defaults(), TripwireArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TripwireArrowConfig parsed =
        TripwireArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":23999}").getAsJsonObject());

    assertEquals(23999, parsed.lifetimeTicks());
    assertEquals(TripwireArrowConfig.DEFAULT_REPORT_INTERVAL_TICKS, parsed.reportIntervalTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final TripwireArrowConfig parsed =
        TripwireArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":24100}").getAsJsonObject());

    assertEquals(TripwireArrowConfig.LIFETIME_TICKS_MAX, parsed.lifetimeTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TripwireArrowConfig parsed =
        TripwireArrowConfig.fromJson(
            JsonParser.parseString("{\"lifetimeTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(TripwireArrowConfig.DEFAULT_LIFETIME_TICKS, parsed.lifetimeTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final TripwireArrowConfig original = new TripwireArrowConfig(23999, 1199);

    assertEquals(original, TripwireArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TripwireArrowConfig original = TripwireArrowConfig.defaults();

    final TripwireArrowConfig updated = original.withLifetimeTicks(23999);

    assertEquals(23999, updated.lifetimeTicks());
    assertEquals(original.reportIntervalTicks(), updated.reportIntervalTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TripwireArrowConfig updated =
        TripwireArrowConfig.defaults().withLifetimeTicks(23999).withReportIntervalTicks(1199);

    assertEquals(new TripwireArrowConfig(23999, 1199), updated);
  }
}
