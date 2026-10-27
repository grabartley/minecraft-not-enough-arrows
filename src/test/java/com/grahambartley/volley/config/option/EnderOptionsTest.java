package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class EnderOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "ender.pearlMaxRangeBlocks",
            "ender.recallMaxRangeBlocks",
            "ender.recallAffectsPlayers"),
        EnderOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals(EnderOptions.options(), EnderOptions.section().options());
  }
}
