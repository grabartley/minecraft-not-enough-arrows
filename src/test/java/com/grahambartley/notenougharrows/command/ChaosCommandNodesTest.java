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
    "notenougharrows config chaos party enabled true,            true",
    "notenougharrows config chaos party enabled false,           true",
    "notenougharrows config chaos party enabled yes,             false",
    "notenougharrows config chaos chicken enabled true,          true",
    "notenougharrows config chaos chicken enabled false,         true",
    "notenougharrows config chaos chicken enabled yes,           false",
    "notenougharrows config chaos puffer enabled true,           true",
    "notenougharrows config chaos puffer enabled false,          true",
    "notenougharrows config chaos puffer enabled yes,            false",
    "notenougharrows config chaos puffer durationticks 0,        true",
    "notenougharrows config chaos puffer durationticks 1200,     true",
    "notenougharrows config chaos puffer durationticks -1,       false",
    "notenougharrows config chaos puffer durationticks 1201,     false",
    "notenougharrows config chaos stink enabled true,            true",
    "notenougharrows config chaos stink enabled false,           true",
    "notenougharrows config chaos stink enabled yes,             false",
    "notenougharrows config chaos stink cloudlifetimeticks 0,    true",
    "notenougharrows config chaos stink cloudlifetimeticks 1200, true",
    "notenougharrows config chaos stink cloudlifetimeticks -1,   false",
    "notenougharrows config chaos stink cloudlifetimeticks 1201, false",
    "notenougharrows config chaos boomerang enabled true,        true",
    "notenougharrows config chaos boomerang enabled false,       true",
    "notenougharrows config chaos boomerang enabled yes,         false",
    "notenougharrows config chaos polymorph enabled true,        true",
    "notenougharrows config chaos polymorph enabled false,       true",
    "notenougharrows config chaos polymorph enabled yes,         false",
    "notenougharrows config chaos polymorph durationticks 0,     true",
    "notenougharrows config chaos polymorph durationticks 2400,  true",
    "notenougharrows config chaos polymorph durationticks -1,    false",
    "notenougharrows config chaos polymorph durationticks 2401,  false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config chaos party enabled true",
    "notenougharrows config chaos party enabled false",
    "notenougharrows config chaos chicken enabled true",
    "notenougharrows config chaos chicken enabled false",
    "notenougharrows config chaos puffer enabled true",
    "notenougharrows config chaos puffer enabled false",
    "notenougharrows config chaos puffer durationticks 0",
    "notenougharrows config chaos puffer durationticks 1200",
    "notenougharrows config chaos stink enabled true",
    "notenougharrows config chaos stink enabled false",
    "notenougharrows config chaos stink cloudlifetimeticks 0",
    "notenougharrows config chaos stink cloudlifetimeticks 1200",
    "notenougharrows config chaos boomerang enabled true",
    "notenougharrows config chaos boomerang enabled false",
    "notenougharrows config chaos polymorph enabled true",
    "notenougharrows config chaos polymorph enabled false",
    "notenougharrows config chaos polymorph durationticks 0",
    "notenougharrows config chaos polymorph durationticks 2400",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
