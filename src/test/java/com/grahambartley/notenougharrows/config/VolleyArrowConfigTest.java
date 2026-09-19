package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VolleyArrowConfigTest {

  @ParameterizedTest
  @CsvSource({"0, 2", "1, 2", "5, 5", "12, 12", "99, 12"})
  void capsFragmentCountSoOneShotCannotFloodAServer(final int given, final int expected) {
    assertEquals(expected, new VolleyArrowConfig(given, 0.4f, 10.0f, 4).fragmentCount());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.4, 0.4", "1.0, 1.0", "2.0, 1.0"})
  void clampsDamageShareToAFraction(final float given, final float expected) {
    assertEquals(expected, new VolleyArrowConfig(5, given, 10.0f, 4).damageShare());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "10.0, 10.0", "45.0, 45.0", "90.0, 45.0"})
  void clampsSpread(final float given, final float expected) {
    assertEquals(expected, new VolleyArrowConfig(5, 0.4f, given, 4).spreadDegrees());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "-5, 1", "4, 4", "40, 40", "99, 40"})
  void keepsTheSplitDelayAtLeastOneTick(final int given, final int expected) {
    assertEquals(expected, new VolleyArrowConfig(5, 0.4f, 10.0f, given).splitDelayTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(VolleyArrowConfig.defaults(), VolleyArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final VolleyArrowConfig parsed =
        VolleyArrowConfig.fromJson(
            JsonParser.parseString("{\"fragmentCount\":9}").getAsJsonObject());

    assertEquals(9, parsed.fragmentCount());
    assertEquals(VolleyArrowConfig.DEFAULT_DAMAGE_SHARE, parsed.damageShare());
  }

  @Test
  void roundTripsThroughJson() {
    final VolleyArrowConfig original = new VolleyArrowConfig(7, 0.3f, 22.0f, 9);

    assertEquals(original, VolleyArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestAlone() {
    final VolleyArrowConfig updated = VolleyArrowConfig.defaults().withFragmentCount(3);

    assertEquals(3, updated.fragmentCount());
    assertEquals(VolleyArrowConfig.DEFAULT_SPREAD_DEGREES, updated.spreadDegrees());
    assertEquals(VolleyArrowConfig.DEFAULT_SPLIT_DELAY_TICKS, updated.splitDelayTicks());
  }

  @Test
  void aChangedValueIsStillClamped() {
    assertEquals(
        VolleyArrowConfig.FRAGMENT_COUNT_MAX,
        VolleyArrowConfig.defaults().withFragmentCount(999).fragmentCount());
  }

  @Test
  void everyVolleyFieldCanBeChangedOnItsOwn() {
    assertEquals(
        new VolleyArrowConfig(3, 0.1f, 2.0f, 5),
        VolleyArrowConfig.defaults()
            .withFragmentCount(3)
            .withDamageShare(0.1f)
            .withSpreadDegrees(2.0f)
            .withSplitDelayTicks(5));
  }
}
