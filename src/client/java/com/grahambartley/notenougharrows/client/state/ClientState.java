package com.grahambartley.notenougharrows.client.state;

import com.google.gson.JsonObject;
import com.grahambartley.notenougharrows.config.ConfigValues;

public record ClientState(
    boolean showCountdownRing,
    boolean playCountdownSound,
    float countdownRingScale,
    float modSoundVolume) {

  public static final float COUNTDOWN_RING_SCALE_MIN = 0.5f;
  public static final float COUNTDOWN_RING_SCALE_MAX = 2.0f;
  public static final float MOD_SOUND_VOLUME_MIN = 0.0f;
  public static final float MOD_SOUND_VOLUME_MAX = 1.0f;

  public static final boolean DEFAULT_SHOW_COUNTDOWN_RING = true;
  public static final boolean DEFAULT_PLAY_COUNTDOWN_SOUND = true;
  public static final float DEFAULT_COUNTDOWN_RING_SCALE = 1.0f;
  public static final float DEFAULT_MOD_SOUND_VOLUME = 1.0f;

  static final String KEY_SHOW_COUNTDOWN_RING = "showCountdownRing";
  static final String KEY_PLAY_COUNTDOWN_SOUND = "playCountdownSound";
  static final String KEY_COUNTDOWN_RING_SCALE = "countdownRingScale";
  static final String KEY_MOD_SOUND_VOLUME = "modSoundVolume";

  public ClientState {
    countdownRingScale =
        ConfigValues.clampFloat(
            countdownRingScale, COUNTDOWN_RING_SCALE_MIN, COUNTDOWN_RING_SCALE_MAX);
    modSoundVolume =
        ConfigValues.clampFloat(modSoundVolume, MOD_SOUND_VOLUME_MIN, MOD_SOUND_VOLUME_MAX);
  }

  public static ClientState defaults() {
    return new ClientState(
        DEFAULT_SHOW_COUNTDOWN_RING,
        DEFAULT_PLAY_COUNTDOWN_SOUND,
        DEFAULT_COUNTDOWN_RING_SCALE,
        DEFAULT_MOD_SOUND_VOLUME);
  }

  public ClientState withShowCountdownRing(final boolean value) {
    return new ClientState(value, playCountdownSound, countdownRingScale, modSoundVolume);
  }

  public ClientState withPlayCountdownSound(final boolean value) {
    return new ClientState(showCountdownRing, value, countdownRingScale, modSoundVolume);
  }

  public ClientState withCountdownRingScale(final float value) {
    return new ClientState(showCountdownRing, playCountdownSound, value, modSoundVolume);
  }

  public ClientState withModSoundVolume(final float value) {
    return new ClientState(showCountdownRing, playCountdownSound, countdownRingScale, value);
  }

  public static ClientState fromJson(final JsonObject root) {
    final ClientState defaults = defaults();
    return new ClientState(
        ConfigValues.readBoolean(root, KEY_SHOW_COUNTDOWN_RING, defaults.showCountdownRing()),
        ConfigValues.readBoolean(root, KEY_PLAY_COUNTDOWN_SOUND, defaults.playCountdownSound()),
        ConfigValues.readFloat(
            root,
            KEY_COUNTDOWN_RING_SCALE,
            defaults.countdownRingScale(),
            COUNTDOWN_RING_SCALE_MIN,
            COUNTDOWN_RING_SCALE_MAX),
        ConfigValues.readFloat(
            root,
            KEY_MOD_SOUND_VOLUME,
            defaults.modSoundVolume(),
            MOD_SOUND_VOLUME_MIN,
            MOD_SOUND_VOLUME_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_SHOW_COUNTDOWN_RING, showCountdownRing);
    root.addProperty(KEY_PLAY_COUNTDOWN_SOUND, playCountdownSound);
    root.addProperty(KEY_COUNTDOWN_RING_SCALE, countdownRingScale);
    root.addProperty(KEY_MOD_SOUND_VOLUME, modSoundVolume);
    return root;
  }
}
