package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.CombatArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.HomingArrowConfig;
import com.grahambartley.notenougharrows.config.LifestealArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.RailgunArrowConfig;
import com.grahambartley.notenougharrows.config.ShockArrowConfig;
import com.grahambartley.notenougharrows.config.StatusArrowConfig;
import com.grahambartley.notenougharrows.config.VolleyArrowConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class CombatCommandNodes {
  private CombatCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT)
        .then(shock())
        .then(lifesteal())
        .then(status())
        .then(homing())
        .then(volley())
        .then(railgun());
  }

  private static LiteralArgumentBuilder<ServerCommandSource> shock() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT_SHOCK)
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_SHOCK_ARC_RADIUS,
                ShockArrowConfig.ARC_RADIUS_MIN,
                ShockArrowConfig.ARC_RADIUS_MAX,
                (current, value) ->
                    change(
                        current, combat -> combat.withShock(combat.shock().withArcRadius(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_SHOCK_DAMAGE,
                ShockArrowConfig.DAMAGE_MIN,
                ShockArrowConfig.DAMAGE_MAX,
                (current, value) ->
                    change(current, combat -> combat.withShock(combat.shock().withDamage(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> lifesteal() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT_LIFESTEAL)
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_LIFESTEAL_SHARE,
                LifestealArrowConfig.SHARE_MIN,
                LifestealArrowConfig.SHARE_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withLifesteal(combat.lifesteal().withShare(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_LIFESTEAL_MAX_HEAL_PER_HIT,
                LifestealArrowConfig.MAX_HEAL_PER_HIT_MIN,
                LifestealArrowConfig.MAX_HEAL_PER_HIT_MAX,
                (current, value) ->
                    change(
                        current,
                        combat ->
                            combat.withLifesteal(combat.lifesteal().withMaxHealPerHit(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> status() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT_STATUS)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.COMBAT_STATUS_RUST_DURATION_TICKS,
                StatusArrowConfig.DURATION_TICKS_MIN,
                StatusArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withStatus(combat.status().withRustDurationTicks(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.COMBAT_STATUS_HASTE_DURATION_TICKS,
                StatusArrowConfig.DURATION_TICKS_MIN,
                StatusArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        combat ->
                            combat.withStatus(combat.status().withHasteDurationTicks(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.COMBAT_STATUS_GUARD_DURATION_TICKS,
                StatusArrowConfig.DURATION_TICKS_MIN,
                StatusArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        combat ->
                            combat.withStatus(combat.status().withGuardDurationTicks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> homing() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT_HOMING)
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_HOMING_TURN_RATE,
                HomingArrowConfig.TURN_RATE_MIN,
                HomingArrowConfig.TURN_RATE_MAX,
                (current, value) ->
                    change(
                        current, combat -> combat.withHoming(combat.homing().withTurnRate(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_HOMING_SEARCH_RADIUS,
                HomingArrowConfig.SEARCH_RADIUS_MIN,
                HomingArrowConfig.SEARCH_RADIUS_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withHoming(combat.homing().withSearchRadius(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_HOMING_SEARCH_CONE_DEGREES,
                HomingArrowConfig.SEARCH_CONE_DEGREES_MIN,
                HomingArrowConfig.SEARCH_CONE_DEGREES_MAX,
                (current, value) ->
                    change(
                        current,
                        combat ->
                            combat.withHoming(combat.homing().withSearchConeDegrees(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> volley() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT_VOLLEY)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.COMBAT_VOLLEY_FRAGMENT_COUNT,
                VolleyArrowConfig.FRAGMENT_COUNT_MIN,
                VolleyArrowConfig.FRAGMENT_COUNT_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withVolley(combat.volley().withFragmentCount(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_VOLLEY_DAMAGE_SHARE,
                VolleyArrowConfig.DAMAGE_SHARE_MIN,
                VolleyArrowConfig.DAMAGE_SHARE_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withVolley(combat.volley().withDamageShare(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_VOLLEY_SPREAD_DEGREES,
                VolleyArrowConfig.SPREAD_DEGREES_MIN,
                VolleyArrowConfig.SPREAD_DEGREES_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withVolley(combat.volley().withSpreadDegrees(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.COMBAT_VOLLEY_SPLIT_DELAY_TICKS,
                VolleyArrowConfig.SPLIT_DELAY_TICKS_MIN,
                VolleyArrowConfig.SPLIT_DELAY_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withVolley(combat.volley().withSplitDelayTicks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> railgun() {
    return ConfigOptionNodes.group(ConfigSettings.COMBAT_RAILGUN)
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_RAILGUN_SPEED_MULTIPLIER,
                RailgunArrowConfig.SPEED_MULTIPLIER_MIN,
                RailgunArrowConfig.SPEED_MULTIPLIER_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withRailgun(combat.railgun().withSpeedMultiplier(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.COMBAT_RAILGUN_GRAVITY_FACTOR,
                RailgunArrowConfig.GRAVITY_FACTOR_MIN,
                RailgunArrowConfig.GRAVITY_FACTOR_MAX,
                (current, value) ->
                    change(
                        current,
                        combat -> combat.withRailgun(combat.railgun().withGravityFactor(value)))));
  }

  private static NotEnoughArrowsConfig change(
      final NotEnoughArrowsConfig current, final UnaryOperator<CombatArrowConfig> change) {
    return current.withCombat(change.apply(current.combat()));
  }
}
