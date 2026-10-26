package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ChaosOptionsTest {

  private static Stream<ConfigOption<VolleyConfig>> options() {
    return ChaosOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "chaos.party.enabled",
            "chaos.chicken.enabled",
            "chaos.puffer.enabled",
            "chaos.puffer.durationTicks",
            "chaos.stink.enabled",
            "chaos.stink.cloudLifetimeTicks",
            "chaos.boomerang.enabled",
            "chaos.polymorph.enabled",
            "chaos.polymorph.durationTicks"),
        ChaosOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("chaos", ChaosOptions.section().id());
    assertEquals(ChaosOptions.options(), ChaosOptions.section().options());
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
