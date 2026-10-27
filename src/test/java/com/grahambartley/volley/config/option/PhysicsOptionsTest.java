package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PhysicsOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "physics.gravityImpactRadius",
            "physics.gravityBlockExclusions",
            "physics.ricochetBounceCount",
            "physics.ricochetRetainsDamage"),
        PhysicsOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(PhysicsOptions.options(), PhysicsOptions.section().options());
  }
}
