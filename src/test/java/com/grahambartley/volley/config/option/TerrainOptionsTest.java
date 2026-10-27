package com.grahambartley.volley.config.option;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class TerrainOptionsTest {

  @Test
  void listsEverySettingInTheOrderTheStatusOutputUses() {
    assertEquals(
        List.of(
            "terrain.drill.enabled",
            "terrain.drill.toolTier",
            "terrain.pillar.enabled",
            "terrain.pillar.heightBlocks",
            "terrain.pillar.lifetimeTicks",
            "terrain.drain.enabled",
            "terrain.drain.radius",
            "terrain.drain.maxBlocks",
            "terrain.freeze.enabled",
            "terrain.freeze.radius",
            "terrain.freeze.maxBlocks",
            "terrain.web.enabled",
            "terrain.web.patchRadius",
            "terrain.web.lifetimeTicks",
            "terrain.paint.enabled"),
        TerrainOptions.options().stream().map(ConfigOption::id).toList());
  }

  @Test
  void groupsItsOptionsUnderItsOwnSection() {
    assertEquals("terrain", TerrainOptions.section().id());
    assertEquals(TerrainOptions.options(), TerrainOptions.section().options());
  }
}
