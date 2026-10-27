package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class UtilityOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "utility.glowDurationTicks",
            "utility.redstoneSignalDurationTicks",
            "utility.redstoneSignalStrength",
            "utility.windBurstRadius",
            "utility.windPushStrength"),
        UtilityOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(UtilityOptions.options(), UtilityOptions.section().options());
  }
}
