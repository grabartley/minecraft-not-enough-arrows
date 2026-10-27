package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grahambartley.volley.command.ConfigStatusLines;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ServerConfigOptionsTest {

  private static Stream<ConfigOption<VolleyConfig>> options() {
    return ServerConfigOptions.all().stream();
  }

  @Test
  void ordersSectionsByFeatureFamilyWithTheArrowFamiliesFirst() {
    assertEquals(
        List.of(
            "explosive",
            "grapple",
            "utility",
            "physics",
            "ender",
            "combat",
            "control",
            "traversal",
            "terrain",
            "agriculture",
            "discovery",
            "chaos",
            "social",
            "fletching",
            "sound"),
        ServerConfigOptions.sections().stream().map(ConfigSection::id).toList());
  }

  @Test
  void flattensEverySectionsOptionsInSectionOrder() {
    assertEquals(
        ServerConfigOptions.sections().stream()
            .flatMap(section -> section.options().stream())
            .toList(),
        ServerConfigOptions.all());
  }

  @Test
  void namesEverySettingExactlyOnce() {
    final Set<String> seen = new HashSet<>();

    for (final ConfigOption<VolleyConfig> option : ServerConfigOptions.all()) {
      assertTrue(seen.add(option.id()), "duplicate setting " + option.id());
    }
  }

  @Test
  void coversTheSameSettingsTheStatusCommandReports() {
    assertEquals(
        ConfigStatusLines.entries(VolleyConfig.defaults()).stream()
            .map(ConfigStatusLines.StatusEntry::setting)
            .toList(),
        ServerConfigOptions.all().stream().map(ConfigOption::id).toList());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void prefixesEverySettingWithItsSection(final ConfigOption<VolleyConfig> option) {
    assertTrue(
        ServerConfigOptions.sections().stream()
            .anyMatch(section -> option.id().startsWith(section.id() + ".")),
        option.id());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionChangesThatOptionsValue(final ConfigOption<VolleyConfig> option) {
    final VolleyConfig before = VolleyConfig.defaults();
    final VolleyConfig after = OptionMutations.toDifferentValue(option, before);

    assertNotEquals(option.displayValue(before), option.displayValue(after));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionLeavesEveryOtherSettingAlone(final ConfigOption<VolleyConfig> option) {
    final VolleyConfig before = VolleyConfig.defaults();
    final VolleyConfig after = OptionMutations.toDifferentValue(option, before);

    for (final ConfigOption<VolleyConfig> other : ServerConfigOptions.all()) {
      if (!other.id().equals(option.id())) {
        assertEquals(other.displayValue(before), other.displayValue(after), other.id());
      }
    }
  }

  @Test
  void namesExactlyTheSettingsTheConfigFileHolds() {
    final Set<String> fileKeys = new HashSet<>();
    collectLeafKeys("", VolleyConfig.defaults().toJson(), fileKeys);

    assertEquals(
        fileKeys,
        ServerConfigOptions.all().stream().map(ConfigOption::id).collect(Collectors.toSet()));
  }

  private static void collectLeafKeys(
      final String prefix, final JsonObject object, final Set<String> keys) {
    for (final String key : object.keySet()) {
      final JsonElement value = object.get(key);
      if (value.isJsonObject()) {
        collectLeafKeys(prefix + key + ".", value.getAsJsonObject(), keys);
      } else {
        keys.add(prefix + key);
      }
    }
  }
}
