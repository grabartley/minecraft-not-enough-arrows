package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BooleanOptionTest {

  private static BooleanOption<NotEnoughArrowsConfig> damageTerrain() {
    return new BooleanOption<>(
        "explosive.damageTerrain",
        config -> config.explosive().damageTerrain(),
        (config, value) -> config.withExplosive(config.explosive().withDamageTerrain(value)));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void writesTheValueItIsGiven(final boolean value) {
    final NotEnoughArrowsConfig written =
        damageTerrain().write(NotEnoughArrowsConfig.defaults(), value);

    assertEquals(value, damageTerrain().read(written));
  }

  @Test
  void roundTripsTheValueThroughTheSubject() {
    assertFalse(
        damageTerrain().read(damageTerrain().write(NotEnoughArrowsConfig.defaults(), false)));
    assertTrue(damageTerrain().read(damageTerrain().write(NotEnoughArrowsConfig.defaults(), true)));
  }

  @Test
  void displaysTheValueAsText() {
    assertEquals(
        "false",
        damageTerrain()
            .displayValue(damageTerrain().write(NotEnoughArrowsConfig.defaults(), false)));
    assertEquals(
        "true",
        damageTerrain()
            .displayValue(damageTerrain().write(NotEnoughArrowsConfig.defaults(), true)));
  }

  @Test
  void rejectsABlankId() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new BooleanOption<NotEnoughArrowsConfig>(
                " ", config -> true, (config, value) -> config));
  }
}
