package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class AgricultureOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "agriculture.blossom.radius",
            "agriculture.till.radius",
            "agriculture.harvest.radius",
            "agriculture.bee.count",
            "agriculture.bee.lifetimeTicks"),
        AgricultureOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("agriculture", AgricultureOptions.section().id());
    assertEquals(AgricultureOptions.options(), AgricultureOptions.section().options());
  }
}
