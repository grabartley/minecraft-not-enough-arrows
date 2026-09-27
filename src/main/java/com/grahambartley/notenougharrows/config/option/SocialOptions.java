package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.CourierArrowConfig;
import com.grahambartley.notenougharrows.config.MagnetArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.SnowGolemArrowConfig;
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
            CourierArrowConfig.MAX_PAYLOAD_MIN,
            CourierArrowConfig.MAX_PAYLOAD_MAX,
            config -> config.social().courier().maxPayload(),
            (config, value) -> courier(config, it -> it.withMaxPayload(value))),
        new IdentifierListOption<>(
            ConfigSettings.SOCIAL_COURIER_UNDELIVERABLE,
            CourierArrowConfig.UNDELIVERABLE_MAX,
            config -> config.social().courier().undeliverable(),
            (config, value) -> courier(config, it -> it.withUndeliverable(value))),
        new IntOption<>(
            ConfigSettings.SOCIAL_SNOWGOLEM_LIFETIME_TICKS,
            SnowGolemArrowConfig.LIFETIME_TICKS_MIN,
            SnowGolemArrowConfig.LIFETIME_TICKS_MAX,
            config -> config.social().snowGolem().lifetimeTicks(),
            (config, value) -> snowGolem(config, it -> it.withLifetimeTicks(value))),
        new IntOption<>(
            ConfigSettings.SOCIAL_MAGNET_RADIUS,
            MagnetArrowConfig.RADIUS_MIN,
            MagnetArrowConfig.RADIUS_MAX,
            config -> config.social().magnet().radius(),
            (config, value) -> magnet(config, it -> it.withRadius(value))));
  }

  private static NotEnoughArrowsConfig courier(
      final NotEnoughArrowsConfig config,
      final Function<CourierArrowConfig, CourierArrowConfig> change) {
    return social(config, it -> it.withCourier(change.apply(it.courier())));
  }

  private static NotEnoughArrowsConfig snowGolem(
      final NotEnoughArrowsConfig config,
      final Function<SnowGolemArrowConfig, SnowGolemArrowConfig> change) {
    return social(config, it -> it.withSnowGolem(change.apply(it.snowGolem())));
  }

  private static NotEnoughArrowsConfig magnet(
      final NotEnoughArrowsConfig config,
      final Function<MagnetArrowConfig, MagnetArrowConfig> change) {
    return social(config, it -> it.withMagnet(change.apply(it.magnet())));
  }

  private static NotEnoughArrowsConfig social(
      final NotEnoughArrowsConfig config,
      final Function<SocialArrowConfig, SocialArrowConfig> change) {
    return config.withSocial(change.apply(config.social()));
  }
}
