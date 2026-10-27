package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ControlOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "control.frost.durationTicks",
            "control.levitation.durationTicks",
            "control.targeting.tauntRadius",
            "control.targeting.tauntDurationTicks",
            "control.targeting.repelRadius",
            "control.targeting.repelDurationTicks",
            "control.targeting.repelDistance",
            "control.allegiance.durationTicks",
            "control.allegiance.defendRadius",
            "control.smoke.radius",
            "control.smoke.durationTicks",
            "control.disarm.affectsPlayers",
            "control.disarm.throwDistance"),
        ControlOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(ControlOptions.options(), ControlOptions.section().options());
  }
}
