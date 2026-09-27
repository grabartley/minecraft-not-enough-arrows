package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ChaosCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("chaos", ChaosCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config chaos partyenabled true,            true",
    "notenougharrows config chaos partyenabled false,           true",
    "notenougharrows config chaos partyenabled yes,             false",
    "notenougharrows config chaos chickenenabled true,          true",
    "notenougharrows config chaos chickenenabled false,         true",
    "notenougharrows config chaos chickenenabled yes,           false",
    "notenougharrows config chaos pufferenabled true,           true",
    "notenougharrows config chaos pufferenabled false,          true",
    "notenougharrows config chaos pufferenabled yes,            false",
    "notenougharrows config chaos pufferdurationticks 0,        true",
    "notenougharrows config chaos pufferdurationticks 1200,     true",
    "notenougharrows config chaos pufferdurationticks -1,       false",
    "notenougharrows config chaos pufferdurationticks 1201,     false",
    "notenougharrows config chaos stinkenabled true,            true",
    "notenougharrows config chaos stinkenabled false,           true",
    "notenougharrows config chaos stinkenabled yes,             false",
    "notenougharrows config chaos stinkcloudlifetimeticks 0,    true",
    "notenougharrows config chaos stinkcloudlifetimeticks 1200, true",
    "notenougharrows config chaos stinkcloudlifetimeticks -1,   false",
    "notenougharrows config chaos stinkcloudlifetimeticks 1201, false",
    "notenougharrows config chaos boomerangenabled true,        true",
    "notenougharrows config chaos boomerangenabled false,       true",
    "notenougharrows config chaos boomerangenabled yes,         false",
    "notenougharrows config chaos polymorphenabled true,        true",
    "notenougharrows config chaos polymorphenabled false,       true",
    "notenougharrows config chaos polymorphenabled yes,         false",
    "notenougharrows config chaos polymorphdurationticks 0,     true",
    "notenougharrows config chaos polymorphdurationticks 2400,  true",
    "notenougharrows config chaos polymorphdurationticks -1,    false",
    "notenougharrows config chaos polymorphdurationticks 2401,  false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config chaos partyenabled true",
    "notenougharrows config chaos partyenabled false",
    "notenougharrows config chaos chickenenabled true",
    "notenougharrows config chaos chickenenabled false",
    "notenougharrows config chaos pufferenabled true",
    "notenougharrows config chaos pufferenabled false",
    "notenougharrows config chaos pufferdurationticks 0",
    "notenougharrows config chaos pufferdurationticks 1200",
    "notenougharrows config chaos stinkenabled true",
    "notenougharrows config chaos stinkenabled false",
    "notenougharrows config chaos stinkcloudlifetimeticks 0",
    "notenougharrows config chaos stinkcloudlifetimeticks 1200",
    "notenougharrows config chaos boomerangenabled true",
    "notenougharrows config chaos boomerangenabled false",
    "notenougharrows config chaos polymorphenabled true",
    "notenougharrows config chaos polymorphenabled false",
    "notenougharrows config chaos polymorphdurationticks 0",
    "notenougharrows config chaos polymorphdurationticks 2400",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
