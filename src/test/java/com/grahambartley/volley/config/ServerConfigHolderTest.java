package com.grahambartley.volley.config;

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
    assertEquals(VolleyConfig.defaults(), ServerConfigHolder.get());
  }

  @Test
  void holdsTheConfigItIsGiven() {
    final VolleyConfig updated =
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false));
    ServerConfigHolder.set(updated);

    assertEquals(updated, ServerConfigHolder.get());
  }

  @Test
  void fallsBackToDefaultsWhenGivenNull() {
    ServerConfigHolder.set(
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false)));
    ServerConfigHolder.set(null);

    assertEquals(VolleyConfig.defaults(), ServerConfigHolder.get());
  }

  @Test
  void resetsBackToDefaults() {
    ServerConfigHolder.set(
        VolleyConfig.defaults()
            .withGrapple(new GrappleArrowConfig(64, 1.0f, 0.2f, true, true, 8, false)));
    ServerConfigHolder.reset();

    assertEquals(VolleyConfig.defaults(), ServerConfigHolder.get());
  }
}
