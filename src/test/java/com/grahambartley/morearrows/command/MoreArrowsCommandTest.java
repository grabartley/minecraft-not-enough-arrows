package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoreArrowsCommandTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;
  private ServerCommandSource everyone;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
    everyone = CommandParsing.source(false);
  }

  @Test
  void theRootCommandRunsOnItsOwn() {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, "morearrows"));
  }

  @Test
  void statusIsReadableWithoutOperatorPermission() {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, "morearrows status"));
  }

  @ParameterizedTest(name = "an operator may run \"{0}\"")
  @ValueSource(
      strings = {
        "morearrows config reset",
        "morearrows config explosive beepvolume 1.5",
        "morearrows config grapple maxrangeblocks 64",
        "morearrows config utility redstonesignalstrength 7",
        "morearrows config physics ricochetbouncecount 2",
        "morearrows config ender pearlmaxrangeblocks 64",
      })
  void everyFamilyIsReachableByAnOperator(String command) {
    assertTrue(CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @ValueSource(
      strings = {
        "morearrows config reset",
        "morearrows config explosive beepvolume 1.5",
        "morearrows config grapple maxrangeblocks 64",
        "morearrows config utility redstonesignalstrength 7",
        "morearrows config physics ricochetbouncecount 2",
        "morearrows config ender pearlmaxrangeblocks 64",
      })
  void mutatingCommandsAreGatedBehindOperatorPermission(String command) {
    assertFalse(CommandParsing.accepts(dispatcher, everyone, command));
  }

  @Test
  void anUnknownSubcommandIsNotAccepted() {
    assertFalse(CommandParsing.accepts(dispatcher, operator, "morearrows config fletching 1"));
  }
}
