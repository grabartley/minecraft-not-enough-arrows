package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DisarmArrowConfigTest {

  @Test
  void affectsPlayersByDefault() {
    assertTrue(DisarmArrowConfig.defaults().affectsPlayers());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(DisarmArrowConfig.defaults(), DisarmArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsTheSwitchFromJson() {
    assertFalse(
        DisarmArrowConfig.fromJson(
                JsonParser.parseString("{\"affectsPlayers\":false}").getAsJsonObject())
            .affectsPlayers());
  }

  @Test
  void keepsTheDefaultWhenTheValueIsNotABoolean() {
    assertTrue(
        DisarmArrowConfig.fromJson(
                JsonParser.parseString("{\"affectsPlayers\":\"maybe\"}").getAsJsonObject())
            .affectsPlayers());
  }

  @Test
  void roundTripsThroughJson() {
    final DisarmArrowConfig original = new DisarmArrowConfig(false, 7.5f);

    assertEquals(original, DisarmArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingTheValueReturnsANewRecord() {
    assertFalse(DisarmArrowConfig.defaults().withAffectsPlayers(false).affectsPlayers());
  }

  @Test
  void throwsTheItemFiveBlocksByDefault() {
    assertEquals(5.0f, DisarmArrowConfig.defaults().throwDistance());
  }

  @ParameterizedTest
  @CsvSource({"-1.0, 0.0", "0.0, 0.0", "5.0, 5.0", "16.0, 16.0", "99.0, 16.0"})
  void clampsTheThrowDistance(final float given, final float expected) {
    assertEquals(expected, new DisarmArrowConfig(true, given).throwDistance());
  }

  @Test
  void changingTheThrowLeavesTheSwitchAlone() {
    final DisarmArrowConfig updated = DisarmArrowConfig.defaults().withThrowDistance(2.0f);

    assertEquals(2.0f, updated.throwDistance());
    assertTrue(updated.affectsPlayers());
  }
}
