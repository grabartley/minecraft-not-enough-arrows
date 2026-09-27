package com.grahambartley.notenougharrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DiscoveryArrowConfigTest {

  @Test
  void startsFromItsDocumentedDefaults() {
    final DiscoveryArrowConfig defaults = DiscoveryArrowConfig.defaults();

    assertEquals(DiscoveryArrowConfig.DEFAULT_TORCH_ENABLED, defaults.torchEnabled());
    assertEquals(
        DiscoveryArrowConfig.DEFAULT_BEACON_LIFETIME_TICKS, defaults.beaconLifetimeTicks());
    assertEquals(DiscoveryArrowConfig.DEFAULT_REVEAL_RADIUS, defaults.revealRadius());
    assertEquals(
        DiscoveryArrowConfig.DEFAULT_REVEAL_DURATION_TICKS, defaults.revealDurationTicks());
    assertEquals(DiscoveryArrowConfig.DEFAULT_PROSPECTOR_BLOCKS, defaults.prospectorBlocks());
    assertEquals(
        DiscoveryArrowConfig.DEFAULT_WATCHER_LIFETIME_TICKS, defaults.watcherLifetimeTicks());
    assertEquals(
        DiscoveryArrowConfig.DEFAULT_WATCHER_REPORT_INTERVAL_TICKS,
        defaults.watcherReportIntervalTicks());
    assertEquals(
        DiscoveryArrowConfig.DEFAULT_TRACER_PATH_LIFETIME_TICKS,
        defaults.tracerPathLifetimeTicks());
  }

  @Test
  void defaultsSurviveTheirOwnClamp() {
    final DiscoveryArrowConfig defaults = DiscoveryArrowConfig.defaults();

    assertEquals(
        defaults,
        new DiscoveryArrowConfig(
            defaults.torchEnabled(),
            defaults.beaconLifetimeTicks(),
            defaults.revealRadius(),
            defaults.revealDurationTicks(),
            defaults.prospectorBlocks(),
            defaults.watcherLifetimeTicks(),
            defaults.watcherReportIntervalTicks(),
            defaults.tracerPathLifetimeTicks()));
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "12000, 12000", "12001, 12000"})
  void clampsBeaconLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected,
        DiscoveryArrowConfig.defaults().withBeaconLifetimeTicks(given).beaconLifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "32, 32", "33, 32"})
  void clampsRevealRadius(final int given, final int expected) {
    assertEquals(expected, DiscoveryArrowConfig.defaults().withRevealRadius(given).revealRadius());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsRevealDurationTicks(final int given, final int expected) {
    assertEquals(
        expected,
        DiscoveryArrowConfig.defaults().withRevealDurationTicks(given).revealDurationTicks());
  }

  @Test
  void normalisesProspectorBlocksEntries() {
    final DiscoveryArrowConfig config =
        DiscoveryArrowConfig.defaults()
            .withProspectorBlocks(
                List.of("  Minecraft:Stone ", "minecraft:stone", "", "minecraft:dirt"));

    assertEquals(List.of("minecraft:stone", "minecraft:dirt"), config.prospectorBlocks());
  }

  @Test
  void capsProspectorBlocksSoTheSyncPayloadStaysBounded() {
    final List<String> tooMany =
        IntStream.rangeClosed(0, DiscoveryArrowConfig.PROSPECTOR_BLOCKS_MAX)
            .mapToObj(i -> "minecraft:block_" + i)
            .toList();

    assertEquals(
        DiscoveryArrowConfig.PROSPECTOR_BLOCKS_MAX,
        DiscoveryArrowConfig.defaults().withProspectorBlocks(tooMany).prospectorBlocks().size());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "24000, 24000", "24001, 24000"})
  void clampsWatcherLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected,
        DiscoveryArrowConfig.defaults().withWatcherLifetimeTicks(given).watcherLifetimeTicks());
  }

  @ParameterizedTest
  @CsvSource({"19, 20", "20, 20", "1200, 1200", "1201, 1200"})
  void clampsWatcherReportIntervalTicks(final int given, final int expected) {
    assertEquals(
        expected,
        DiscoveryArrowConfig.defaults()
            .withWatcherReportIntervalTicks(given)
            .watcherReportIntervalTicks());
  }

  @ParameterizedTest
  @CsvSource({"-1, 0", "0, 0", "1200, 1200", "1201, 1200"})
  void clampsTracerPathLifetimeTicks(final int given, final int expected) {
    assertEquals(
        expected,
        DiscoveryArrowConfig.defaults()
            .withTracerPathLifetimeTicks(given)
            .tracerPathLifetimeTicks());
  }

  @Test
  void fallsBackToDefaultsForAnEmptyObject() {
    assertEquals(DiscoveryArrowConfig.defaults(), DiscoveryArrowConfig.fromJson(new JsonObject()));
  }

  @Test
  void fallsBackToDefaultsForAMissingObject() {
    assertEquals(DiscoveryArrowConfig.defaults(), DiscoveryArrowConfig.fromJson(null));
  }

  @Test
  void readsOnlyTheKeysThatArePresentAndDefaultsTheRest() {
    final DiscoveryArrowConfig parsed =
        DiscoveryArrowConfig.fromJson(
            JsonParser.parseString("{\"beaconLifetimeTicks\":11999}").getAsJsonObject());

    assertEquals(11999, parsed.beaconLifetimeTicks());
    assertEquals(DiscoveryArrowConfig.DEFAULT_TORCH_ENABLED, parsed.torchEnabled());
  }

  @Test
  void clampsAnOutOfRangeValueReadFromAFile() {
    final DiscoveryArrowConfig parsed =
        DiscoveryArrowConfig.fromJson(
            JsonParser.parseString("{\"beaconLifetimeTicks\":12100}").getAsJsonObject());

    assertEquals(DiscoveryArrowConfig.BEACON_LIFETIME_TICKS_MAX, parsed.beaconLifetimeTicks());
  }

  @Test
  void fallsBackToTheDefaultForAValueOfTheWrongType() {
    final DiscoveryArrowConfig parsed =
        DiscoveryArrowConfig.fromJson(
            JsonParser.parseString("{\"beaconLifetimeTicks\":\"lots\"}").getAsJsonObject());

    assertEquals(DiscoveryArrowConfig.DEFAULT_BEACON_LIFETIME_TICKS, parsed.beaconLifetimeTicks());
  }

  @Test
  void roundTripsThroughJson() {
    final DiscoveryArrowConfig original =
        new DiscoveryArrowConfig(
            false, 11999, 31, 1199, List.of("minecraft:stone"), 23999, 1199, 1199);

    assertEquals(original, DiscoveryArrowConfig.fromJson(original.toJson()));
  }

  @Test
  void changingOneFieldLeavesTheRestOfTheFamilyAlone() {
    final DiscoveryArrowConfig original = DiscoveryArrowConfig.defaults();

    final DiscoveryArrowConfig updated = original.withTorchEnabled(false);

    assertEquals(false, updated.torchEnabled());
    assertEquals(original.beaconLifetimeTicks(), updated.beaconLifetimeTicks());
    assertEquals(original.revealRadius(), updated.revealRadius());
    assertEquals(original.revealDurationTicks(), updated.revealDurationTicks());
    assertEquals(original.prospectorBlocks(), updated.prospectorBlocks());
    assertEquals(original.watcherLifetimeTicks(), updated.watcherLifetimeTicks());
    assertEquals(original.watcherReportIntervalTicks(), updated.watcherReportIntervalTicks());
    assertEquals(original.tracerPathLifetimeTicks(), updated.tracerPathLifetimeTicks());
  }

  @Test
  void everyFieldCanBeChangedOnItsOwn() {
    final DiscoveryArrowConfig updated =
        DiscoveryArrowConfig.defaults()
            .withTorchEnabled(false)
            .withBeaconLifetimeTicks(11999)
            .withRevealRadius(31)
            .withRevealDurationTicks(1199)
            .withProspectorBlocks(List.of("minecraft:stone"))
            .withWatcherLifetimeTicks(23999)
            .withWatcherReportIntervalTicks(1199)
            .withTracerPathLifetimeTicks(1199);

    assertEquals(
        new DiscoveryArrowConfig(
            false, 11999, 31, 1199, List.of("minecraft:stone"), 23999, 1199, 1199),
        updated);
  }
}
