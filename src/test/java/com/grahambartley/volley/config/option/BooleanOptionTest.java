package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.volley.config.VolleyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BooleanOptionTest {

  private static BooleanOption<VolleyConfig> damageTerrain() {
    return new BooleanOption<>(
        "explosive.damageTerrain",
        config -> config.explosive().damageTerrain(),
        (config, value) -> config.withExplosive(config.explosive().withDamageTerrain(value)));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void writesTheValueItIsGiven(final boolean value) {
    final VolleyConfig written = damageTerrain().write(VolleyConfig.defaults(), value);

    assertEquals(value, damageTerrain().read(written));
  }

  @Test
  void roundTripsTheValueThroughTheSubject() {
    assertFalse(damageTerrain().read(damageTerrain().write(VolleyConfig.defaults(), false)));
    assertTrue(damageTerrain().read(damageTerrain().write(VolleyConfig.defaults(), true)));
  }

  @Test
  void displaysTheValueAsText() {
    assertEquals(
        "false",
        damageTerrain().displayValue(damageTerrain().write(VolleyConfig.defaults(), false)));
    assertEquals(
        "true", damageTerrain().displayValue(damageTerrain().write(VolleyConfig.defaults(), true)));
  }

  @Test
  void rejectsABlankId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BooleanOption<VolleyConfig>(" ", config -> true, (config, value) -> config));
  }
}
