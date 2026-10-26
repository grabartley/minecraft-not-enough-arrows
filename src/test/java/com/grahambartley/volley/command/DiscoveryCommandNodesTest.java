package com.grahambartley.volley.command;

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
    "volley config discovery torch enabled true,                       true",
    "volley config discovery torch enabled false,                      true",
    "volley config discovery torch enabled yes,                        false",
    "volley config discovery beacon lifetimeticks 0,                   true",
    "volley config discovery beacon lifetimeticks 12000,               true",
    "volley config discovery beacon lifetimeticks -1,                  false",
    "volley config discovery beacon lifetimeticks 12001,               false",
    "volley config discovery prospector radius 0,                      true",
    "volley config discovery prospector radius 16,                     true",
    "volley config discovery prospector radius -1,                     false",
    "volley config discovery prospector radius 17,                     false",
    "volley config discovery prospector durationticks 0,               true",
    "volley config discovery prospector durationticks 1200,            true",
    "volley config discovery prospector durationticks -1,              false",
    "volley config discovery prospector durationticks 1201,            false",
    "volley config discovery prospector blocks add minecraft:stone,    true",
    "volley config discovery prospector blocks remove minecraft:stone, true",
    "volley config discovery prospector blocks clear,                  true",
    "volley config discovery prospector blocks add,                    false",
    "volley config discovery prospector blocks minecraft:stone,        false",
    "volley config discovery sonar radius 0,                           true",
    "volley config discovery sonar radius 32,                          true",
    "volley config discovery sonar radius -1,                          false",
    "volley config discovery sonar radius 33,                          false",
    "volley config discovery sonar durationticks 0,                    true",
    "volley config discovery sonar durationticks 1200,                 true",
    "volley config discovery sonar durationticks -1,                   false",
    "volley config discovery sonar durationticks 1201,                 false",
    "volley config discovery tripwire lifetimeticks 20,                true",
    "volley config discovery tripwire lifetimeticks 24000,             true",
    "volley config discovery tripwire lifetimeticks 19,                false",
    "volley config discovery tripwire lifetimeticks 24001,             false",
    "volley config discovery tripwire reportintervalticks 20,          true",
    "volley config discovery tripwire reportintervalticks 1200,        true",
    "volley config discovery tripwire reportintervalticks 19,          false",
    "volley config discovery tripwire reportintervalticks 1201,        false",
    "volley config discovery tracer pathlifetimeticks 0,               true",
    "volley config discovery tracer pathlifetimeticks 1200,            true",
    "volley config discovery tracer pathlifetimeticks -1,              false",
    "volley config discovery tracer pathlifetimeticks 1201,            false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "volley config discovery torch enabled true",
    "volley config discovery torch enabled false",
    "volley config discovery beacon lifetimeticks 0",
    "volley config discovery beacon lifetimeticks 12000",
    "volley config discovery prospector radius 0",
    "volley config discovery prospector radius 16",
    "volley config discovery prospector durationticks 0",
    "volley config discovery prospector durationticks 1200",
    "volley config discovery prospector blocks add minecraft:stone",
    "volley config discovery prospector blocks remove minecraft:stone",
    "volley config discovery prospector blocks clear",
    "volley config discovery sonar radius 0",
    "volley config discovery sonar radius 32",
    "volley config discovery sonar durationticks 0",
    "volley config discovery sonar durationticks 1200",
    "volley config discovery tripwire lifetimeticks 20",
    "volley config discovery tripwire lifetimeticks 24000",
    "volley config discovery tripwire reportintervalticks 20",
    "volley config discovery tripwire reportintervalticks 1200",
    "volley config discovery tracer pathlifetimeticks 0",
    "volley config discovery tracer pathlifetimeticks 1200",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
