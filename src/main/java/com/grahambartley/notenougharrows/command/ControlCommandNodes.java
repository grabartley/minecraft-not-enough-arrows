package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.ControlArrowConfig;
import com.grahambartley.notenougharrows.config.DisarmArrowConfig;
import com.grahambartley.notenougharrows.config.FrostArrowConfig;
import com.grahambartley.notenougharrows.config.LevitationArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.SmokeArrowConfig;
import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class ControlCommandNodes {
  private ControlCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL)
        .then(frost())
        .then(levitation())
        .then(targeting())
        .then(allegiance())
        .then(smoke())
        .then(disarm());
  }

  private static LiteralArgumentBuilder<ServerCommandSource> frost() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL_FROST)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.CONTROL_FROST_DURATION_TICKS,
                FrostArrowConfig.DURATION_TICKS_MIN,
                FrostArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        control -> control.withFrost(control.frost().withDurationTicks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> levitation() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL_LEVITATION)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.CONTROL_LEVITATION_DURATION_TICKS,
                LevitationArrowConfig.DURATION_TICKS_MIN,
                LevitationArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withLevitation(
                                control.levitation().withDurationTicks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> targeting() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL_TARGETING)
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.CONTROL_TARGETING_TAUNT_RADIUS,
                TargetingArrowConfig.RADIUS_MIN,
                TargetingArrowConfig.RADIUS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withTargeting(control.targeting().withTauntRadius(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.CONTROL_TARGETING_TAUNT_DURATION_TICKS,
                TargetingArrowConfig.DURATION_TICKS_MIN,
                TargetingArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withTargeting(
                                control.targeting().withTauntDurationTicks(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.CONTROL_TARGETING_REPEL_RADIUS,
                TargetingArrowConfig.RADIUS_MIN,
                TargetingArrowConfig.RADIUS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withTargeting(control.targeting().withRepelRadius(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.CONTROL_TARGETING_REPEL_DURATION_TICKS,
                TargetingArrowConfig.DURATION_TICKS_MIN,
                TargetingArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withTargeting(
                                control.targeting().withRepelDurationTicks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> allegiance() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL_ALLEGIANCE)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.CONTROL_ALLEGIANCE_DURATION_TICKS,
                AllegianceArrowConfig.DURATION_TICKS_MIN,
                AllegianceArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withAllegiance(control.allegiance().withDurationTicks(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.CONTROL_ALLEGIANCE_DEFEND_RADIUS,
                AllegianceArrowConfig.DEFEND_RADIUS_MIN,
                AllegianceArrowConfig.DEFEND_RADIUS_MAX,
                (current, value) ->
                    change(
                        current,
                        control ->
                            control.withAllegiance(control.allegiance().withDefendRadius(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> smoke() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL_SMOKE)
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.CONTROL_SMOKE_RADIUS,
                SmokeArrowConfig.RADIUS_MIN,
                SmokeArrowConfig.RADIUS_MAX,
                (current, value) ->
                    change(
                        current, control -> control.withSmoke(control.smoke().withRadius(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.CONTROL_SMOKE_DURATION_TICKS,
                SmokeArrowConfig.DURATION_TICKS_MIN,
                SmokeArrowConfig.DURATION_TICKS_MAX,
                (current, value) ->
                    change(
                        current,
                        control -> control.withSmoke(control.smoke().withDurationTicks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> disarm() {
    return ConfigOptionNodes.group(ConfigSettings.CONTROL_DISARM)
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.CONTROL_DISARM_AFFECTS_PLAYERS,
                (current, value) ->
                    change(
                        current,
                        control -> control.withDisarm(control.disarm().withAffectsPlayers(value)))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.CONTROL_DISARM_THROW_DISTANCE,
                DisarmArrowConfig.THROW_DISTANCE_MIN,
                DisarmArrowConfig.THROW_DISTANCE_MAX,
                (current, value) ->
                    change(
                        current,
                        control -> control.withDisarm(control.disarm().withThrowDistance(value)))));
  }

  private static NotEnoughArrowsConfig change(
      final NotEnoughArrowsConfig current, final UnaryOperator<ControlArrowConfig> change) {
    return current.withControl(change.apply(current.control()));
  }
}
