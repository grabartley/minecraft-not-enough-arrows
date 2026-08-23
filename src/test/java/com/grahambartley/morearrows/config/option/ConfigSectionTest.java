package com.grahambartley.morearrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.morearrows.config.MoreArrowsConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigSectionTest {

  private static ConfigOption<MoreArrowsConfig> anyOption() {
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
    final List<ConfigOption<MoreArrowsConfig>> source = new ArrayList<>(List.of(anyOption()));
    final ConfigSection<MoreArrowsConfig> section = new ConfigSection<>("explosive", source);

    source.clear();

    assertEquals(1, section.options().size());
  }

  @Test
  void rejectsABlankId() {
    assertThrows(
        IllegalArgumentException.class, () -> new ConfigSection<>("", List.of(anyOption())));
  }
}
