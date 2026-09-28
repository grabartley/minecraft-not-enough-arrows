package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TraversalOptionsTest {

  private static Stream<ConfigOption<NotEnoughArrowsConfig>> options() {
    return TraversalOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "traversal.zipline.maxSpanBlocks",
            "traversal.zipline.pendingWindowTicks",
            "traversal.zipline.rideSpeed",
            "traversal.zipline.lifetimeTicks",
            "traversal.tow.rangeBlocks",
            "traversal.tow.maxTicks",
            "traversal.tow.speed",
            "traversal.updraft.heightBlocks",
            "traversal.updraft.lifetimeTicks",
            "traversal.updraft.strength",
            "traversal.vine.lengthBlocks",
            "traversal.trampoline.strength",
            "traversal.trampoline.lifetimeTicks",
            "traversal.scaffold.heightBlocks",
            "traversal.scaffold.lifetimeTicks",
            "traversal.bridge.lengthBlocks",
            "traversal.bridge.lifetimeTicks"),
        TraversalOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("traversal", TraversalOptions.section().id());
    assertEquals(TraversalOptions.options(), TraversalOptions.section().options());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionChangesThatOptionsValue(final ConfigOption<NotEnoughArrowsConfig> option) {
    final NotEnoughArrowsConfig before = NotEnoughArrowsConfig.defaults();
    final NotEnoughArrowsConfig after = OptionMutations.toDifferentValue(option, before);

    assertNotEquals(option.displayValue(before), option.displayValue(after));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionLeavesEveryOtherSettingAlone(
      final ConfigOption<NotEnoughArrowsConfig> option) {
    final NotEnoughArrowsConfig before = NotEnoughArrowsConfig.defaults();
    final NotEnoughArrowsConfig after = OptionMutations.toDifferentValue(option, before);

    for (final ConfigOption<NotEnoughArrowsConfig> other : ServerConfigOptions.all()) {
      if (!other.id().equals(option.id())) {
        assertEquals(other.displayValue(before), other.displayValue(after), other.id());
      }
    }
  }
}
