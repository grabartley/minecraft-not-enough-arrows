package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class TraversalOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "traversal.zipline.maxSpanBlocks",
            "traversal.zipline.pendingWindowTicks",
            "traversal.zipline.rideSpeed",
            "traversal.zipline.lifetimeTicks",
            "traversal.tow.rangeBlocks",
            "traversal.tow.maxTicks",
            "traversal.tow.speed",
            "traversal.updraft.heightBlocks",
            "traversal.updraft.lifetimeTicks",
            "traversal.updraft.strength",
            "traversal.vine.lengthBlocks",
            "traversal.trampoline.strength",
            "traversal.trampoline.lifetimeTicks",
            "traversal.scaffold.heightBlocks",
            "traversal.scaffold.lifetimeTicks",
            "traversal.bridge.lengthBlocks",
            "traversal.bridge.lifetimeTicks"),
        TraversalOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("traversal", TraversalOptions.section().id());
    assertEquals(TraversalOptions.options(), TraversalOptions.section().options());
  }
}
