package com.grahambartley.volley.config.option;

import com.grahambartley.volley.config.ConfigSettings;
import com.grahambartley.volley.config.PhysicsArrowConfig;
import com.grahambartley.volley.config.VolleyConfig;
import java.util.List;
import java.util.function.Function;

public final class PhysicsOptions {

  private PhysicsOptions() {}

  private static final List<ConfigOption<VolleyConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<VolleyConfig> SECTION =
      new ConfigSection<>(ConfigSettings.PHYSICS, OPTIONS);

  public static ConfigSection<VolleyConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<VolleyConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<VolleyConfig>> buildOptions() {
    return List.of(
        new IntOption<>(
            ConfigSettings.PHYSICS_GRAVITY_IMPACT_RADIUS,
            PhysicsArrowConfig.GRAVITY_IMPACT_RADIUS_MIN,
            PhysicsArrowConfig.GRAVITY_IMPACT_RADIUS_MAX,
            config -> config.physics().gravityImpactRadius(),
            (config, value) -> physics(config, it -> it.withGravityImpactRadius(value))),
        new IdentifierListOption<>(
            ConfigSettings.PHYSICS_GRAVITY_BLOCK_EXCLUSIONS,
            PhysicsArrowConfig.GRAVITY_BLOCK_EXCLUSIONS_MAX,
            config -> config.physics().gravityBlockExclusions(),
            (config, value) -> physics(config, it -> it.withGravityBlockExclusions(value))),
        new IntOption<>(
            ConfigSettings.PHYSICS_RICOCHET_BOUNCE_COUNT,
            PhysicsArrowConfig.RICOCHET_BOUNCE_COUNT_MIN,
            PhysicsArrowConfig.RICOCHET_BOUNCE_COUNT_MAX,
            config -> config.physics().ricochetBounceCount(),
            (config, value) -> physics(config, it -> it.withRicochetBounceCount(value))),
        new BooleanOption<>(
            ConfigSettings.PHYSICS_RICOCHET_RETAINS_DAMAGE,
            config -> config.physics().ricochetRetainsDamage(),
            (config, value) -> physics(config, it -> it.withRicochetRetainsDamage(value))));
  }

  private static VolleyConfig physics(
      final VolleyConfig config, final Function<PhysicsArrowConfig, PhysicsArrowConfig> change) {
    return config.withPhysics(change.apply(config.physics()));
  }
}
