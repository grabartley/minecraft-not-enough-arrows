package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class TerrainArrowConfigTest {

  @Test
  void defaultsEveryArrow() {
    final TerrainArrowConfig defaults = TerrainArrowConfig.defaults();

    assertEquals(DrillArrowConfig.defaults(), defaults.drill());
    assertEquals(PillarArrowConfig.defaults(), defaults.pillar());
    assertEquals(DrainArrowConfig.defaults(), defaults.drain());
    assertEquals(FreezeArrowConfig.defaults(), defaults.freeze());
    assertEquals(WebArrowConfig.defaults(), defaults.web());
    assertEquals(PaintArrowConfig.defaults(), defaults.paint());
  }

  @Test
  void substitutesDefaultsForMissingArrows() {
    assertEquals(
        TerrainArrowConfig.defaults(), new TerrainArrowConfig(null, null, null, null, null, null));
  }

  @Test
  void replacesOnlyTheDrillArrow() {
    final DrillArrowConfig replacement = DrillArrowConfig.defaults().withEnabled(false);
    final TerrainArrowConfig updated = TerrainArrowConfig.defaults().withDrill(replacement);

    assertEquals(replacement, updated.drill());
    assertEquals(PillarArrowConfig.defaults(), updated.pillar());
    assertEquals(DrainArrowConfig.defaults(), updated.drain());
    assertEquals(FreezeArrowConfig.defaults(), updated.freeze());
    assertEquals(WebArrowConfig.defaults(), updated.web());
    assertEquals(PaintArrowConfig.defaults(), updated.paint());
  }

  @Test
  void replacesOnlyThePillarArrow() {
    final PillarArrowConfig replacement = PillarArrowConfig.defaults().withEnabled(false);
    final TerrainArrowConfig updated = TerrainArrowConfig.defaults().withPillar(replacement);

    assertEquals(replacement, updated.pillar());
    assertEquals(DrillArrowConfig.defaults(), updated.drill());
    assertEquals(DrainArrowConfig.defaults(), updated.drain());
    assertEquals(FreezeArrowConfig.defaults(), updated.freeze());
    assertEquals(WebArrowConfig.defaults(), updated.web());
    assertEquals(PaintArrowConfig.defaults(), updated.paint());
  }

  @Test
  void replacesOnlyTheDrainArrow() {
    final DrainArrowConfig replacement = DrainArrowConfig.defaults().withEnabled(false);
    final TerrainArrowConfig updated = TerrainArrowConfig.defaults().withDrain(replacement);

    assertEquals(replacement, updated.drain());
    assertEquals(DrillArrowConfig.defaults(), updated.drill());
    assertEquals(PillarArrowConfig.defaults(), updated.pillar());
    assertEquals(FreezeArrowConfig.defaults(), updated.freeze());
    assertEquals(WebArrowConfig.defaults(), updated.web());
    assertEquals(PaintArrowConfig.defaults(), updated.paint());
  }

  @Test
  void replacesOnlyTheFreezeArrow() {
    final FreezeArrowConfig replacement = FreezeArrowConfig.defaults().withEnabled(false);
    final TerrainArrowConfig updated = TerrainArrowConfig.defaults().withFreeze(replacement);

    assertEquals(replacement, updated.freeze());
    assertEquals(DrillArrowConfig.defaults(), updated.drill());
    assertEquals(PillarArrowConfig.defaults(), updated.pillar());
    assertEquals(DrainArrowConfig.defaults(), updated.drain());
    assertEquals(WebArrowConfig.defaults(), updated.web());
    assertEquals(PaintArrowConfig.defaults(), updated.paint());
  }

  @Test
  void replacesOnlyTheWebArrow() {
    final WebArrowConfig replacement = WebArrowConfig.defaults().withEnabled(false);
    final TerrainArrowConfig updated = TerrainArrowConfig.defaults().withWeb(replacement);

    assertEquals(replacement, updated.web());
    assertEquals(DrillArrowConfig.defaults(), updated.drill());
    assertEquals(PillarArrowConfig.defaults(), updated.pillar());
    assertEquals(DrainArrowConfig.defaults(), updated.drain());
    assertEquals(FreezeArrowConfig.defaults(), updated.freeze());
    assertEquals(PaintArrowConfig.defaults(), updated.paint());
  }

  @Test
  void replacesOnlyThePaintArrow() {
    final PaintArrowConfig replacement = PaintArrowConfig.defaults().withEnabled(false);
    final TerrainArrowConfig updated = TerrainArrowConfig.defaults().withPaint(replacement);

    assertEquals(replacement, updated.paint());
    assertEquals(DrillArrowConfig.defaults(), updated.drill());
    assertEquals(PillarArrowConfig.defaults(), updated.pillar());
    assertEquals(DrainArrowConfig.defaults(), updated.drain());
    assertEquals(FreezeArrowConfig.defaults(), updated.freeze());
    assertEquals(WebArrowConfig.defaults(), updated.web());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TerrainArrowConfig.defaults(), TerrainArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void readsOneArrowAndDefaultsTheRest() {
    final TerrainArrowConfig parsed =
        TerrainArrowConfig.fromJson(
            JsonParser.parseString("{\"drill\":{\"enabled\":False}}").getAsJsonObject());

    assertEquals(false, parsed.drill().enabled());
    assertEquals(PillarArrowConfig.defaults(), parsed.pillar());
  }

  @Test
  void nestsEachArrowUnderItsOwnKey() {
    final JsonObject json = TerrainArrowConfig.defaults().toJson();

    assertTrue(json.get("drill").isJsonObject());
    assertTrue(json.get("pillar").isJsonObject());
    assertTrue(json.get("drain").isJsonObject());
    assertTrue(json.get("freeze").isJsonObject());
    assertTrue(json.get("web").isJsonObject());
    assertTrue(json.get("paint").isJsonObject());
  }

  @Test
  void roundTripsThroughJson() {
    final TerrainArrowConfig original =
        new TerrainArrowConfig(
            DrillArrowConfig.defaults().withEnabled(false),
            PillarArrowConfig.defaults().withEnabled(false),
            DrainArrowConfig.defaults().withEnabled(false),
            FreezeArrowConfig.defaults().withEnabled(false),
            WebArrowConfig.defaults().withEnabled(false),
            PaintArrowConfig.defaults().withEnabled(false));

    assertEquals(original, TerrainArrowConfig.fromJson(original.toJson()));
  }
}
