package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EnderCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "morearrows config ender pearlmaxrangeblocks 4,        true",
    "morearrows config ender pearlmaxrangeblocks 128,      true",
    "morearrows config ender pearlmaxrangeblocks 3,        false",
    "morearrows config ender pearlmaxrangeblocks 129,      false",
    "morearrows config ender recallmaxrangeblocks 4,       true",
    "morearrows config ender recallmaxrangeblocks 128,     true",
    "morearrows config ender recallmaxrangeblocks 3,       false",
    "morearrows config ender recallmaxrangeblocks 129,     false",
    "morearrows config ender recallaffectsplayers true,    true",
    "morearrows config ender recallaffectsplayers false,   true",
    "morearrows config ender recallaffectsplayers maybe,   false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
