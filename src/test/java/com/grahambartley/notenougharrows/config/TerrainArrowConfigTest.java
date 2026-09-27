package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TerrainArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final TerrainArrowConfig defaults = TerrainArrowConfig.defaults();

    assertEquals(TerrainArrowConfig.DEFAULT_DRILL_ENABLED, defaults.drillEnabled());
    assertEquals(TerrainArrowConfig.DEFAULT_DRILL_TOOL_TIER, defaults.drillToolTier());
    assertEquals(TerrainArrowConfig.DEFAULT_PILLAR_ENABLED, defaults.pillarEnabled());
    assertEquals(TerrainArrowConfig.DEFAULT_PILLAR_HEIGHT_BLOCKS, defaults.pillarHeightBlocks());
    assertEquals(TerrainArrowConfig.DEFAULT_PILLAR_LIFETIME_TICKS, defaults.pillarLifetimeTicks());
    assertEquals(TerrainArrowConfig.DEFAULT_DRAIN_ENABLED, defaults.drainEnabled());
    assertEquals(TerrainArrowConfig.DEFAULT_DRAIN_RADIUS, defaults.drainRadius());
    assertEquals(TerrainArrowConfig.DEFAULT_DRAIN_MAX_BLOCKS, defaults.drainMaxBlocks());
    assertEquals(TerrainArrowConfig.DEFAULT_FREEZE_ENABLED, defaults.freezeEnabled());
    assertEquals(TerrainArrowConfig.DEFAULT_FREEZE_RADIUS, defaults.freezeRadius());
    assertEquals(TerrainArrowConfig.DEFAULT_WEB_ENABLED, defaults.webEnabled());
    assertEquals(TerrainArrowConfig.DEFAULT_WEB_PATCH_RADIUS, defaults.webPatchRadius());
    assertEquals(TerrainArrowConfig.DEFAULT_WEB_LIFETIME_TICKS, defaults.webLifetimeTicks());
    assertEquals(TerrainArrowConfig.DEFAULT_PAINT_ENABLED, defaults.paintEnabled());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final TerrainArrowConfig defaults = TerrainArrowConfig.defaults();

    assertEquals(
        defaults,
        new TerrainArrowConfig(
            defaults.drillEnabled(),
            defaults.drillToolTier(),
            defaults.pillarEnabled(),
            defaults.pillarHeightBlocks(),
            defaults.pillarLifetimeTicks(),
            defaults.drainEnabled(),
            defaults.drainRadius(),
            defaults.drainMaxBlocks(),
            defaults.freezeEnabled(),
            defaults.freezeRadius(),
            defaults.webEnabled(),
            defaults.webPatchRadius(),
            defaults.webLifetimeTicks(),
            defaults.paintEnabled()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "2, 2", "3, 2"})
  void clampsDrillToolTier(final int given, final int expected) {
    assertEquals(expected, TerrainArrowConfig.defaults().withDrillToolTier(given).drillToolTier());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "16, 16", "17, 16"})
  void clampsPillarHeightBlocks(final int given, final int expected) {
    assertEquals(
        expected, TerrainArrowConfig.defaults().withPillarHeightBlocks(given).pillarHeightBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsPillarLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected,
        TerrainArrowConfig.defaults().withPillarLifetimeTicks(given).pillarLifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsDrainRadius(final int given, final int expected) {
    assertEquals(expected, TerrainArrowConfig.defaults().withDrainRadius(given).drainRadius());
  }

  @ParameterizedTest
  @CsvSource({"0, 1", "1, 1", "512, 512", "513, 512"})
  void clampsDrainMaxBlocks(final int given, final int expected) {
    assertEquals(
        expected, TerrainArrowConfig.defaults().withDrainMaxBlocks(given).drainMaxBlocks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "8, 8", "9, 8"})
  void clampsFreezeRadius(final int given, final int expected) {
    assertEquals(expected, TerrainArrowConfig.defaults().withFreezeRadius(given).freezeRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "3, 3", "4, 3"})
  void clampsWebPatchRadius(final int given, final int expected) {
    assertEquals(
        expected, TerrainArrowConfig.defaults().withWebPatchRadius(given).webPatchRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsWebLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected, TerrainArrowConfig.defaults().withWebLifetimeTicks(given).webLifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(TerrainArrowConfig.defaults(), TerrainArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(TerrainArrowConfig.defaults(), TerrainArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final TerrainArrowConfig parsed =
        TerrainArrowConfig.fromJson(
            JsonParser.parseString("{\"drillToolTier\":1}").getAsJsonObject());

    assertEquals(1, parsed.drillToolTier());
    assertEquals(TerrainArrowConfig.DEFAULT_DRILL_ENABLED, parsed.drillEnabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final TerrainArrowConfig parsed =
        TerrainArrowConfig.fromJson(
            JsonParser.parseString("{\"drillToolTier\":102}").getAsJsonObject());

    assertEquals(TerrainArrowConfig.DRILL_TOOL_TIER_MAX, parsed.drillToolTier());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final TerrainArrowConfig parsed =
        TerrainArrowConfig.fromJson(
            JsonParser.parseString("{\"drillToolTier\":\"lots\"}").getAsJsonObject());

    assertEquals(TerrainArrowConfig.DEFAULT_DRILL_TOOL_TIER, parsed.drillToolTier());
  }

  @Test
  void roundTripsThroughJson() {
    final TerrainArrowConfig original =
        new TerrainArrowConfig(
            false, 1, false, 15, 11999, false, 7, 511, false, 7, false, 2, 11999, false);

    assertEquals(original, TerrainArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final TerrainArrowConfig original = TerrainArrowConfig.defaults();

    final TerrainArrowConfig updated = original.withDrillEnabled(false);

    assertEquals(false, updated.drillEnabled());
    assertEquals(original.drillToolTier(), updated.drillToolTier());
    assertEquals(original.pillarEnabled(), updated.pillarEnabled());
    assertEquals(original.pillarHeightBlocks(), updated.pillarHeightBlocks());
    assertEquals(original.pillarLifetimeTicks(), updated.pillarLifetimeTicks());
    assertEquals(original.drainEnabled(), updated.drainEnabled());
    assertEquals(original.drainRadius(), updated.drainRadius());
    assertEquals(original.drainMaxBlocks(), updated.drainMaxBlocks());
    assertEquals(original.freezeEnabled(), updated.freezeEnabled());
    assertEquals(original.freezeRadius(), updated.freezeRadius());
    assertEquals(original.webEnabled(), updated.webEnabled());
    assertEquals(original.webPatchRadius(), updated.webPatchRadius());
    assertEquals(original.webLifetimeTicks(), updated.webLifetimeTicks());
    assertEquals(original.paintEnabled(), updated.paintEnabled());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final TerrainArrowConfig updated =
        TerrainArrowConfig.defaults()
            .withDrillEnabled(false)
            .withDrillToolTier(1)
            .withPillarEnabled(false)
            .withPillarHeightBlocks(15)
            .withPillarLifetimeTicks(11999)
            .withDrainEnabled(false)
            .withDrainRadius(7)
            .withDrainMaxBlocks(511)
            .withFreezeEnabled(false)
            .withFreezeRadius(7)
            .withWebEnabled(false)
            .withWebPatchRadius(2)
            .withWebLifetimeTicks(11999)
            .withPaintEnabled(false);

    assertEquals(
        new TerrainArrowConfig(
            false, 1, false, 15, 11999, false, 7, 511, false, 7, false, 2, 11999, false),
        updated);
  }
}
