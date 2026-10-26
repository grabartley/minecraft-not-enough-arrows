package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VolleyCommandTest {

  private static final String OLD_ALIAS = "nea";

  private static final List<String> OPERATOR_COMMANDS =
      List.of(
          "config reset",
          "config explosive beepvolume 0.5",
          "config grapple maxrangeblocks 64",
          "config utility redstonesignalstrength 7",
          "config physics ricochetbouncecount 2",
          "config ender pearlmaxrangeblocks 64",
          "config traversal zipline maxspanblocks 64",
          "config terrain drill tooltier 1",
          "config agriculture bee count 5",
          "config discovery prospector blocks add minecraft:stone",
          "config chaos party enabled false",
          "config social magnet radius 4");

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;
  private ServerCommandSource everyone;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
    everyone = CommandParsing.source(false);
  }

  static Stream<Arguments> operatorCommands() {
    return OPERATOR_COMMANDS.stream().map(Arguments::of);
  }

  @Test
  void theRootCommandRunsOnItsOwn() {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, VolleyCommand.ROOT));
  }

  @Test
  void statusIsReadableWithoutOperatorPermission() {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, VolleyCommand.ROOT + " status"));
  }

  @ParameterizedTest(name = "an operator may run \"volley {0}\"")
  @MethodSource("operatorCommands")
  void everyFamilyIsReachableByAnOperator(String command) {
    assertTrue(CommandParsing.accepts(dispatcher, operator, VolleyCommand.ROOT + " " + command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"volley {0}\"")
  @MethodSource("operatorCommands")
  void mutatingCommandsAreGatedBehindOperatorPermission(String command) {
    assertFalse(CommandParsing.accepts(dispatcher, everyone, VolleyCommand.ROOT + " " + command));
  }

  @Test
  void anUnknownSubcommandIsNotAccepted() {
    assertFalse(
        CommandParsing.accepts(dispatcher, operator, VolleyCommand.ROOT + " config fletching 1"));
  }

  @Test
  void theOldAliasIsGone() {
    assertFalse(CommandParsing.accepts(dispatcher, everyone, OLD_ALIAS));
    assertFalse(CommandParsing.accepts(dispatcher, everyone, OLD_ALIAS + " status"));
  }
}
