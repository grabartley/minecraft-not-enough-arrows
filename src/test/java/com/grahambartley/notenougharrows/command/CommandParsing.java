package com.grahambartley.notenougharrows.command;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.server.command.ServerCommandSource;

final class CommandParsing {
  private CommandParsing() {}

  static CommandDispatcher<ServerCommandSource> dispatcher() {
    final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();
    NotEnoughArrowsCommand.register(dispatcher);
    return dispatcher;
  }

  static ServerCommandSource source(final boolean operator) {
    final ServerCommandSource source = mock(ServerCommandSource.class);
    when(source.hasPermissionLevel(anyInt())).thenReturn(operator);
    return source;
  }

  static boolean accepts(
      final CommandDispatcher<ServerCommandSource> dispatcher,
      final ServerCommandSource source,
      final String command) {
    final ParseResults<ServerCommandSource> results = dispatcher.parse(command, source);
    return results.getExceptions().isEmpty()
        && !results.getReader().canRead()
        && results.getContext().getLastChild().getCommand() != null;
  }
}
