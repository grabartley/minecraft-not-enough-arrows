package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.ConfigSettings;
import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.server.command.ServerCommandSource;

public final class GrappleCommandNodes {
  private GrappleCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return ConfigOptionNodes.group(ConfigSettings.GRAPPLE)
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.GRAPPLE_MAX_RANGE_BLOCKS,
                GrappleArrowConfig.MAX_RANGE_BLOCKS_MIN,
                GrappleArrowConfig.MAX_RANGE_BLOCKS_MAX,
                (current, value) -> change(current, grapple -> grapple.withMaxRangeBlocks(value))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.GRAPPLE_PULL_SPEED,
                GrappleArrowConfig.PULL_SPEED_MIN,
                GrappleArrowConfig.PULL_SPEED_MAX,
                (current, value) -> change(current, grapple -> grapple.withPullSpeed(value))))
        .then(
            ConfigOptionNodes.floatOption(
                ConfigSettings.GRAPPLE_PULL_ACCELERATION,
                GrappleArrowConfig.PULL_ACCELERATION_MIN,
                GrappleArrowConfig.PULL_ACCELERATION_MAX,
                (current, value) ->
                    change(current, grapple -> grapple.withPullAcceleration(value))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.GRAPPLE_CANCEL_FALL_DAMAGE_ON_ARRIVAL,
                (current, value) ->
                    change(current, grapple -> grapple.withCancelFallDamageOnArrival(value))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.GRAPPLE_RETURN_ARROW_ON_ARRIVAL,
                (current, value) ->
                    change(current, grapple -> grapple.withReturnArrowOnArrival(value))))
        .then(
            ConfigOptionNodes.intOption(
                ConfigSettings.GRAPPLE_ROPE_LENGTH_BLOCKS,
                GrappleArrowConfig.ROPE_LENGTH_BLOCKS_MIN,
                GrappleArrowConfig.ROPE_LENGTH_BLOCKS_MAX,
                (current, value) ->
                    change(current, grapple -> grapple.withRopeLengthBlocks(value))))
        .then(
            ConfigOptionNodes.booleanOption(
                ConfigSettings.GRAPPLE_ROPES_DECAY,
                (current, value) -> change(current, grapple -> grapple.withRopesDecay(value))));
  }

  private static NotEnoughArrowsConfig change(
      final NotEnoughArrowsConfig current, final UnaryOperator<GrappleArrowConfig> change) {
    return current.withGrapple(change.apply(current.grapple()));
  }
}
