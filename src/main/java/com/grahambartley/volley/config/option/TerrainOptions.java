package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.DrainArrowConfig;
import com.grahambartley.volley.config.DrillArrowConfig;
import com.grahambartley.volley.config.FreezeArrowConfig;
import com.grahambartley.volley.config.PaintArrowConfig;
import com.grahambartley.volley.config.PillarArrowConfig;
import com.grahambartley.volley.config.TerrainArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.config.WebArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class TerrainOptions {
  private TerrainOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.TERRAIN, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.TERRAIN_DRILL_ENABLED,
            config -> config.terrain().drill().enabled(),
            (config, value) -> drill(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_DRILL_TOOL_TIER,
            DrillArrowConfig.TOOL_TIER_MIN,
            DrillArrowConfig.TOOL_TIER_MAX,
            config -> config.terrain().drill().toolTier(),
            (config, value) -> drill(config, it -> it.withToolTier(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_PILLAR_ENABLED,
            config -> config.terrain().pillar().enabled(),
            (config, value) -> pillar(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_PILLAR_HEIGHT_BLOCKS,
            PillarArrowConfig.HEIGHT_BLOCKS_MIN,
            PillarArrowConfig.HEIGHT_BLOCKS_MAX,
            config -> config.terrain().pillar().heightBlocks(),
            (config, value) -> pillar(config, it -> it.withHeightBlocks(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_PILLAR_LIFETIME_TICKS,
            PillarArrowConfig.LIFETIME_TICKS_MIN,
            PillarArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.terrain().pillar().lifetimeTicks(),
            (config, value) -> pillar(config, it -> it.withLifetimeTicks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_DRAIN_ENABLED,
            config -> config.terrain().drain().enabled(),
            (config, value) -> drain(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_DRAIN_RADIUS,
            DrainArrowConfig.RADIUS_MIN,
            DrainArrowConfig.RADIUS_MAX,
            config -> config.terrain().drain().radius(),
            (config, value) -> drain(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_DRAIN_MAX_BLOCKS,
            DrainArrowConfig.MAX_BLOCKS_MIN,
            DrainArrowConfig.MAX_BLOCKS_MAX,
            config -> config.terrain().drain().maxBlocks(),
            (config, value) -> drain(config, it -> it.withMaxBlocks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_FREEZE_ENABLED,
            config -> config.terrain().freeze().enabled(),
            (config, value) -> freeze(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_FREEZE_RADIUS,
            FreezeArrowConfig.RADIUS_MIN,
            FreezeArrowConfig.RADIUS_MAX,
            config -> config.terrain().freeze().radius(),
            (config, value) -> freeze(config, it -> it.withRadius(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_FREEZE_MAX_BLOCKS,
            FreezeArrowConfig.MAX_BLOCKS_MIN,
            FreezeArrowConfig.MAX_BLOCKS_MAX,
            config -> config.terrain().freeze().maxBlocks(),
            (config, value) -> freeze(config, it -> it.withMaxBlocks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_WEB_ENABLED,
            config -> config.terrain().web().enabled(),
            (config, value) -> web(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_WEB_PATCH_RADIUS,
            WebArrowConfig.PATCH_RADIUS_MIN,
            WebArrowConfig.PATCH_RADIUS_MAX,
            config -> config.terrain().web().patchRadius(),
            (config, value) -> web(config, it -> it.withPatchRadius(value))),
        new IntOption<>(
            ConfigSettings.TERRAIN_WEB_LIFETIME_TICKS,
            WebArrowConfig.LIFETIME_TICKS_MIN,
            WebArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.terrain().web().lifetimeTicks(),
            (config, value) -> web(config, it -> it.withLifetimeTicks(value))),
        new BooleanOption<>(
            ConfigSettings.TERRAIN_PAINT_ENABLED,
            config -> config.terrain().paint().enabled(),
            (config, value) -> paint(config, it -> it.withEnabled(value))));
  }

  private static VolleyConfig drill(
      final VolleyConfig config, final Function<DrillArrowConfig, DrillArrowConfig> change) {
    return terrain(config, it -> it.withDrill(change.apply(it.drill())));
  }

  private static VolleyConfig pillar(
      final VolleyConfig config, final Function<PillarArrowConfig, PillarArrowConfig> change) {
    return terrain(config, it -> it.withPillar(change.apply(it.pillar())));
  }

  private static VolleyConfig drain(
      final VolleyConfig config, final Function<DrainArrowConfig, DrainArrowConfig> change) {
    return terrain(config, it -> it.withDrain(change.apply(it.drain())));
  }

  private static VolleyConfig freeze(
      final VolleyConfig config, final Function<FreezeArrowConfig, FreezeArrowConfig> change) {
    return terrain(config, it -> it.withFreeze(change.apply(it.freeze())));
  }

  private static VolleyConfig web(
      final VolleyConfig config, final Function<WebArrowConfig, WebArrowConfig> change) {
    return terrain(config, it -> it.withWeb(change.apply(it.web())));
  }

  private static VolleyConfig paint(
      final VolleyConfig config, final Function<PaintArrowConfig, PaintArrowConfig> change) {
    return terrain(config, it -> it.withPaint(change.apply(it.paint())));
  }

  private static VolleyConfig terrain(
      final VolleyConfig config, final Function<TerrainArrowConfig, TerrainArrowConfig> change) {
    return config.withTerrain(change.apply(config.terrain()));
  }
}
