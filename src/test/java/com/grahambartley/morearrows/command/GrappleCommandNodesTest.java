package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GrappleCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "morearrows config grapple maxrangeblocks 4,                  true",
    "morearrows config grapple maxrangeblocks 128,                true",
    "morearrows config grapple maxrangeblocks 3,                  false",
    "morearrows config grapple maxrangeblocks 129,                false",
    "morearrows config grapple pullspeed 0.1,                     true",
    "morearrows config grapple pullspeed 4.0,                     true",
    "morearrows config grapple pullspeed 0.05,                    false",
    "morearrows config grapple pullspeed 4.01,                    false",
    "morearrows config grapple cancelfalldamageonarrival true,    true",
    "morearrows config grapple returnarrowonarrival false,        true",
    "morearrows config grapple ropelengthblocks 1,                true",
    "morearrows config grapple ropelengthblocks 128,              true",
    "morearrows config grapple ropelengthblocks 0,                false",
    "morearrows config grapple ropelengthblocks 129,              false",
    "morearrows config grapple ropesdecay true,                   true",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
