package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.TerrainArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class TerrainOptions {
  private TerrainOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.TERRAIN, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.TERRAIN_DRILL_ENABLED,
            config -> config.terrain().drillEnabled(),
            (config, value) -> terrain(config, it -> it.withDrillEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_DRILL_TOOL_TIER,
            TerrainArrowConfig.DRILL_TOOL_TIER_MIN,
            TerrainArrowConfig.DRILL_TOOL_TIER_MAX,
            config -> config.terrain().drillToolTier(),
            (config, value) -> terrain(config, it -> it.withDrillToolTier(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_PILLAR_ENABLED,
            config -> config.terrain().pillarEnabled(),
            (config, value) -> terrain(config, it -> it.withPillarEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_PILLAR_HEIGHT_BLOCKS,
            TerrainArrowConfig.PILLAR_HEIGHT_BLOCKS_MIN,
            TerrainArrowConfig.PILLAR_HEIGHT_BLOCKS_MAX,
            config -> config.terrain().pillarHeightBlocks(),
            (config, value) -> terrain(config, it -> it.withPillarHeightBlocks(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_PILLAR_LIFETIME_TICKS,
            TerrainArrowConfig.PILLAR_LIFETIME_TICKS_MIN,
            TerrainArrowConfig.PILLAR_LIFETIME_TICKS_MAX,
            config -> config.terrain().pillarLifetimeTicks(),
            (config, value) -> terrain(config, it -> it.withPillarLifetimeTicks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_DRAIN_ENABLED,
            config -> config.terrain().drainEnabled(),
            (config, value) -> terrain(config, it -> it.withDrainEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_DRAIN_RADIUS,
            TerrainArrowConfig.DRAIN_RADIUS_MIN,
            TerrainArrowConfig.DRAIN_RADIUS_MAX,
            config -> config.terrain().drainRadius(),
            (config, value) -> terrain(config, it -> it.withDrainRadius(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_DRAIN_MAX_BLOCKS,
            TerrainArrowConfig.DRAIN_MAX_BLOCKS_MIN,
            TerrainArrowConfig.DRAIN_MAX_BLOCKS_MAX,
            config -> config.terrain().drainMaxBlocks(),
            (config, value) -> terrain(config, it -> it.withDrainMaxBlocks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_FREEZE_ENABLED,
            config -> config.terrain().freezeEnabled(),
            (config, value) -> terrain(config, it -> it.withFreezeEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_FREEZE_RADIUS,
            TerrainArrowConfig.FREEZE_RADIUS_MIN,
            TerrainArrowConfig.FREEZE_RADIUS_MAX,
            config -> config.terrain().freezeRadius(),
            (config, value) -> terrain(config, it -> it.withFreezeRadius(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_WEB_ENABLED,
            config -> config.terrain().webEnabled(),
            (config, value) -> terrain(config, it -> it.withWebEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_WEB_PATCH_RADIUS,
            TerrainArrowConfig.WEB_PATCH_RADIUS_MIN,
            TerrainArrowConfig.WEB_PATCH_RADIUS_MAX,
            config -> config.terrain().webPatchRadius(),
            (config, value) -> terrain(config, it -> it.withWebPatchRadius(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_WEB_LIFETIME_TICKS,
            TerrainArrowConfig.WEB_LIFETIME_TICKS_MIN,
            TerrainArrowConfig.WEB_LIFETIME_TICKS_MAX,
            config -> config.terrain().webLifetimeTicks(),
            (config, value) -> terrain(config, it -> it.withWebLifetimeTicks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_PAINT_ENABLED,
            config -> config.terrain().paintEnabled(),
            (config, value) -> terrain(config, it -> it.withPaintEnabled(value))));
  }

  private static NotEnoughArrowsConfig terrain(
      final NotEnoughArrowsConfig config,
      final Function<TerrainArrowConfig, TerrainArrowConfig> change) {
    return config.withTerrain(change.apply(config.terrain()));
  }
}
