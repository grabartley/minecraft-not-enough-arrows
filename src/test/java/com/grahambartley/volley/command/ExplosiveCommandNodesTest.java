package com.grahambartley.volley.command;

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
    "volley config explosive gunpowder delayticks 0,          true",
    "volley config explosive gunpowder delayticks 200,        true",
    "volley config explosive gunpowder delayticks 201,        false",
    "volley config explosive gunpowder delayticks -1,         false",
    "volley config explosive gunpowder power 0.0,             true",
    "volley config explosive gunpowder power 20.0,            true",
    "volley config explosive gunpowder power 20.1,            false",
    "volley config explosive tnt delayticks 200,              true",
    "volley config explosive tnt delayticks 201,              false",
    "volley config explosive tnt power 20.0,                  true",
    "volley config explosive tnt power 20.1,                  false",
    "volley config explosive firecharge delayticks 200,       true",
    "volley config explosive firecharge delayticks 201,       false",
    "volley config explosive firecharge power 20.0,           true",
    "volley config explosive firecharge power 20.1,           false",
    "volley config explosive damageterrain true,              true",
    "volley config explosive damageentities false,            true",
    "volley config explosive firepatchradius 8,               true",
    "volley config explosive firepatchradius 9,               false",
    "volley config explosive firepatchdurationticks 6000,     true",
    "volley config explosive firepatchdurationticks 6001,     false",
    "volley config explosive beepvolume 1.0,                  true",
    "volley config explosive incendiary burnradius 0,        true",
    "volley config explosive incendiary burnradius 8,        true",
    "volley config explosive incendiary burnradius 9,        false",
    "volley config explosive incendiary igniteseconds 0,     true",
    "volley config explosive incendiary igniteseconds 60,    true",
    "volley config explosive incendiary igniteseconds 61,    false",
    "volley config explosive incendiary ignitesblocks true,  true",
    "volley config explosive incendiary ignitesblocks maybe, false",
    "volley config explosive beepvolume 1.1,                  false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
