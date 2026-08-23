package com.grahambartley.morearrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.command.ConfigStatusLines;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ServerConfigOptionsTest {

  private static Stream<ConfigOption<MoreArrowsConfig>> options() {
    return ServerConfigOptions.all().stream();
  }

  @Test
  void ordersSectionsByArrowFamily() {
    assertEquals(
        List.of("explosive", "grapple", "utility", "physics"),
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

    for (final ConfigOption<MoreArrowsConfig> option : ServerConfigOptions.all()) {
      assertTrue(seen.add(option.id()), "duplicate setting " + option.id());
    }
  }

  @Test
  void coversTheSameSettingsTheStatusCommandReports() {
    assertEquals(
        ConfigStatusLines.entries(MoreArrowsConfig.defaults()).stream()
            .map(ConfigStatusLines.StatusEntry::setting)
            .toList(),
        ServerConfigOptions.all().stream().map(ConfigOption::id).toList());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void prefixesEverySettingWithItsSection(final ConfigOption<MoreArrowsConfig> option) {
    assertTrue(
        ServerConfigOptions.sections().stream()
            .anyMatch(section -> option.id().startsWith(section.id() + ".")),
        option.id());
  }
}
