package com.grahambartley.morearrows.command;

import com.grahambartley.morearrows.config.ConfigSettings;
import com.grahambartley.morearrows.config.ConfigValueFormat;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.PhysicsArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class PhysicsCommandNodes {
  public static final String BLOCK_ARGUMENT = "block";
  public static final String ADD = "add";
  public static final String REMOVE = "remove";
  public static final String CLEAR = "clear";

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
        .then(exclusions())
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

  private static LiteralArgumentBuilder<ServerCommandSource> exclusions() {
    return ConfigOptionNodes.group(ConfigSettings.PHYSICS_GRAVITY_BLOCK_EXCLUSIONS)
        .then(
            CommandManager.literal(ADD)
                .then(
                    CommandManager.argument(BLOCK_ARGUMENT, IdentifierArgumentType.identifier())
                        .executes(context -> edit(context, GravityExclusions::add))))
        .then(
            CommandManager.literal(REMOVE)
                .then(
                    CommandManager.argument(BLOCK_ARGUMENT, IdentifierArgumentType.identifier())
                        .executes(context -> edit(context, GravityExclusions::remove))))
        .then(
            CommandManager.literal(CLEAR)
                .executes(
                    context ->
                        applyResult(
                            context,
                            GravityExclusions.clear(
                                ServerConfigService.get().physics().gravityBlockExclusions()))));
  }

  @FunctionalInterface
  private interface ExclusionEdit {
    GravityExclusions.Result apply(List<String> current, String blockId);
  }

  private static int edit(
      final CommandContext<ServerCommandSource> context, final ExclusionEdit editor) {
    final String blockId = IdentifierArgumentType.getIdentifier(context, BLOCK_ARGUMENT).toString();
    final List<String> current = ServerConfigService.get().physics().gravityBlockExclusions();
    return applyResult(context, editor.apply(current, blockId));
  }

  private static int applyResult(
      final CommandContext<ServerCommandSource> context, final GravityExclusions.Result result) {
    if (!result.outcome().succeeded()) {
      context.getSource().sendError(rejection(result.outcome()));
      return 0;
    }
    return ConfigUpdates.apply(
        context,
        change(
            ServerConfigService.get(),
            physics -> physics.withGravityBlockExclusions(result.updated())),
        ConfigSettings.PHYSICS_GRAVITY_BLOCK_EXCLUSIONS,
        ConfigValueFormat.of(result.updated()));
  }

  public static Text rejection(final GravityExclusions.Outcome outcome) {
    return Text.translatable(rejectionKey(outcome));
  }

  public static String rejectionKey(final GravityExclusions.Outcome outcome) {
    return switch (outcome) {
      case ALREADY_PRESENT -> "command.more-arrows.exclusions.already_present";
      case NOT_PRESENT -> "command.more-arrows.exclusions.not_present";
      case LIST_FULL -> "command.more-arrows.exclusions.full";
      default -> "command.more-arrows.exclusions.invalid";
    };
  }

  private static MoreArrowsConfig change(
      final MoreArrowsConfig current, final UnaryOperator<PhysicsArrowConfig> change) {
    return current.withPhysics(change.apply(current.physics()));
  }
}
