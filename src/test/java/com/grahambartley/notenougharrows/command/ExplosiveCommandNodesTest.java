package com.grahambartley.notenougharrows.command;

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
    "notenougharrows config explosive gunpowder delayticks 0,          true",
    "notenougharrows config explosive gunpowder delayticks 200,        true",
    "notenougharrows config explosive gunpowder delayticks 201,        false",
    "notenougharrows config explosive gunpowder delayticks -1,         false",
    "notenougharrows config explosive gunpowder power 0.0,             true",
    "notenougharrows config explosive gunpowder power 20.0,            true",
    "notenougharrows config explosive gunpowder power 20.1,            false",
    "notenougharrows config explosive tnt delayticks 200,              true",
    "notenougharrows config explosive tnt delayticks 201,              false",
    "notenougharrows config explosive tnt power 20.0,                  true",
    "notenougharrows config explosive tnt power 20.1,                  false",
    "notenougharrows config explosive firecharge delayticks 200,       true",
    "notenougharrows config explosive firecharge delayticks 201,       false",
    "notenougharrows config explosive firecharge power 20.0,           true",
    "notenougharrows config explosive firecharge power 20.1,           false",
    "notenougharrows config explosive damageterrain true,              true",
    "notenougharrows config explosive damageentities false,            true",
    "notenougharrows config explosive firepatchradius 8,               true",
    "notenougharrows config explosive firepatchradius 9,               false",
    "notenougharrows config explosive firepatchdurationticks 6000,     true",
    "notenougharrows config explosive firepatchdurationticks 6001,     false",
    "notenougharrows config explosive beepvolume 2.0,                  true",
    "notenougharrows config explosive incendiary burnradius 0,        true",
    "notenougharrows config explosive incendiary burnradius 8,        true",
    "notenougharrows config explosive incendiary burnradius 9,        false",
    "notenougharrows config explosive incendiary igniteseconds 0,     true",
    "notenougharrows config explosive incendiary igniteseconds 60,    true",
    "notenougharrows config explosive incendiary igniteseconds 61,    false",
    "notenougharrows config explosive incendiary ignitesblocks true,  true",
    "notenougharrows config explosive incendiary ignitesblocks maybe, false",
    "notenougharrows config explosive beepvolume 2.1,                  false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
