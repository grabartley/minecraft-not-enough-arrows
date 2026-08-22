package com.grahambartley.morearrows.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ServerConfigHolderTest {

  @AfterEach
  void tearDown() {
    ServerConfigHolder.reset();
  }

  @Test
  void startsAtDefaults() {
    assertEquals(MoreArrowsConfig.defaults(), ServerConfigHolder.get());
  }

  @Test
  void holdsTheConfigItIsGiven() {
    final MoreArrowsConfig updated =
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, true, true, 8, false));
    ServerConfigHolder.set(updated);

    assertEquals(updated, ServerConfigHolder.get());
  }

  @Test
  void fallsBackToDefaultsWhenGivenNull() {
    ServerConfigHolder.set(
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, true, true, 8, false)));
    ServerConfigHolder.set(null);

    assertEquals(MoreArrowsConfig.defaults(), ServerConfigHolder.get());
  }

  @Test
  void resetsBackToDefaults() {
    ServerConfigHolder.set(
        MoreArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, true, true, 8, false)));
    ServerConfigHolder.reset();

    assertEquals(MoreArrowsConfig.defaults(), ServerConfigHolder.get());
  }
}
