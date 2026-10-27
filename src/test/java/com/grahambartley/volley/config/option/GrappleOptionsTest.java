package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class GrappleOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "grapple.maxRangeBlocks",
            "grapple.pullSpeed",
            "grapple.pullAcceleration",
            "grapple.cancelFallDamageOnArrival",
            "grapple.returnArrowOnArrival",
            "grapple.ropeLengthBlocks",
            "grapple.ropesDecay"),
        GrappleOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(GrappleOptions.options(), GrappleOptions.section().options());
  }
}
