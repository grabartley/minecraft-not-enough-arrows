package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record CombatArrowConfig(
    ShockArrowConfig shock,
    LifestealArrowConfig lifesteal,
    StatusArrowConfig status,
    HomingArrowConfig homing,
    VolleyArrowConfig volley,
    RailgunArrowConfig railgun) {

  static final String KEY_SHOCK = "shock";
  static final String KEY_LIFESTEAL = "lifesteal";
  static final String KEY_STATUS = "status";
  static final String KEY_HOMING = "homing";
  static final String KEY_VOLLEY = "volley";
  static final String KEY_RAILGUN = "railgun";

  public CombatArrowConfig {
    shock = shock == null ? ShockArrowConfig.defaults() : shock;
    lifesteal = lifesteal == null ? LifestealArrowConfig.defaults() : lifesteal;
    status = status == null ? StatusArrowConfig.defaults() : status;
    homing = homing == null ? HomingArrowConfig.defaults() : homing;
    volley = volley == null ? VolleyArrowConfig.defaults() : volley;
    railgun = railgun == null ? RailgunArrowConfig.defaults() : railgun;
  }

  public static CombatArrowConfig defaults() {
    return new CombatArrowConfig(
        ShockArrowConfig.defaults(),
        LifestealArrowConfig.defaults(),
        StatusArrowConfig.defaults(),
        HomingArrowConfig.defaults(),
        VolleyArrowConfig.defaults(),
        RailgunArrowConfig.defaults());
  }

  public CombatArrowConfig withShock(final ShockArrowConfig value) {
    return new CombatArrowConfig(value, lifesteal, status, homing, volley, railgun);
  }

  public CombatArrowConfig withLifesteal(final LifestealArrowConfig value) {
    return new CombatArrowConfig(shock, value, status, homing, volley, railgun);
  }

  public CombatArrowConfig withStatus(final StatusArrowConfig value) {
    return new CombatArrowConfig(shock, lifesteal, value, homing, volley, railgun);
  }

  public CombatArrowConfig withHoming(final HomingArrowConfig value) {
    return new CombatArrowConfig(shock, lifesteal, status, value, volley, railgun);
  }

  public CombatArrowConfig withVolley(final VolleyArrowConfig value) {
    return new CombatArrowConfig(shock, lifesteal, status, homing, value, railgun);
  }

  public CombatArrowConfig withRailgun(final RailgunArrowConfig value) {
    return new CombatArrowConfig(shock, lifesteal, status, homing, volley, value);
  }

  public static CombatArrowConfig fromJson(final JsonObject root) {
    return new CombatArrowConfig(
        ShockArrowConfig.fromJson(ConfigValues.readObject(root, KEY_SHOCK)),
        LifestealArrowConfig.fromJson(ConfigValues.readObject(root, KEY_LIFESTEAL)),
        StatusArrowConfig.fromJson(ConfigValues.readObject(root, KEY_STATUS)),
        HomingArrowConfig.fromJson(ConfigValues.readObject(root, KEY_HOMING)),
        VolleyArrowConfig.fromJson(ConfigValues.readObject(root, KEY_VOLLEY)),
        RailgunArrowConfig.fromJson(ConfigValues.readObject(root, KEY_RAILGUN)));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.add(KEY_SHOCK, shock.toJson());
    root.add(KEY_LIFESTEAL, lifesteal.toJson());
    root.add(KEY_STATUS, status.toJson());
    root.add(KEY_HOMING, homing.toJson());
    root.add(KEY_VOLLEY, volley.toJson());
    root.add(KEY_RAILGUN, railgun.toJson());
    return root;
  }
}
