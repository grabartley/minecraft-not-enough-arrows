package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PhysicsCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "volley config physics gravityimpactradius 0,                            true",
    "volley config physics gravityimpactradius 8,                            true",
    "volley config physics gravityimpactradius 9,                            false",
    "volley config physics gravityimpactradius -1,                           false",
    "volley config physics ricochetbouncecount 0,                            true",
    "volley config physics ricochetbouncecount 16,                           true",
    "volley config physics ricochetbouncecount 17,                           false",
    "volley config physics ricochetretainsdamage true,                       true",
    "volley config physics ricochetretainsdamage yes,                        false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "volley config physics gravityblockexclusions add minecraft:sand,        true",
    "volley config physics gravityblockexclusions add sand,                  true",
    "volley config physics gravityblockexclusions remove minecraft:sand,     true",
    "volley config physics gravityblockexclusions clear,                     true",
    "volley config physics gravityblockexclusions add,                       false",
    "volley config physics gravityblockexclusions clear extra,               false",
    "volley config physics gravityblockexclusions add minecraft:Sand,        false",
  })
  void theExclusionListIsEditedThroughAddRemoveAndClear(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }
}
