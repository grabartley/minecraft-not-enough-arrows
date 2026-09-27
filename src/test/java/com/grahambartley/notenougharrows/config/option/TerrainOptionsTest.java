package com.grahambartley.notenougharrows.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TerrainOptionsTest {

  private static Stream<ConfigOption<NotEnoughArrowsConfig>> options() {
    return TerrainOptions.options().stream();
  }

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "terrain.drillEnabled",
            "terrain.drillToolTier",
            "terrain.pillarEnabled",
            "terrain.pillarHeightBlocks",
            "terrain.pillarLifetimeTicks",
            "terrain.drainEnabled",
            "terrain.drainRadius",
            "terrain.drainMaxBlocks",
            "terrain.freezeEnabled",
            "terrain.freezeRadius",
            "terrain.webEnabled",
            "terrain.webPatchRadius",
            "terrain.webLifetimeTicks",
            "terrain.paintEnabled"),
        TerrainOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("terrain", TerrainOptions.section().id());
    assertEquals(TerrainOptions.options(), TerrainOptions.section().options());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionChangesThatOptionsValue(final ConfigOption<NotEnoughArrowsConfig> option) {
    final NotEnoughArrowsConfig before = NotEnoughArrowsConfig.defaults();
    final NotEnoughArrowsConfig after = OptionMutations.toDifferentValue(option, before);

    assertNotEquals(option.displayValue(before), option.displayValue(after));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("options")
  void writingAnOptionLeavesEveryOtherSettingAlone(
      final ConfigOption<NotEnoughArrowsConfig> option) {
    final NotEnoughArrowsConfig before = NotEnoughArrowsConfig.defaults();
    final NotEnoughArrowsConfig after = OptionMutations.toDifferentValue(option, before);

    for (final ConfigOption<NotEnoughArrowsConfig> other : ServerConfigOptions.all()) {
      if (!other.id().equals(option.id())) {
        assertEquals(other.displayValue(before), other.displayValue(after), other.id());
      }
    }
  }
}
