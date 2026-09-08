package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.ExplosiveTierConfig;
import com.grahambartley.morearrows.config.IncendiaryArrowConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class ExplosiveCommandNodes {
  private ExplosiveCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.EXPLOSIVE)
        .then(
            tier(
                ConfigSettings.EXPLOSIVE_GUNPOWDER,
                ExplosiveArrowConfig::gunpowder,
                ExplosiveArrowConfig::withGunpowder))
        .then(
            tier(
                ConfigSettings.EXPLOSIVE_TNT,
                ExplosiveArrowConfig::tnt,
                ExplosiveArrowConfig::withTnt))
        .then(
            tier(
                ConfigSettings.EXPLOSIVE_FIRE_CHARGE,
                ExplosiveArrowConfig::fireCharge,
                ExplosiveArrowConfig::withFireCharge))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.EXPLOSIVE_DAMAGE_TERRAIN,
                (current, value) ->
                    change(current, explosive -> explosive.withDamageTerrain(value))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.EXPLOSIVE_DAMAGE_ENTITIES,
                (current, value) ->
                    change(current, explosive -> explosive.withDamageEntities(value))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.EXPLOSIVE_FIRE_PATCH_RADIUS,
                ExplosiveArrowConfig.FIRE_PATCH_RADIUS_MIN,
                ExplosiveArrowConfig.FIRE_PATCH_RADIUS_MAX,
                (current, value) ->
                    change(current, explosive -> explosive.withFirePatchRadius(value))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.EXPLOSIVE_FIRE_PATCH_DURATION_TICKS,
                ExplosiveArrowConfig.FIRE_PATCH_DURATION_TICKS_MIN,
                ExplosiveArrowConfig.FIRE_PATCH_DURATION_TICKS_MAX,
                (current, value) ->
                    change(current, explosive -> explosive.withFirePatchDurationTicks(value))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.EXPLOSIVE_BEEP_VOLUME,
                ExplosiveArrowConfig.BEEP_VOLUME_MIN,
                ExplosiveArrowConfig.BEEP_VOLUME_MAX,
                (current, value) -> change(current, explosive -> explosive.withBeepVolume(value))))
        .then(incendiary());
  }

  private static LiteralArgumentBuilder<ServerCommandSource> incendiary() {
    return ConfigOptionNodes.group(ConfigSettings.EXPLOSIVE_INCENDIARY)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.EXPLOSIVE_INCENDIARY_BURN_RADIUS,
                IncendiaryArrowConfig.BURN_RADIUS_MIN,
                IncendiaryArrowConfig.BURN_RADIUS_MAX,
                (current, value) ->
                    change(
                        current,
                        explosive -> explosive.withIncendiary(fire -> fire.withBurnRadius(value)))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.EXPLOSIVE_INCENDIARY_IGNITE_SECONDS,
                IncendiaryArrowConfig.IGNITE_SECONDS_MIN,
                IncendiaryArrowConfig.IGNITE_SECONDS_MAX,
                (current, value) ->
                    change(
                        current,
                        explosive ->
                            explosive.withIncendiary(fire -> fire.withIgniteSeconds(value)))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.EXPLOSIVE_INCENDIARY_IGNITES_BLOCKS,
                (current, value) ->
                    change(
                        current,
                        explosive ->
                            explosive.withIncendiary(fire -> fire.withIgnitesBlocks(value)))));
  }

  private static LiteralArgumentBuilder<ServerCommandSource> tier(
      final String setting,
      final Function<ExplosiveArrowConfig, ExplosiveTierConfig> reader,
      final TierWriter writer) {
    return ConfigOptionNodes.group(setting)
        .then(
            ConfigOptionNodes.intOption(
                setting + "." + ConfigSettings.DELAY_TICKS,
                ExplosiveTierConfig.DELAY_TICKS_MIN,
                ExplosiveTierConfig.DELAY_TICKS_MAX,
                (current, value) ->
                    changeTier(current, reader, writer, tier -> tier.withDelayTicks(value))))
        .then(
            ConfigOptionNodes.floatOption(
                setting + "." + ConfigSettings.POWER,
                ExplosiveTierConfig.POWER_MIN,
                ExplosiveTierConfig.POWER_MAX,
                (current, value) ->
                    changeTier(current, reader, writer, tier -> tier.withPower(value))));
  }

  @FunctionalInterface
  private interface TierWriter {
    ExplosiveArrowConfig apply(ExplosiveArrowConfig explosive, ExplosiveTierConfig tier);
  }

  private static MoreArrowsConfig changeTier(
      final MoreArrowsConfig current,
      final Function<ExplosiveArrowConfig, ExplosiveTierConfig> reader,
      final TierWriter writer,
      final UnaryOperator<ExplosiveTierConfig> tierChange) {
    return change(
        current, explosive -> writer.apply(explosive, tierChange.apply(reader.apply(explosive))));
  }

  private static MoreArrowsConfig change(
      final MoreArrowsConfig current, final UnaryOperator<ExplosiveArrowConfig> change) {
    return current.withExplosive(change.apply(current.explosive()));
  }
}
