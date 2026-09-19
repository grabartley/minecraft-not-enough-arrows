package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CombatOptionsTest {

  private static Stream<ConfigOption<NotEnoughArrowsConfig>> options() {
    return CombatOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "combat.shock.arcRadius",
            "combat.shock.damage",
            "combat.lifesteal.share",
            "combat.lifesteal.maxHealPerHit",
            "combat.status.rustDurationTicks",
            "combat.status.hasteDurationTicks",
            "combat.status.guardDurationTicks",
            "combat.homing.turnRate",
            "combat.homing.searchRadius",
            "combat.homing.searchConeDegrees",
            "combat.volley.fragmentCount",
            "combat.volley.damageShare",
            "combat.volley.spreadDegrees",
            "combat.volley.splitDelayTicks",
            "combat.railgun.speedMultiplier",
            "combat.railgun.gravityFactor"),
        CombatOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(CombatOptions.options(), CombatOptions.section().options());
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
