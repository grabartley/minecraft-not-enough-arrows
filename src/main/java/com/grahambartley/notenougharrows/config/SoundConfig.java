package com.grahambartley.notenougharrows.config;

import com.google.gson.JsonObject;

public record SoundConfig(float volume) {

  public static final float VOLUME_MIN = 0.0f;
  public static final float VOLUME_MAX = 1.0f;

  public static final float DEFAULT_VOLUME = 1.0f;

  static final String KEY_VOLUME = "volume";

  public SoundConfig {
    volume = ConfigValues.clampFloat(volume, VOLUME_MIN, VOLUME_MAX);
  }

  public static SoundConfig defaults() {
    return new SoundConfig(DEFAULT_VOLUME);
  }

  public SoundConfig withVolume(final float value) {
    return new SoundConfig(value);
  }

  public static SoundConfig fromJson(final JsonObject root) {
    final SoundConfig defaults = defaults();
    return new SoundConfig(
        ConfigValues.readFloat(root, KEY_VOLUME, defaults.volume(), VOLUME_MIN, VOLUME_MAX));
  }

  public JsonObject toJson() {
    final JsonObject root = new JsonObject();
    root.addProperty(KEY_VOLUME, volume);
    return root;
  }
}
