package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExplosiveOptionsTest {

  private static Stream<ConfigOption<NotEnoughArrowsConfig>> options() {
    return ExplosiveOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "explosive.gunpowder.delayTicks",
            "explosive.gunpowder.power",
            "explosive.tnt.delayTicks",
            "explosive.tnt.power",
            "explosive.fireCharge.delayTicks",
            "explosive.fireCharge.power",
            "explosive.damageTerrain",
            "explosive.damageEntities",
            "explosive.firePatchRadius",
            "explosive.firePatchDurationTicks",
            "explosive.beepVolume",
            "explosive.incendiary.burnRadius",
            "explosive.incendiary.igniteSeconds",
            "explosive.incendiary.ignitesBlocks"),
        ExplosiveOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(ExplosiveOptions.options(), ExplosiveOptions.section().options());
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
