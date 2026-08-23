package com.grahambartley.morearrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BooleanOptionTest {

  private static BooleanOption<MoreArrowsConfig> damageTerrain() {
    return new BooleanOption<>(
        "explosive.damageTerrain",
        config -> config.explosive().damageTerrain(),
        (config, value) -> config.withExplosive(config.explosive().withDamageTerrain(value)));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void writesTheValueItIsGiven(final boolean value) {
    final MoreArrowsConfig written = damageTerrain().write(MoreArrowsConfig.defaults(), value);

    assertEquals(value, damageTerrain().read(written));
  }

  @Test
  void readsTheValueOffTheSubject() {
    assertFalse(damageTerrain().read(MoreArrowsConfig.defaults()));
    assertTrue(damageTerrain().read(damageTerrain().write(MoreArrowsConfig.defaults(), true)));
  }

  @Test
  void displaysTheValueAsText() {
    assertEquals("false", damageTerrain().displayValue(MoreArrowsConfig.defaults()));
    assertEquals(
        "true",
        damageTerrain().displayValue(damageTerrain().write(MoreArrowsConfig.defaults(), true)));
  }

  @Test
  void rejectsABlankId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BooleanOption<MoreArrowsConfig>(" ", config -> true, (config, value) -> config));
  }
}
