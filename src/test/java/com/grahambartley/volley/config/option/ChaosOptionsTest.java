package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChaosOptionsTest {

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
}
