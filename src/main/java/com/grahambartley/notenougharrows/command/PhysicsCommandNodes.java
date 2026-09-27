package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.PhysicsArrowConfig;
import com.grahambartley.notenougharrows.config.option.PhysicsOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class PhysicsCommandNodes {
  private PhysicsCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.PHYSICS)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.PHYSICS_GRAVITY_IMPACT_RADIUS,
                PhysicsArrowConfig.GRAVITY_IMPACT_RADIUS_MIN,
                PhysicsArrowConfig.GRAVITY_IMPACT_RADIUS_MAX,
                (current, value) ->
                    change(current, physics -> physics.withGravityImpactRadius(value))))
        .then(IdentifierListNodes.build(PhysicsOptions.GRAVITY_BLOCK_EXCLUSIONS))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.PHYSICS_RICOCHET_BOUNCE_COUNT,
                PhysicsArrowConfig.RICOCHET_BOUNCE_COUNT_MIN,
                PhysicsArrowConfig.RICOCHET_BOUNCE_COUNT_MAX,
                (current, value) ->
                    change(current, physics -> physics.withRicochetBounceCount(value))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.PHYSICS_RICOCHET_RETAINS_DAMAGE,
                (current, value) ->
                    change(current, physics -> physics.withRicochetRetainsDamage(value))));
  }

  private static NotEnoughArrowsConfig change(
      final NotEnoughArrowsConfig current, final UnaryOperator<PhysicsArrowConfig> change) {
    return current.withPhysics(change.apply(current.physics()));
  }
}
