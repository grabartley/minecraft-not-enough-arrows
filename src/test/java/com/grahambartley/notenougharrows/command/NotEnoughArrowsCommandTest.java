package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NotEnoughArrowsCommandTest {

  private static final List<String> ROOTS =
      List.of(NotEnoughArrowsCommand.ROOT, NotEnoughArrowsCommand.ALIAS);

  private static final List<String> OPERATOR_COMMANDS =
      List.of(
          "config reset",
          "config explosive beepvolume 1.5",
          "config grapple maxrangeblocks 64",
          "config utility redstonesignalstrength 7",
          "config physics ricochetbouncecount 2",
          "config ender pearlmaxrangeblocks 64");

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;
  private ServerCommandSource everyone;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
    everyone = CommandParsing.source(false);
  }

  static Stream<String> roots() {
    return ROOTS.stream();
  }

  static Stream<Arguments> rootsAndOperatorCommands() {
    return ROOTS.stream()
        .flatMap(root -> OPERATOR_COMMANDS.stream().map(command -> Arguments.of(root, command)));
  }

  @ParameterizedTest(name = "\"{0}\" runs on its own")
  @MethodSource("roots")
  void theRootCommandRunsOnItsOwn(String root) {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, root));
  }

  @ParameterizedTest(name = "\"{0} status\" is readable without operator permission")
  @MethodSource("roots")
  void statusIsReadableWithoutOperatorPermission(String root) {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, root + " status"));
  }

  @ParameterizedTest(name = "an operator may run \"{0} {1}\"")
  @MethodSource("rootsAndOperatorCommands")
  void everyFamilyIsReachableByAnOperator(String root, String command) {
    assertTrue(CommandParsing.accepts(dispatcher, operator, root + " " + command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0} {1}\"")
  @MethodSource("rootsAndOperatorCommands")
  void mutatingCommandsAreGatedBehindOperatorPermission(String root, String command) {
    assertFalse(CommandParsing.accepts(dispatcher, everyone, root + " " + command));
  }

  @ParameterizedTest(name = "\"{0} config fletching 1\" is not accepted")
  @MethodSource("roots")
  void anUnknownSubcommandIsNotAccepted(String root) {
    assertFalse(CommandParsing.accepts(dispatcher, operator, root + " config fletching 1"));
  }
}
