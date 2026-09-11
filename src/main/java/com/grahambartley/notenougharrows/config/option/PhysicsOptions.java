package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.PhysicsArrowConfig;
import java.util.List;
import java.util.function.Function;

public final class PhysicsOptions {

  private PhysicsOptions() {}

  private static final List<ConfigOption<NotEnoughArrowsConfig>> OPTIONS = buildOptions();
  private static final ConfigSection<NotEnoughArrowsConfig> SECTION =
      new ConfigSection<>(ConfigSettings.PHYSICS, OPTIONS);

  public static ConfigSection<NotEnoughArrowsConfig> section() {
    return SECTION;
  }

  public static List<ConfigOption<NotEnoughArrowsConfig>> options() {
    return OPTIONS;
  }

  private static List<ConfigOption<NotEnoughArrowsConfig>> buildOptions() {
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

  private static NotEnoughArrowsConfig physics(
      final NotEnoughArrowsConfig config,
      final Function<PhysicsArrowConfig, PhysicsArrowConfig> change) {
    return config.withPhysics(change.apply(config.physics()));
  }
}
