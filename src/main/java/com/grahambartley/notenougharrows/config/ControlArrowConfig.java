package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record ControlArrowConfig(
    FrostArrowConfig frost,
    LevitationArrowConfig levitation,
    TargetingArrowConfig targeting,
    SmokeArrowConfig smoke,
    DisarmArrowConfig disarm) {

  static final String KEY_FROST = "frost";
  static final String KEY_LEVITATION = "levitation";
  static final String KEY_TARGETING = "targeting";
  static final String KEY_SMOKE = "smoke";
  static final String KEY_DISARM = "disarm";

  public ControlArrowConfig {
    frost = frost == null ? FrostArrowConfig.defaults() : frost;
    levitation = levitation == null ? LevitationArrowConfig.defaults() : levitation;
    targeting = targeting == null ? TargetingArrowConfig.defaults() : targeting;
    smoke = smoke == null ? SmokeArrowConfig.defaults() : smoke;
    disarm = disarm == null ? DisarmArrowConfig.defaults() : disarm;
  }

  public static ControlArrowConfig defaults() {
    return new ControlArrowConfig(
        FrostArrowConfig.defaults(),
        LevitationArrowConfig.defaults(),
        TargetingArrowConfig.defaults(),
        SmokeArrowConfig.defaults(),
        DisarmArrowConfig.defaults());
  }

  public ControlArrowConfig withFrost(final FrostArrowConfig value) {
    return new ControlArrowConfig(value, levitation, targeting, smoke, disarm);
  }

  public ControlArrowConfig withLevitation(final LevitationArrowConfig value) {
    return new ControlArrowConfig(frost, value, targeting, smoke, disarm);
  }

  public ControlArrowConfig withTargeting(final TargetingArrowConfig value) {
    return new ControlArrowConfig(frost, levitation, value, smoke, disarm);
  }

  public ControlArrowConfig withSmoke(final SmokeArrowConfig value) {
    return new ControlArrowConfig(frost, levitation, targeting, value, disarm);
  }

  public ControlArrowConfig withDisarm(final DisarmArrowConfig value) {
    return new ControlArrowConfig(frost, levitation, targeting, smoke, value);
  }

  public static ControlArrowConfig fromJson(final JsonObject root) {
    return new ControlArrowConfig(
        FrostArrowConfig.fromJson(ConfigValues.readObject(root, KEY_FROST)),
        LevitationArrowConfig.fromJson(ConfigValues.readObject(root, KEY_LEVITATION)),
        TargetingArrowConfig.fromJson(ConfigValues.readObject(root, KEY_TARGETING)),
        SmokeArrowConfig.fromJson(ConfigValues.readObject(root, KEY_SMOKE)),
        DisarmArrowConfig.fromJson(ConfigValues.readObject(root, KEY_DISARM)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_FROST, frost.toJson());
    root.add(KEY_LEVITATION, levitation.toJson());
    root.add(KEY_TARGETING, targeting.toJson());
    root.add(KEY_SMOKE, smoke.toJson());
    root.add(KEY_DISARM, disarm.toJson());
    return root;
  }
}
