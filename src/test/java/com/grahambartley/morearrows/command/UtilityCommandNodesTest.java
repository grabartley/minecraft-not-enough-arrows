package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UtilityCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "morearrows config utility glowdurationticks 0,               true",
    "morearrows config utility glowdurationticks 6000,            true",
    "morearrows config utility glowdurationticks 6001,            false",
    "morearrows config utility redstonesignaldurationticks 1,     true",
    "morearrows config utility redstonesignaldurationticks 1200,  true",
    "morearrows config utility redstonesignaldurationticks 0,     true",
    "morearrows config utility redstonesignaldurationticks -1,    false",
    "morearrows config utility redstonesignaldurationticks 1201,  false",
    "morearrows config utility redstonesignalstrength 1,          true",
    "morearrows config utility redstonesignalstrength 15,         true",
    "morearrows config utility redstonesignalstrength 0,          false",
    "morearrows config utility redstonesignalstrength 16,         false",
    "morearrows config utility windburstradius 0.5,               true",
    "morearrows config utility windburstradius 16.0,              true",
    "morearrows config utility windburstradius 0.4,               false",
    "morearrows config utility windburstradius 16.1,              false",
    "morearrows config utility windpushstrength 0.0,              true",
    "morearrows config utility windpushstrength 8.0,              true",
    "morearrows config utility windpushstrength 8.1,              false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
