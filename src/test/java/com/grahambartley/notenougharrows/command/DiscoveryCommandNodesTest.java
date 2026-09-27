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
    "notenougharrows config discovery torchenabled true,                       true",
    "notenougharrows config discovery torchenabled false,                      true",
    "notenougharrows config discovery torchenabled yes,                        false",
    "notenougharrows config discovery beaconlifetimeticks 0,                   true",
    "notenougharrows config discovery beaconlifetimeticks 12000,               true",
    "notenougharrows config discovery beaconlifetimeticks -1,                  false",
    "notenougharrows config discovery beaconlifetimeticks 12001,               false",
    "notenougharrows config discovery revealradius 0,                          true",
    "notenougharrows config discovery revealradius 32,                         true",
    "notenougharrows config discovery revealradius -1,                         false",
    "notenougharrows config discovery revealradius 33,                         false",
    "notenougharrows config discovery revealdurationticks 0,                   true",
    "notenougharrows config discovery revealdurationticks 1200,                true",
    "notenougharrows config discovery revealdurationticks -1,                  false",
    "notenougharrows config discovery revealdurationticks 1201,                false",
    "notenougharrows config discovery prospectorblocks add minecraft:stone,    true",
    "notenougharrows config discovery prospectorblocks remove minecraft:stone, true",
    "notenougharrows config discovery prospectorblocks clear,                  true",
    "notenougharrows config discovery prospectorblocks add,                    false",
    "notenougharrows config discovery prospectorblocks minecraft:stone,        false",
    "notenougharrows config discovery watcherlifetimeticks 20,                 true",
    "notenougharrows config discovery watcherlifetimeticks 24000,              true",
    "notenougharrows config discovery watcherlifetimeticks 19,                 false",
    "notenougharrows config discovery watcherlifetimeticks 24001,              false",
    "notenougharrows config discovery watcherreportintervalticks 20,           true",
    "notenougharrows config discovery watcherreportintervalticks 1200,         true",
    "notenougharrows config discovery watcherreportintervalticks 19,           false",
    "notenougharrows config discovery watcherreportintervalticks 1201,         false",
    "notenougharrows config discovery tracerpathlifetimeticks 0,               true",
    "notenougharrows config discovery tracerpathlifetimeticks 1200,            true",
    "notenougharrows config discovery tracerpathlifetimeticks -1,              false",
    "notenougharrows config discovery tracerpathlifetimeticks 1201,            false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config discovery torchenabled true",
    "notenougharrows config discovery torchenabled false",
    "notenougharrows config discovery beaconlifetimeticks 0",
    "notenougharrows config discovery beaconlifetimeticks 12000",
    "notenougharrows config discovery revealradius 0",
    "notenougharrows config discovery revealradius 32",
    "notenougharrows config discovery revealdurationticks 0",
    "notenougharrows config discovery revealdurationticks 1200",
    "notenougharrows config discovery prospectorblocks add minecraft:stone",
    "notenougharrows config discovery prospectorblocks remove minecraft:stone",
    "notenougharrows config discovery prospectorblocks clear",
    "notenougharrows config discovery watcherlifetimeticks 20",
    "notenougharrows config discovery watcherlifetimeticks 24000",
    "notenougharrows config discovery watcherreportintervalticks 20",
    "notenougharrows config discovery watcherreportintervalticks 1200",
    "notenougharrows config discovery tracerpathlifetimeticks 0",
    "notenougharrows config discovery tracerpathlifetimeticks 1200",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
