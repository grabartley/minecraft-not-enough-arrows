package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.BoomerangArrowConfig;
import com.grahambartley.volley.config.ChaosArrowConfig;
import com.grahambartley.volley.config.ChickenArrowConfig;
import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.PartyArrowConfig;
import com.grahambartley.volley.config.PolymorphArrowConfig;
import com.grahambartley.volley.config.PufferArrowConfig;
import com.grahambartley.volley.config.StinkArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class ChaosOptions {
  private ChaosOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.CHAOS, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
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

  private static VolleyConfig party(
      final VolleyConfig config, final Function<PartyArrowConfig, PartyArrowConfig> change) {
    return chaos(config, it -> it.withParty(change.apply(it.party())));
  }

  private static VolleyConfig chicken(
      final VolleyConfig config, final Function<ChickenArrowConfig, ChickenArrowConfig> change) {
    return chaos(config, it -> it.withChicken(change.apply(it.chicken())));
  }

  private static VolleyConfig puffer(
      final VolleyConfig config, final Function<PufferArrowConfig, PufferArrowConfig> change) {
    return chaos(config, it -> it.withPuffer(change.apply(it.puffer())));
  }

  private static VolleyConfig stink(
      final VolleyConfig config, final Function<StinkArrowConfig, StinkArrowConfig> change) {
    return chaos(config, it -> it.withStink(change.apply(it.stink())));
  }

  private static VolleyConfig boomerang(
      final VolleyConfig config,
      final Function<BoomerangArrowConfig, BoomerangArrowConfig> change) {
    return chaos(config, it -> it.withBoomerang(change.apply(it.boomerang())));
  }

  private static VolleyConfig polymorph(
      final VolleyConfig config,
      final Function<PolymorphArrowConfig, PolymorphArrowConfig> change) {
    return chaos(config, it -> it.withPolymorph(change.apply(it.polymorph())));
  }

  private static VolleyConfig chaos(
      final VolleyConfig config, final Function<ChaosArrowConfig, ChaosArrowConfig> change) {
    return config.withChaos(change.apply(config.chaos()));
  }
}
