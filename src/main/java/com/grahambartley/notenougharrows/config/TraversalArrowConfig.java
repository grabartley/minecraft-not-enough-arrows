package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record TraversalArrowConfig(
    ZiplineArrowConfig zipline,
    TowArrowConfig tow,
    UpdraftArrowConfig updraft,
    VineArrowConfig vine,
    TrampolineArrowConfig trampoline,
    ScaffoldArrowConfig scaffold,
    BridgeArrowConfig bridge) {

  static final String KEY_ZIPLINE = "zipline";
  static final String KEY_TOW = "tow";
  static final String KEY_UPDRAFT = "updraft";
  static final String KEY_VINE = "vine";
  static final String KEY_TRAMPOLINE = "trampoline";
  static final String KEY_SCAFFOLD = "scaffold";
  static final String KEY_BRIDGE = "bridge";

  public TraversalArrowConfig {
    zipline = zipline == null ? ZiplineArrowConfig.defaults() : zipline;
    tow = tow == null ? TowArrowConfig.defaults() : tow;
    updraft = updraft == null ? UpdraftArrowConfig.defaults() : updraft;
    vine = vine == null ? VineArrowConfig.defaults() : vine;
    trampoline = trampoline == null ? TrampolineArrowConfig.defaults() : trampoline;
    scaffold = scaffold == null ? ScaffoldArrowConfig.defaults() : scaffold;
    bridge = bridge == null ? BridgeArrowConfig.defaults() : bridge;
  }

  public static TraversalArrowConfig defaults() {
    return new TraversalArrowConfig(
        ZiplineArrowConfig.defaults(),
        TowArrowConfig.defaults(),
        UpdraftArrowConfig.defaults(),
        VineArrowConfig.defaults(),
        TrampolineArrowConfig.defaults(),
        ScaffoldArrowConfig.defaults(),
        BridgeArrowConfig.defaults());
  }

  public TraversalArrowConfig withZipline(final ZiplineArrowConfig value) {
    return new TraversalArrowConfig(value, tow, updraft, vine, trampoline, scaffold, bridge);
  }

  public TraversalArrowConfig withTow(final TowArrowConfig value) {
    return new TraversalArrowConfig(zipline, value, updraft, vine, trampoline, scaffold, bridge);
  }

  public TraversalArrowConfig withUpdraft(final UpdraftArrowConfig value) {
    return new TraversalArrowConfig(zipline, tow, value, vine, trampoline, scaffold, bridge);
  }

  public TraversalArrowConfig withVine(final VineArrowConfig value) {
    return new TraversalArrowConfig(zipline, tow, updraft, value, trampoline, scaffold, bridge);
  }

  public TraversalArrowConfig withTrampoline(final TrampolineArrowConfig value) {
    return new TraversalArrowConfig(zipline, tow, updraft, vine, value, scaffold, bridge);
  }

  public TraversalArrowConfig withScaffold(final ScaffoldArrowConfig value) {
    return new TraversalArrowConfig(zipline, tow, updraft, vine, trampoline, value, bridge);
  }

  public TraversalArrowConfig withBridge(final BridgeArrowConfig value) {
    return new TraversalArrowConfig(zipline, tow, updraft, vine, trampoline, scaffold, value);
  }

  public static TraversalArrowConfig fromJson(final JsonObject root) {
    return new TraversalArrowConfig(
        ZiplineArrowConfig.fromJson(ConfigValues.readObject(root, KEY_ZIPLINE)),
        TowArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TOW)),
        UpdraftArrowConfig.fromJson(ConfigValues.readObject(root, KEY_UPDRAFT)),
        VineArrowConfig.fromJson(ConfigValues.readObject(root, KEY_VINE)),
        TrampolineArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TRAMPOLINE)),
        ScaffoldArrowConfig.fromJson(ConfigValues.readObject(root, KEY_SCAFFOLD)),
        BridgeArrowConfig.fromJson(ConfigValues.readObject(root, KEY_BRIDGE)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_ZIPLINE, zipline.toJson());
    root.add(KEY_TOW, tow.toJson());
    root.add(KEY_UPDRAFT, updraft.toJson());
    root.add(KEY_VINE, vine.toJson());
    root.add(KEY_TRAMPOLINE, trampoline.toJson());
    root.add(KEY_SCAFFOLD, scaffold.toJson());
    root.add(KEY_BRIDGE, bridge.toJson());
    return root;
  }
}
