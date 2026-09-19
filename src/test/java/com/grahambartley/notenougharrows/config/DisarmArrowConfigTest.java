package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

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
    final DisarmArrowConfig original = new DisarmArrowConfig(false);

    assertEquals(original, DisarmArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingTheValueReturnsANewRecord() {
    assertFalse(DisarmArrowConfig.defaults().withAffectsPlayers(false).affectsPlayers());
  }
}
