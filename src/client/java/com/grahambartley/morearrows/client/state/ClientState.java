package com.grahambartley.morearrows.client.state;

import com.google.gson.JsonObject;
import com.grahambartley.morearrows.config.ConfigValues;

public record ClientState(
    boolean showCountdownHud, boolean playCountdownSound, float countdownHudScale) {

  public static final float COUNTDOWN_HUD_SCALE_MIN = 0.5f;
  public static final float COUNTDOWN_HUD_SCALE_MAX = 2.0f;

  public static final boolean DEFAULT_SHOW_COUNTDOWN_HUD = true;
  public static final boolean DEFAULT_PLAY_COUNTDOWN_SOUND = true;
  public static final float DEFAULT_COUNTDOWN_HUD_SCALE = 1.0f;

  static final String KEY_SHOW_COUNTDOWN_HUD = "showCountdownHud";
  static final String KEY_PLAY_COUNTDOWN_SOUND = "playCountdownSound";
  static final String KEY_COUNTDOWN_HUD_SCALE = "countdownHudScale";

  public ClientState {
    countdownHudScale =
        ConfigValues.clampFloat(
            countdownHudScale, COUNTDOWN_HUD_SCALE_MIN, COUNTDOWN_HUD_SCALE_MAX);
  }

  public static ClientState defaults() {
    return new ClientState(
        DEFAULT_SHOW_COUNTDOWN_HUD, DEFAULT_PLAY_COUNTDOWN_SOUND, DEFAULT_COUNTDOWN_HUD_SCALE);
  }

  public ClientState withShowCountdownHud(final boolean value) {
    return new ClientState(value, playCountdownSound, countdownHudScale);
  }

  public ClientState withPlayCountdownSound(final boolean value) {
    return new ClientState(showCountdownHud, value, countdownHudScale);
  }

  public ClientState withCountdownHudScale(final float value) {
    return new ClientState(showCountdownHud, playCountdownSound, value);
  }

  public static ClientState fromJson(final JsonObject root) {
    final ClientState defaults = defaults();
    return new ClientState(
        ConfigValues.readBoolean(root, KEY_SHOW_COUNTDOWN_HUD, defaults.showCountdownHud()),
        ConfigValues.readBoolean(root, KEY_PLAY_COUNTDOWN_SOUND, defaults.playCountdownSound()),
        ConfigValues.readFloat(
            root,
            KEY_COUNTDOWN_HUD_SCALE,
            defaults.countdownHudScale(),
            COUNTDOWN_HUD_SCALE_MIN,
            COUNTDOWN_HUD_SCALE_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_SHOW_COUNTDOWN_HUD, showCountdownHud);
    root.addProperty(KEY_PLAY_COUNTDOWN_SOUND, playCountdownSound);
    root.addProperty(KEY_COUNTDOWN_HUD_SCALE, countdownHudScale);
    return root;
  }
}
