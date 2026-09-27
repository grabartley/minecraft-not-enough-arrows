package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class AgricultureArrowConfigTest {

  @Test
  void defaultsEveryArrow() {
    final AgricultureArrowConfig defaults = AgricultureArrowConfig.defaults();

    assertEquals(BlossomArrowConfig.defaults(), defaults.blossom());
    assertEquals(TillArrowConfig.defaults(), defaults.till());
    assertEquals(HarvestArrowConfig.defaults(), defaults.harvest());
    assertEquals(BeeArrowConfig.defaults(), defaults.bee());
  }

  @Test
  void substitutesDefaultsForMissingArrows() {
    assertEquals(
        AgricultureArrowConfig.defaults(), new AgricultureArrowConfig(null, null, null, null));
  }

  @Test
  void replacesOnlyTheBlossomArrow() {
    final BlossomArrowConfig replacement = BlossomArrowConfig.defaults().withRadius(7);
    final AgricultureArrowConfig updated =
        AgricultureArrowConfig.defaults().withBlossom(replacement);

    assertEquals(replacement, updated.blossom());
    assertEquals(TillArrowConfig.defaults(), updated.till());
    assertEquals(HarvestArrowConfig.defaults(), updated.harvest());
    assertEquals(BeeArrowConfig.defaults(), updated.bee());
  }

  @Test
  void replacesOnlyTheTillArrow() {
    final TillArrowConfig replacement = TillArrowConfig.defaults().withRadius(7);
    final AgricultureArrowConfig updated = AgricultureArrowConfig.defaults().withTill(replacement);

    assertEquals(replacement, updated.till());
    assertEquals(BlossomArrowConfig.defaults(), updated.blossom());
    assertEquals(HarvestArrowConfig.defaults(), updated.harvest());
    assertEquals(BeeArrowConfig.defaults(), updated.bee());
  }

  @Test
  void replacesOnlyTheHarvestArrow() {
    final HarvestArrowConfig replacement = HarvestArrowConfig.defaults().withRadius(7);
    final AgricultureArrowConfig updated =
        AgricultureArrowConfig.defaults().withHarvest(replacement);

    assertEquals(replacement, updated.harvest());
    assertEquals(BlossomArrowConfig.defaults(), updated.blossom());
    assertEquals(TillArrowConfig.defaults(), updated.till());
    assertEquals(BeeArrowConfig.defaults(), updated.bee());
  }

  @Test
  void replacesOnlyTheBeeArrow() {
    final BeeArrowConfig replacement = BeeArrowConfig.defaults().withCount(7);
    final AgricultureArrowConfig updated = AgricultureArrowConfig.defaults().withBee(replacement);

    assertEquals(replacement, updated.bee());
    assertEquals(BlossomArrowConfig.defaults(), updated.blossom());
    assertEquals(TillArrowConfig.defaults(), updated.till());
    assertEquals(HarvestArrowConfig.defaults(), updated.harvest());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        AgricultureArrowConfig.defaults(), AgricultureArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOneArrowAndDefaultsTheRest() {
    final AgricultureArrowConfig parsed =
        AgricultureArrowConfig.fromJson(
            JsonParser.parseString("{\"blossom\":{\"radius\":7}}").getAsJsonObject());

    assertEquals(7, parsed.blossom().radius());
    assertEquals(TillArrowConfig.defaults(), parsed.till());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = AgricultureArrowConfig.defaults().toJson();

    assertTrue(json.get("blossom").isJsonObject());
    assertTrue(json.get("till").isJsonObject());
    assertTrue(json.get("harvest").isJsonObject());
    assertTrue(json.get("bee").isJsonObject());
  }

  @Test
  void roundTripsThroughJson() {
    final AgricultureArrowConfig original =
        new AgricultureArrowConfig(
            BlossomArrowConfig.defaults().withRadius(7),
            TillArrowConfig.defaults().withRadius(7),
            HarvestArrowConfig.defaults().withRadius(7),
            BeeArrowConfig.defaults().withCount(7));

    assertEquals(original, AgricultureArrowConfig.fromJson(original.toJson()));
  }
}
