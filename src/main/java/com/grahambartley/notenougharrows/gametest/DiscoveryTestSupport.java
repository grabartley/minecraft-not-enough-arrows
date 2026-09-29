package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.DiscoveryArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import java.util.function.UnaryOperator;

final class DiscoveryTestSupport {
  static final String TEMPLATE = FiringRangeSupport.TEMPLATE;
  static final String ARENA = "not-enough-arrows:open_arena";

  private DiscoveryTestSupport() {}

  static void useDiscovery(final UnaryOperator<DiscoveryArrowConfig> change) {
    final NotEnoughArrowsConfig defaults = NotEnoughArrowsConfig.defaults();
    ServerConfigHolder.set(defaults.withDiscovery(change.apply(defaults.discovery())));
  }
}
