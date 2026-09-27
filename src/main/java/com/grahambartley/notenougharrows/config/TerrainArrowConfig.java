package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TerrainArrowConfig(
    DrillArrowConfig drill,
    PillarArrowConfig pillar,
    DrainArrowConfig drain,
    FreezeArrowConfig freeze,
    WebArrowConfig web,
    PaintArrowConfig paint) {

  static final String KEY_DRILL = "drill";
  static final String KEY_PILLAR = "pillar";
  static final String KEY_DRAIN = "drain";
  static final String KEY_FREEZE = "freeze";
  static final String KEY_WEB = "web";
  static final String KEY_PAINT = "paint";

  public TerrainArrowConfig {
    drill = drill == null ? DrillArrowConfig.defaults() : drill;
    pillar = pillar == null ? PillarArrowConfig.defaults() : pillar;
    drain = drain == null ? DrainArrowConfig.defaults() : drain;
    freeze = freeze == null ? FreezeArrowConfig.defaults() : freeze;
    web = web == null ? WebArrowConfig.defaults() : web;
    paint = paint == null ? PaintArrowConfig.defaults() : paint;
  }

  public static TerrainArrowConfig defaults() {
    return new TerrainArrowConfig(
        DrillArrowConfig.defaults(),
        PillarArrowConfig.defaults(),
        DrainArrowConfig.defaults(),
        FreezeArrowConfig.defaults(),
        WebArrowConfig.defaults(),
        PaintArrowConfig.defaults());
  }

  public TerrainArrowConfig withDrill(final DrillArrowConfig value) {
    return new TerrainArrowConfig(value, pillar, drain, freeze, web, paint);
  }

  public TerrainArrowConfig withPillar(final PillarArrowConfig value) {
    return new TerrainArrowConfig(drill, value, drain, freeze, web, paint);
  }

  public TerrainArrowConfig withDrain(final DrainArrowConfig value) {
    return new TerrainArrowConfig(drill, pillar, value, freeze, web, paint);
  }

  public TerrainArrowConfig withFreeze(final FreezeArrowConfig value) {
    return new TerrainArrowConfig(drill, pillar, drain, value, web, paint);
  }

  public TerrainArrowConfig withWeb(final WebArrowConfig value) {
    return new TerrainArrowConfig(drill, pillar, drain, freeze, value, paint);
  }

  public TerrainArrowConfig withPaint(final PaintArrowConfig value) {
    return new TerrainArrowConfig(drill, pillar, drain, freeze, web, value);
  }

  public static TerrainArrowConfig fromJson(final JsonObject root) {
    return new TerrainArrowConfig(
        DrillArrowConfig.fromJson(ConfigValues.readObject(root, KEY_DRILL)),
        PillarArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PILLAR)),
        DrainArrowConfig.fromJson(ConfigValues.readObject(root, KEY_DRAIN)),
        FreezeArrowConfig.fromJson(ConfigValues.readObject(root, KEY_FREEZE)),
        WebArrowConfig.fromJson(ConfigValues.readObject(root, KEY_WEB)),
        PaintArrowConfig.fromJson(ConfigValues.readObject(root, KEY_PAINT)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_DRILL, drill.toJson());
    root.add(KEY_PILLAR, pillar.toJson());
    root.add(KEY_DRAIN, drain.toJson());
    root.add(KEY_FREEZE, freeze.toJson());
    root.add(KEY_WEB, web.toJson());
    root.add(KEY_PAINT, paint.toJson());
    return root;
  }
}
