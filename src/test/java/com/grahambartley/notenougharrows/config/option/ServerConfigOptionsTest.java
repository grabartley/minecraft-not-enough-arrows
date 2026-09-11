package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.command.ConfigStatusLines;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ServerConfigOptionsTest {

  private static Stream<ConfigOption<NotEnoughArrowsConfig>> options() {
    return ServerConfigOptions.all().stream();
  }

  @Test
  void ordersSectionsByFeatureFamilyWithTheArrowFamiliesFirst() {
    assertEquals(
        List.of("explosive", "grapple", "utility", "physics", "ender", "fletching"),
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

    for (final ConfigOption<NotEnoughArrowsConfig> option : ServerConfigOptions.all()) {
      assertTrue(seen.add(option.id()), "duplicate setting " + option.id());
    }
  }

  @Test
  void coversTheSameSettingsTheStatusCommandReports() {
    assertEquals(
        ConfigStatusLines.entries(NotEnoughArrowsConfig.defaults()).stream()
            .map(ConfigStatusLines.StatusEntry::setting)
            .toList(),
        ServerConfigOptions.all().stream().map(ConfigOption::id).toList());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void prefixesEverySettingWithItsSection(final ConfigOption<NotEnoughArrowsConfig> option) {
    assertTrue(
        ServerConfigOptions.sections().stream()
            .anyMatch(section -> option.id().startsWith(section.id() + ".")),
        option.id());
  }
}
