package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CombatOptionsTest {

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
}
