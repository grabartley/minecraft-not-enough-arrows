package com.grahambartley.notenougharrows.config;

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
    assertEquals(NotEnoughArrowsConfig.defaults(), ServerConfigHolder.get());
  }

  @Test
  void holdsTheConfigItIsGiven() {
    final NotEnoughArrowsConfig updated =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false));
    ServerConfigHolder.set(updated);

    assertEquals(updated, ServerConfigHolder.get());
  }

  @Test
  void fallsBackToDefaultsWhenGivenNull() {
    ServerConfigHolder.set(
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false)));
    ServerConfigHolder.set(null);

    assertEquals(NotEnoughArrowsConfig.defaults(), ServerConfigHolder.get());
  }

  @Test
  void resetsBackToDefaults() {
    ServerConfigHolder.set(
        NotEnoughArrowsConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false)));
    ServerConfigHolder.reset();

    assertEquals(NotEnoughArrowsConfig.defaults(), ServerConfigHolder.get());
  }
}
