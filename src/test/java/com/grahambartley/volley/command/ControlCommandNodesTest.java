package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ControlCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "volley config control frost durationticks 0,              true",
    "volley config control frost durationticks 1200,           true",
    "volley config control frost durationticks -1,             false",
    "volley config control frost durationticks 1201,           false",
    "volley config control levitation durationticks 0,         true",
    "volley config control levitation durationticks 1200,      true",
    "volley config control levitation durationticks 1201,      false",
    "volley config control targeting tauntradius 0.0,          true",
    "volley config control targeting tauntradius 32.0,         true",
    "volley config control targeting tauntradius 32.1,         false",
    "volley config control targeting tauntdurationticks 0,     true",
    "volley config control targeting tauntdurationticks 6000,  true",
    "volley config control targeting tauntdurationticks 6001,  false",
    "volley config control targeting repelradius 8.0,          true",
    "volley config control targeting repelradius -0.1,         false",
    "volley config control targeting repeldurationticks 200,   true",
    "volley config control targeting repeldurationticks -1,    false",
    "volley config control allegiance durationticks 6000,      true",
    "volley config control allegiance durationticks 6001,      false",
    "volley config control allegiance defendradius 32.0,       true",
    "volley config control allegiance defendradius 32.1,       false",
    "volley config control disarm throwdistance 5.0,           true",
    "volley config control disarm throwdistance 16.0,          true",
    "volley config control disarm throwdistance 16.1,          false",
    "volley config control smoke radius 0.0,                   true",
    "volley config control smoke radius 16.0,                  true",
    "volley config control smoke radius 16.1,                  false",
    "volley config control smoke durationticks 6000,           true",
    "volley config control smoke durationticks 6001,           false",
    "volley config control disarm affectsplayers true,         true",
    "volley config control disarm affectsplayers false,        true",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "\"{0}\"")
  @ValueSource(
      strings = {
        "volley config control disarm affectsplayers maybe",
        "volley config control disarm affectsplayers 1",
      })
  void theDisarmSwitchTakesOnlyABoolean(final String command) {
    assertFalse(CommandParsing.accepts(dispatcher, operator, command));
  }

  @Test
  void aPlayerWhoIsNotAnOperatorCannotReachTheControlSettings() {
    assertFalse(
        CommandParsing.accepts(
            dispatcher, CommandParsing.source(false), "volley config control smoke radius 1.0"));
  }
}
