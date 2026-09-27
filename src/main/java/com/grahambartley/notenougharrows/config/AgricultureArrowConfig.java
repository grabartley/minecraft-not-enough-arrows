package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record AgricultureArrowConfig(
    BlossomArrowConfig blossom,
    TillArrowConfig till,
    HarvestArrowConfig harvest,
    BeeArrowConfig bee) {

  static final String KEY_BLOSSOM = "blossom";
  static final String KEY_TILL = "till";
  static final String KEY_HARVEST = "harvest";
  static final String KEY_BEE = "bee";

  public AgricultureArrowConfig {
    blossom = blossom == null ? BlossomArrowConfig.defaults() : blossom;
    till = till == null ? TillArrowConfig.defaults() : till;
    harvest = harvest == null ? HarvestArrowConfig.defaults() : harvest;
    bee = bee == null ? BeeArrowConfig.defaults() : bee;
  }

  public static AgricultureArrowConfig defaults() {
    return new AgricultureArrowConfig(
        BlossomArrowConfig.defaults(),
        TillArrowConfig.defaults(),
        HarvestArrowConfig.defaults(),
        BeeArrowConfig.defaults());
  }

  public AgricultureArrowConfig withBlossom(final BlossomArrowConfig value) {
    return new AgricultureArrowConfig(value, till, harvest, bee);
  }

  public AgricultureArrowConfig withTill(final TillArrowConfig value) {
    return new AgricultureArrowConfig(blossom, value, harvest, bee);
  }

  public AgricultureArrowConfig withHarvest(final HarvestArrowConfig value) {
    return new AgricultureArrowConfig(blossom, till, value, bee);
  }

  public AgricultureArrowConfig withBee(final BeeArrowConfig value) {
    return new AgricultureArrowConfig(blossom, till, harvest, value);
  }

  public static AgricultureArrowConfig fromJson(final JsonObject root) {
    return new AgricultureArrowConfig(
        BlossomArrowConfig.fromJson(ConfigValues.readObject(root, KEY_BLOSSOM)),
        TillArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TILL)),
        HarvestArrowConfig.fromJson(ConfigValues.readObject(root, KEY_HARVEST)),
        BeeArrowConfig.fromJson(ConfigValues.readObject(root, KEY_BEE)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_BLOSSOM, blossom.toJson());
    root.add(KEY_TILL, till.toJson());
    root.add(KEY_HARVEST, harvest.toJson());
    root.add(KEY_BEE, bee.toJson());
    return root;
  }
}
