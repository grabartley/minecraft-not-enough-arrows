package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class AgricultureOptionsTest {

  private static Stream<ConfigOption<VolleyConfig>> options() {
    return AgricultureOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "agriculture.blossom.radius",
            "agriculture.till.radius",
            "agriculture.harvest.radius",
            "agriculture.bee.count",
            "agriculture.bee.lifetimeTicks"),
        AgricultureOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("agriculture", AgricultureOptions.section().id());
    assertEquals(AgricultureOptions.options(), AgricultureOptions.section().options());
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
}
