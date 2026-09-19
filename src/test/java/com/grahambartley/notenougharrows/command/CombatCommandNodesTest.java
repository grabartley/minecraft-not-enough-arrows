package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CombatCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config combat shock arcradius 0.0,              true",
    "notenougharrows config combat shock arcradius 32.0,             true",
    "notenougharrows config combat shock arcradius 32.1,             false",
    "notenougharrows config combat shock damage 0.0,                 true",
    "notenougharrows config combat shock damage 20.1,                false",
    "notenougharrows config combat lifesteal share 0.0,              true",
    "notenougharrows config combat lifesteal share 1.0,              true",
    "notenougharrows config combat lifesteal share 1.1,              false",
    "notenougharrows config combat lifesteal maxhealperhit 20.0,     true",
    "notenougharrows config combat lifesteal maxhealperhit 20.1,     false",
    "notenougharrows config combat status rustdurationticks 0,       true",
    "notenougharrows config combat status rustdurationticks 12000,   true",
    "notenougharrows config combat status rustdurationticks 12001,   false",
    "notenougharrows config combat status hastedurationticks 600,    true",
    "notenougharrows config combat status guarddurationticks -1,     false",
    "notenougharrows config combat homing turnrate 0.0,              true",
    "notenougharrows config combat homing turnrate 1.0,              true",
    "notenougharrows config combat homing turnrate 1.1,              false",
    "notenougharrows config combat homing searchradius 64.0,         true",
    "notenougharrows config combat homing searchconedegrees 180.0,   true",
    "notenougharrows config combat homing searchconedegrees 181.0,   false",
    "notenougharrows config combat volley fragmentcount 2,           true",
    "notenougharrows config combat volley fragmentcount 12,          true",
    "notenougharrows config combat volley fragmentcount 1,           false",
    "notenougharrows config combat volley fragmentcount 13,          false",
    "notenougharrows config combat volley damageshare 0.4,           true",
    "notenougharrows config combat volley spreaddegrees 45.0,        true",
    "notenougharrows config combat volley spreaddegrees 45.1,        false",
    "notenougharrows config combat volley splitdelayticks 1,         true",
    "notenougharrows config combat volley splitdelayticks 0,         false",
    "notenougharrows config combat railgun speedmultiplier 1.0,      true",
    "notenougharrows config combat railgun speedmultiplier 8.0,      true",
    "notenougharrows config combat railgun speedmultiplier 0.9,      false",
    "notenougharrows config combat railgun gravityfactor 0.05,       true",
    "notenougharrows config combat railgun gravityfactor 2.0,        true",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "\"{0}\"")
  @ValueSource(
      strings = {
        "notenougharrows config combat railgun gravityfactor 0.0",
        "notenougharrows config combat railgun gravityfactor 0.04",
        "notenougharrows config combat railgun gravityfactor -1.0",
      })
  void aGravityFactorOfNoneIsRefusedSoNoArrowCanFlyForever(final String command) {
    assertFalse(CommandParsing.accepts(dispatcher, operator, command));
  }

  @Test
  void aPlayerWhoIsNotAnOperatorCannotReachTheCombatSettings() {
    assertFalse(
        CommandParsing.accepts(
            dispatcher,
            CommandParsing.source(false),
            "notenougharrows config combat shock damage 1.0"));
  }
}
