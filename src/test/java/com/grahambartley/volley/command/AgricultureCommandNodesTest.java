package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AgricultureCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("agriculture", AgricultureCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "volley config agriculture blossom radius 0,       true",
    "volley config agriculture blossom radius 8,       true",
    "volley config agriculture blossom radius -1,      false",
    "volley config agriculture blossom radius 9,       false",
    "volley config agriculture till radius 0,          true",
    "volley config agriculture till radius 8,          true",
    "volley config agriculture till radius -1,         false",
    "volley config agriculture till radius 9,          false",
    "volley config agriculture harvest radius 0,       true",
    "volley config agriculture harvest radius 8,       true",
    "volley config agriculture harvest radius -1,      false",
    "volley config agriculture harvest radius 9,       false",
    "volley config agriculture bee count 1,            true",
    "volley config agriculture bee count 8,            true",
    "volley config agriculture bee count 0,            false",
    "volley config agriculture bee count 9,            false",
    "volley config agriculture bee lifetimeticks 20,   true",
    "volley config agriculture bee lifetimeticks 6000, true",
    "volley config agriculture bee lifetimeticks 19,   false",
    "volley config agriculture bee lifetimeticks 6001, false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "volley config agriculture blossom radius 0",
    "volley config agriculture blossom radius 8",
    "volley config agriculture till radius 0",
    "volley config agriculture till radius 8",
    "volley config agriculture harvest radius 0",
    "volley config agriculture harvest radius 8",
    "volley config agriculture bee count 1",
    "volley config agriculture bee count 8",
    "volley config agriculture bee lifetimeticks 20",
    "volley config agriculture bee lifetimeticks 6000",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
