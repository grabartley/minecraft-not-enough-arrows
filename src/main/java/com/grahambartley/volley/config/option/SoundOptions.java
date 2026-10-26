package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.SoundConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;

public final class SoundOptions {
  public static final float VOLUME_STEP = 0.05f;

  private SoundOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.SOUND, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new FloatOption<>(
            ConfigSettings.SOUND_VOLUME,
            SoundConfig.VOLUME_MIN,
            SoundConfig.VOLUME_MAX,
            VOLUME_STEP,
            config -> config.sound().volume(),
            (config, value) -> config.withSound(config.sound().withVolume(value))));
  }
}
