package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigSectionTest {

  private static ConfigOption<NotEnoughArrowsConfig> anyOption() {
    return new BooleanOption<>(
        "explosive.damageTerrain",
        config -> config.explosive().damageTerrain(),
        (config, value) -> config.withExplosive(config.explosive().withDamageTerrain(value)));
  }

  @Test
  void keepsTheOptionsItWasGiven() {
    assertEquals(
        List.of(anyOption()), new ConfigSection<>("explosive", List.of(anyOption())).options());
  }

  @Test
  void doesNotSeeLaterChangesToTheListItWasGiven() {
    final List<ConfigOption<NotEnoughArrowsConfig>> source = new ArrayList<>(List.of(anyOption()));
    final ConfigSection<NotEnoughArrowsConfig> section = new ConfigSection<>("explosive", source);

    source.clear();

    assertEquals(1, section.options().size());
  }

  @Test
  void rejectsABlankId() {
    assertThrows(
        IllegalArgumentException.class, () -> new ConfigSection<>("", List.of(anyOption())));
  }
}
