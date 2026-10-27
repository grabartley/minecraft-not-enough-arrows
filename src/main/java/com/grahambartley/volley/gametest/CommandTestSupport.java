package com.grahambartley.volley.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.TestContext;

final class CommandTestSupport {
  private CommandTestSupport() {}

  static boolean run(final TestContext context, final String command) {
    return run(context, context.getWorld().getServer().getCommandSource(), command);
  }

  static boolean run(
      final TestContext context, final ServerCommandSource source, final String command) {
    try {
      return context
              .getWorld()
              .getServer()
              .getCommandManager()
              .getDispatcher()
              .execute(command, source)
          > 0;
    } catch (final CommandSyntaxException ex) {
      return false;
    }
  }
}
