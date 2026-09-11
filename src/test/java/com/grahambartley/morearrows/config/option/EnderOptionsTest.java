package com.grahambartley.morearrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EnderOptionsTest {

  private static Stream<ConfigOption<MoreArrowsConfig>> options() {
    return EnderOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "ender.pearlMaxRangeBlocks",
            "ender.pearlArrivalDamage",
            "ender.recallMaxRangeBlocks",
            "ender.recallAffectsPlayers"),
        EnderOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(EnderOptions.options(), EnderOptions.section().options());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionChangesThatOptionsValue(final ConfigOption<MoreArrowsConfig> option) {
    final MoreArrowsConfig before = MoreArrowsConfig.defaults();
    final MoreArrowsConfig after = OptionMutations.toDifferentValue(option, before);

    assertNotEquals(option.displayValue(before), option.displayValue(after));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionLeavesEveryOtherSettingAlone(final ConfigOption<MoreArrowsConfig> option) {
    final MoreArrowsConfig before = MoreArrowsConfig.defaults();
    final MoreArrowsConfig after = OptionMutations.toDifferentValue(option, before);

    for (final ConfigOption<MoreArrowsConfig> other : ServerConfigOptions.all()) {
      if (!other.id().equals(option.id())) {
        assertEquals(other.displayValue(before), other.displayValue(after), other.id());
      }
    }
  }
}
