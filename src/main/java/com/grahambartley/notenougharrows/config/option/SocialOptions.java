package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.SocialArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class SocialOptions {
  private SocialOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.SOCIAL, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.SOCIAL_COURIER_MAX_PAYLOAD,
            SocialArrowConfig.COURIER_MAX_PAYLOAD_MIN,
            SocialArrowConfig.COURIER_MAX_PAYLOAD_MAX,
            config -> config.social().courierMaxPayload(),
            (config, value) -> social(config, it -> it.withCourierMaxPayload(value))),
        new IdentifierListOption<>(
            ConfigSettings.SOCIAL_COURIER_UNDELIVERABLE,
            SocialArrowConfig.COURIER_UNDELIVERABLE_MAX,
            config -> config.social().courierUndeliverable(),
            (config, value) -> social(config, it -> it.withCourierUndeliverable(value))),
        new IntOption<>(
            ConfigSettings.SOCIAL_SNOW_GOLEM_LIFETIME_TICKS,
            SocialArrowConfig.SNOW_GOLEM_LIFETIME_TICKS_MIN,
            SocialArrowConfig.SNOW_GOLEM_LIFETIME_TICKS_MAX,
            config -> config.social().snowGolemLifetimeTicks(),
            (config, value) -> social(config, it -> it.withSnowGolemLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.SOCIAL_MAGNET_RADIUS,
            SocialArrowConfig.MAGNET_RADIUS_MIN,
            SocialArrowConfig.MAGNET_RADIUS_MAX,
            config -> config.social().magnetRadius(),
            (config, value) -> social(config, it -> it.withMagnetRadius(value))));
  }

  private static NotEnoughArrowsConfig social(
      final NotEnoughArrowsConfig config,
      final Function<SocialArrowConfig, SocialArrowConfig> change) {
    return config.withSocial(change.apply(config.social()));
  }
}
