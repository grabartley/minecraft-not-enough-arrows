package com.grahambartley.morearrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExplosiveCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "morearrows config explosive gunpowder delayticks 0,          true",
    "morearrows config explosive gunpowder delayticks 200,        true",
    "morearrows config explosive gunpowder delayticks 201,        false",
    "morearrows config explosive gunpowder delayticks -1,         false",
    "morearrows config explosive gunpowder power 0.0,             true",
    "morearrows config explosive gunpowder power 20.0,            true",
    "morearrows config explosive gunpowder power 20.1,            false",
    "morearrows config explosive tnt delayticks 200,              true",
    "morearrows config explosive tnt delayticks 201,              false",
    "morearrows config explosive tnt power 20.0,                  true",
    "morearrows config explosive tnt power 20.1,                  false",
    "morearrows config explosive firecharge delayticks 200,       true",
    "morearrows config explosive firecharge delayticks 201,       false",
    "morearrows config explosive firecharge power 20.0,           true",
    "morearrows config explosive firecharge power 20.1,           false",
    "morearrows config explosive damageterrain true,              true",
    "morearrows config explosive damageentities false,            true",
    "morearrows config explosive firepatchradius 8,               true",
    "morearrows config explosive firepatchradius 9,               false",
    "morearrows config explosive firepatchdurationticks 6000,     true",
    "morearrows config explosive firepatchdurationticks 6001,     false",
    "morearrows config explosive beepvolume 2.0,                  true",
    "morearrows config explosive beepvolume 2.1,                  false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
