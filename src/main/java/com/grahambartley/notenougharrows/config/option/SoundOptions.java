package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.SoundConfig;
import java.util.List;

public final class SoundOptions {
  public static final float VOLUME_STEP = 0.05f;

  private SoundOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.SOUND, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
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
