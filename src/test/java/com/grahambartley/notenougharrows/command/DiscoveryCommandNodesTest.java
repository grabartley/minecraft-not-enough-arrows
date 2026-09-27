package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DiscoveryCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("discovery", DiscoveryCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config discovery torch enabled true,                       true",
    "notenougharrows config discovery torch enabled false,                      true",
    "notenougharrows config discovery torch enabled yes,                        false",
    "notenougharrows config discovery beacon lifetimeticks 0,                   true",
    "notenougharrows config discovery beacon lifetimeticks 12000,               true",
    "notenougharrows config discovery beacon lifetimeticks -1,                  false",
    "notenougharrows config discovery beacon lifetimeticks 12001,               false",
    "notenougharrows config discovery prospector radius 0,                      true",
    "notenougharrows config discovery prospector radius 16,                     true",
    "notenougharrows config discovery prospector radius -1,                     false",
    "notenougharrows config discovery prospector radius 17,                     false",
    "notenougharrows config discovery prospector durationticks 0,               true",
    "notenougharrows config discovery prospector durationticks 1200,            true",
    "notenougharrows config discovery prospector durationticks -1,              false",
    "notenougharrows config discovery prospector durationticks 1201,            false",
    "notenougharrows config discovery prospector blocks add minecraft:stone,    true",
    "notenougharrows config discovery prospector blocks remove minecraft:stone, true",
    "notenougharrows config discovery prospector blocks clear,                  true",
    "notenougharrows config discovery prospector blocks add,                    false",
    "notenougharrows config discovery prospector blocks minecraft:stone,        false",
    "notenougharrows config discovery sonar radius 0,                           true",
    "notenougharrows config discovery sonar radius 32,                          true",
    "notenougharrows config discovery sonar radius -1,                          false",
    "notenougharrows config discovery sonar radius 33,                          false",
    "notenougharrows config discovery sonar durationticks 0,                    true",
    "notenougharrows config discovery sonar durationticks 1200,                 true",
    "notenougharrows config discovery sonar durationticks -1,                   false",
    "notenougharrows config discovery sonar durationticks 1201,                 false",
    "notenougharrows config discovery tripwire lifetimeticks 20,                true",
    "notenougharrows config discovery tripwire lifetimeticks 24000,             true",
    "notenougharrows config discovery tripwire lifetimeticks 19,                false",
    "notenougharrows config discovery tripwire lifetimeticks 24001,             false",
    "notenougharrows config discovery tripwire reportintervalticks 20,          true",
    "notenougharrows config discovery tripwire reportintervalticks 1200,        true",
    "notenougharrows config discovery tripwire reportintervalticks 19,          false",
    "notenougharrows config discovery tripwire reportintervalticks 1201,        false",
    "notenougharrows config discovery tracer pathlifetimeticks 0,               true",
    "notenougharrows config discovery tracer pathlifetimeticks 1200,            true",
    "notenougharrows config discovery tracer pathlifetimeticks -1,              false",
    "notenougharrows config discovery tracer pathlifetimeticks 1201,            false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config discovery torch enabled true",
    "notenougharrows config discovery torch enabled false",
    "notenougharrows config discovery beacon lifetimeticks 0",
    "notenougharrows config discovery beacon lifetimeticks 12000",
    "notenougharrows config discovery prospector radius 0",
    "notenougharrows config discovery prospector radius 16",
    "notenougharrows config discovery prospector durationticks 0",
    "notenougharrows config discovery prospector durationticks 1200",
    "notenougharrows config discovery prospector blocks add minecraft:stone",
    "notenougharrows config discovery prospector blocks remove minecraft:stone",
    "notenougharrows config discovery prospector blocks clear",
    "notenougharrows config discovery sonar radius 0",
    "notenougharrows config discovery sonar radius 32",
    "notenougharrows config discovery sonar durationticks 0",
    "notenougharrows config discovery sonar durationticks 1200",
    "notenougharrows config discovery tripwire lifetimeticks 20",
    "notenougharrows config discovery tripwire lifetimeticks 24000",
    "notenougharrows config discovery tripwire reportintervalticks 20",
    "notenougharrows config discovery tripwire reportintervalticks 1200",
    "notenougharrows config discovery tracer pathlifetimeticks 0",
    "notenougharrows config discovery tracer pathlifetimeticks 1200",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
