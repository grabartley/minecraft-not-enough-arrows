package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class DiscoveryOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "discovery.torch.enabled",
            "discovery.beacon.lifetimeTicks",
            "discovery.prospector.radius",
            "discovery.prospector.durationTicks",
            "discovery.prospector.blocks",
            "discovery.sonar.radius",
            "discovery.sonar.durationTicks",
            "discovery.tripwire.lifetimeTicks",
            "discovery.tripwire.reportIntervalTicks",
            "discovery.tracer.pathLifetimeTicks"),
        DiscoveryOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("discovery", DiscoveryOptions.section().id());
    assertEquals(DiscoveryOptions.options(), DiscoveryOptions.section().options());
  }
}
