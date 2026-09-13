package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.command.ConfigStatusLines.StatusEntry;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConfigStatusLinesTest {

  private static Map<String, String> reported(final NotEnoughArrowsConfig config) {
    return ConfigStatusLines.entries(config).stream()
        .collect(Collectors.toMap(StatusEntry::setting, StatusEntry::value));
  }

  @ParameterizedTest(name = "{0}={1}")
  @CsvSource({
    "explosive.gunpowder.delayTicks,       80",
    "explosive.gunpowder.power,            4.00",
    "explosive.tnt.delayTicks,             70",
    "explosive.tnt.power,                  6.00",
    "explosive.fireCharge.delayTicks,      60",
    "explosive.fireCharge.power,           8.00",
    "explosive.damageTerrain,              true",
    "explosive.damageEntities,             true",
    "explosive.firePatchRadius,            2",
    "explosive.firePatchDurationTicks,     200",
    "explosive.beepVolume,                 1.00",
    "grapple.maxRangeBlocks,               128",
    "grapple.pullSpeed,                    1.50",
    "grapple.pullAcceleration,             0.15",
    "grapple.cancelFallDamageOnArrival,    true",
    "grapple.returnArrowOnArrival,         true",
    "grapple.ropeLengthBlocks,             128",
    "grapple.ropesDecay,                   false",
    "utility.glowDurationTicks,            200",
    "utility.redstoneSignalDurationTicks,  40",
    "utility.redstoneSignalStrength,       15",
    "utility.windBurstRadius,              3.00",
    "utility.windPushStrength,             1.00",
    "physics.gravityImpactRadius,          3",
    "physics.gravityBlockExclusions,       (none)",
    "physics.ricochetBounceCount,          3",
    "physics.ricochetRetainsDamage,        true",
  })
  void everySettingIsReportedWithItsCurrentValue(String setting, String expected) {
    assertEquals(expected, reported(NotEnoughArrowsConfig.defaults()).get(setting));
  }

  @Test
  void everySettingIsReportedExactlyOnce() {
    final List<StatusEntry> entries = ConfigStatusLines.entries(NotEnoughArrowsConfig.defaults());

    assertEquals(
        entries.size(),
        entries.stream().map(StatusEntry::setting).distinct().count(),
        "A setting reported twice means the status output disagrees with itself");
  }

  @Test
  void aChangedValueIsWhatGetsReported() {
    final NotEnoughArrowsConfig config =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(NotEnoughArrowsConfig.defaults().grapple().withMaxRangeBlocks(96));

    assertEquals("96", reported(config).get(ConfigSettings.GRAPPLE_MAX_RANGE_BLOCKS));
  }

  @Test
  void everySettingItReportsCanBeSetBackThroughTheCommandTree() {
    final var dispatcher = CommandParsing.dispatcher();
    final var operator = CommandParsing.source(true);

    final List<String> unsettable =
        ConfigStatusLines.entries(NotEnoughArrowsConfig.defaults()).stream()
            .map(ConfigStatusLinesTest::commandFor)
            .filter(command -> !CommandParsing.accepts(dispatcher, operator, command))
            .toList();

    assertTrue(
        unsettable.isEmpty(),
        "Settings the status output reports but an operator cannot set: " + unsettable);
  }

  private static String commandFor(final StatusEntry entry) {
    final String path =
        java.util.Arrays.stream(entry.setting().split("\\."))
            .map(segment -> segment.toLowerCase(Locale.ROOT))
            .collect(Collectors.joining(" "));
    final String value =
        entry.setting().equals(ConfigSettings.PHYSICS_GRAVITY_BLOCK_EXCLUSIONS)
            ? PhysicsCommandNodes.ADD + " minecraft:sand"
            : entry.value();
    return "notenougharrows config " + path + " " + value;
  }
}
