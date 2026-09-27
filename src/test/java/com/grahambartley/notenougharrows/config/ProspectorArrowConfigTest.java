package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProspectorArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final ProspectorArrowConfig defaults = ProspectorArrowConfig.defaults();

    assertEquals(ProspectorArrowConfig.DEFAULT_RADIUS, defaults.radius());
    assertEquals(ProspectorArrowConfig.DEFAULT_DURATION_TICKS, defaults.durationTicks());
    assertEquals(ProspectorArrowConfig.DEFAULT_BLOCKS, defaults.blocks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final ProspectorArrowConfig defaults = ProspectorArrowConfig.defaults();

    assertEquals(
        defaults,
        new ProspectorArrowConfig(defaults.radius(), defaults.durationTicks(), defaults.blocks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "16, 16", "17, 16"})
  void clampsRadius(final int given, final int expected) {
    assertEquals(expected, ProspectorArrowConfig.defaults().withRadius(given).radius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsDurationTicks(final int given, final int expected) {
    assertEquals(
        expected, ProspectorArrowConfig.defaults().withDurationTicks(given).durationTicks());
  }

  @Test
  void normalisesBlocksEntries() {
    final ProspectorArrowConfig config =
        ProspectorArrowConfig.defaults()
            .withBlocks(List.of("  Minecraft:Stone ", "minecraft:stone", "", "minecraft:dirt"));

    assertEquals(List.of("minecraft:stone", "minecraft:dirt"), config.blocks());
  }

  @Test
  void capsBlocksSoTheSyncPayloadStaysBounded() {
    final List<String> tooMany =
        IntStream.rangeClosed(0, ProspectorArrowConfig.BLOCKS_MAX)
            .mapToObj(i -> "minecraft:block_" + i)
            .toList();

    assertEquals(
        ProspectorArrowConfig.BLOCKS_MAX,
        ProspectorArrowConfig.defaults().withBlocks(tooMany).blocks().size());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(
        ProspectorArrowConfig.defaults(), ProspectorArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(ProspectorArrowConfig.defaults(), ProspectorArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final ProspectorArrowConfig parsed =
        ProspectorArrowConfig.fromJson(JsonParser.parseString("{\"radius\":15}").getAsJsonObject());

    assertEquals(15, parsed.radius());
    assertEquals(ProspectorArrowConfig.DEFAULT_DURATION_TICKS, parsed.durationTicks());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final ProspectorArrowConfig parsed =
        ProspectorArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":116}").getAsJsonObject());

    assertEquals(ProspectorArrowConfig.RADIUS_MAX, parsed.radius());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final ProspectorArrowConfig parsed =
        ProspectorArrowConfig.fromJson(
            JsonParser.parseString("{\"radius\":\"lots\"}").getAsJsonObject());

    assertEquals(ProspectorArrowConfig.DEFAULT_RADIUS, parsed.radius());
  }

  @Test
  void roundTripsThroughJson() {
    final ProspectorArrowConfig original =
        new ProspectorArrowConfig(15, 1199, List.of("minecraft:stone"));

    assertEquals(original, ProspectorArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final ProspectorArrowConfig original = ProspectorArrowConfig.defaults();

    final ProspectorArrowConfig updated = original.withRadius(15);

    assertEquals(15, updated.radius());
    assertEquals(original.durationTicks(), updated.durationTicks());
    assertEquals(original.blocks(), updated.blocks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final ProspectorArrowConfig updated =
        ProspectorArrowConfig.defaults()
            .withRadius(15)
            .withDurationTicks(1199)
            .withBlocks(List.of("minecraft:stone"));

    assertEquals(new ProspectorArrowConfig(15, 1199, List.of("minecraft:stone")), updated);
  }
}
