package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CombatOptionsTest {

  private static Stream<ConfigOption<VolleyConfig>> options() {
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
