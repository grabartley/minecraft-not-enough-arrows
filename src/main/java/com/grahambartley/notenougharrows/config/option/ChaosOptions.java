package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.BoomerangArrowConfig;
import com.grahambartley.notenougharrows.config.ChaosArrowConfig;
import com.grahambartley.notenougharrows.config.ChickenArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.PartyArrowConfig;
import com.grahambartley.notenougharrows.config.PolymorphArrowConfig;
import com.grahambartley.notenougharrows.config.PufferArrowConfig;
import com.grahambartley.notenougharrows.config.StinkArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class ChaosOptions {
  private ChaosOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.CHAOS, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
    return List.of(
        new BooleanOption<>(
            ConfigSettings.CHAOS_PARTY_ENABLED,
            config -> config.chaos().party().enabled(),
            (config, value) -> party(config, it -> it.withEnabled(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_CHICKEN_ENABLED,
            config -> config.chaos().chicken().enabled(),
            (config, value) -> chicken(config, it -> it.withEnabled(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_PUFFER_ENABLED,
            config -> config.chaos().puffer().enabled(),
            (config, value) -> puffer(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.CHAOS_PUFFER_DURATION_TICKS,
            PufferArrowConfig.DURATION_TICKS_MIN,
            PufferArrowConfig.DURATION_TICKS_MAX,
            config -> config.chaos().puffer().durationTicks(),
            (config, value) -> puffer(config, it -> it.withDurationTicks(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_STINK_ENABLED,
            config -> config.chaos().stink().enabled(),
            (config, value) -> stink(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.CHAOS_STINK_CLOUD_LIFETIME_TICKS,
            StinkArrowConfig.CLOUD_LIFETIME_TICKS_MIN,
            StinkArrowConfig.CLOUD_LIFETIME_TICKS_MAX,
            config -> config.chaos().stink().cloudLifetimeTicks(),
            (config, value) -> stink(config, it -> it.withCloudLifetimeTicks(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_BOOMERANG_ENABLED,
            config -> config.chaos().boomerang().enabled(),
            (config, value) -> boomerang(config, it -> it.withEnabled(value))),
        new BooleanOption<>(
            ConfigSettings.CHAOS_POLYMORPH_ENABLED,
            config -> config.chaos().polymorph().enabled(),
            (config, value) -> polymorph(config, it -> it.withEnabled(value))),
        new IntOption<>(
            ConfigSettings.CHAOS_POLYMORPH_DURATION_TICKS,
            PolymorphArrowConfig.DURATION_TICKS_MIN,
            PolymorphArrowConfig.DURATION_TICKS_MAX,
            config -> config.chaos().polymorph().durationTicks(),
            (config, value) -> polymorph(config, it -> it.withDurationTicks(value))));
  }

  private static NotEnoughArrowsConfig party(
      final NotEnoughArrowsConfig config,
      final Function<PartyArrowConfig, PartyArrowConfig> change) {
    return chaos(config, it -> it.withParty(change.apply(it.party())));
  }

  private static NotEnoughArrowsConfig chicken(
      final NotEnoughArrowsConfig config,
      final Function<ChickenArrowConfig, ChickenArrowConfig> change) {
    return chaos(config, it -> it.withChicken(change.apply(it.chicken())));
  }

  private static NotEnoughArrowsConfig puffer(
      final NotEnoughArrowsConfig config,
      final Function<PufferArrowConfig, PufferArrowConfig> change) {
    return chaos(config, it -> it.withPuffer(change.apply(it.puffer())));
  }

  private static NotEnoughArrowsConfig stink(
      final NotEnoughArrowsConfig config,
      final Function<StinkArrowConfig, StinkArrowConfig> change) {
    return chaos(config, it -> it.withStink(change.apply(it.stink())));
  }

  private static NotEnoughArrowsConfig boomerang(
      final NotEnoughArrowsConfig config,
      final Function<BoomerangArrowConfig, BoomerangArrowConfig> change) {
    return chaos(config, it -> it.withBoomerang(change.apply(it.boomerang())));
  }

  private static NotEnoughArrowsConfig polymorph(
      final NotEnoughArrowsConfig config,
      final Function<PolymorphArrowConfig, PolymorphArrowConfig> change) {
    return chaos(config, it -> it.withPolymorph(change.apply(it.polymorph())));
  }

  private static NotEnoughArrowsConfig chaos(
      final NotEnoughArrowsConfig config,
      final Function<ChaosArrowConfig, ChaosArrowConfig> change) {
    return config.withChaos(change.apply(config.chaos()));
  }
}
