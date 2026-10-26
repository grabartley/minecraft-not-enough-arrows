package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import org.junit.jupiter.api.Test;

class IdentifierListOptionTest {

  private static IdentifierListOption<VolleyConfig> exclusions(final int maxEntries) {
    return new IdentifierListOption<>(
        "physics.gravityBlockExclusions",
        maxEntries,
        config -> config.physics().gravityBlockExclusions(),
        (config, value) -> config.withPhysics(config.physics().withGravityBlockExclusions(value)));
  }

  @Test
  void normalisesCaseAndWhitespaceOnWrite() {
    final VolleyConfig written =
        exclusions(256).write(VolleyConfig.defaults(), List.of("  Minecraft:STONE  "));

    assertEquals(List.of("minecraft:stone"), exclusions(256).read(written));
  }

  @Test
  void dropsDuplicateEntriesOnWrite() {
    final VolleyConfig written =
        exclusions(256)
            .write(
                VolleyConfig.defaults(),
                List.of("minecraft:stone", "MINECRAFT:STONE", "minecraft:dirt"));

    assertEquals(List.of("minecraft:stone", "minecraft:dirt"), exclusions(256).read(written));
  }

  @Test
  void stopsAtItsMaximumNumberOfEntries() {
    final VolleyConfig written =
        exclusions(2)
            .write(
                VolleyConfig.defaults(),
                List.of("minecraft:stone", "minecraft:dirt", "minecraft:sand"));

    assertEquals(List.of("minecraft:stone", "minecraft:dirt"), exclusions(2).read(written));
  }

  @Test
  void displaysAnEmptyListAsText() {
    assertEquals("(none)", exclusions(256).displayValue(VolleyConfig.defaults()));
  }

  @Test
  void displaysAPopulatedListAsText() {
    final VolleyConfig written =
        exclusions(256)
            .write(VolleyConfig.defaults(), List.of("minecraft:stone", "minecraft:dirt"));

    assertEquals("minecraft:stone, minecraft:dirt", exclusions(256).displayValue(written));
  }

  @Test
  void rejectsANegativeMaximum() {
    assertThrows(IllegalArgumentException.class, () -> exclusions(-1));
  }
}
