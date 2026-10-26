package com.grahambartley.volley.command;

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
    "volley config chaos party enabled true,            true",
    "volley config chaos party enabled false,           true",
    "volley config chaos party enabled yes,             false",
    "volley config chaos chicken enabled true,          true",
    "volley config chaos chicken enabled false,         true",
    "volley config chaos chicken enabled yes,           false",
    "volley config chaos puffer enabled true,           true",
    "volley config chaos puffer enabled false,          true",
    "volley config chaos puffer enabled yes,            false",
    "volley config chaos puffer durationticks 0,        true",
    "volley config chaos puffer durationticks 1200,     true",
    "volley config chaos puffer durationticks -1,       false",
    "volley config chaos puffer durationticks 1201,     false",
    "volley config chaos stink enabled true,            true",
    "volley config chaos stink enabled false,           true",
    "volley config chaos stink enabled yes,             false",
    "volley config chaos stink cloudlifetimeticks 0,    true",
    "volley config chaos stink cloudlifetimeticks 1200, true",
    "volley config chaos stink cloudlifetimeticks -1,   false",
    "volley config chaos stink cloudlifetimeticks 1201, false",
    "volley config chaos boomerang enabled true,        true",
    "volley config chaos boomerang enabled false,       true",
    "volley config chaos boomerang enabled yes,         false",
    "volley config chaos polymorph enabled true,        true",
    "volley config chaos polymorph enabled false,       true",
    "volley config chaos polymorph enabled yes,         false",
    "volley config chaos polymorph durationticks 0,     true",
    "volley config chaos polymorph durationticks 2400,  true",
    "volley config chaos polymorph durationticks -1,    false",
    "volley config chaos polymorph durationticks 2401,  false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "volley config chaos party enabled true",
    "volley config chaos party enabled false",
    "volley config chaos chicken enabled true",
    "volley config chaos chicken enabled false",
    "volley config chaos puffer enabled true",
    "volley config chaos puffer enabled false",
    "volley config chaos puffer durationticks 0",
    "volley config chaos puffer durationticks 1200",
    "volley config chaos stink enabled true",
    "volley config chaos stink enabled false",
    "volley config chaos stink cloudlifetimeticks 0",
    "volley config chaos stink cloudlifetimeticks 1200",
    "volley config chaos boomerang enabled true",
    "volley config chaos boomerang enabled false",
    "volley config chaos polymorph enabled true",
    "volley config chaos polymorph enabled false",
    "volley config chaos polymorph durationticks 0",
    "volley config chaos polymorph durationticks 2400",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
