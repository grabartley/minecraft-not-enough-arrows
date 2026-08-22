package com.grahambartley.morearrows.command;

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
    "morearrows config physics gravityimpactradius 0,                            true",
    "morearrows config physics gravityimpactradius 8,                            true",
    "morearrows config physics gravityimpactradius 9,                            false",
    "morearrows config physics gravityimpactradius -1,                           false",
    "morearrows config physics ricochetbouncecount 0,                            true",
    "morearrows config physics ricochetbouncecount 16,                           true",
    "morearrows config physics ricochetbouncecount 17,                           false",
    "morearrows config physics ricochetretainsdamage true,                       true",
    "morearrows config physics ricochetretainsdamage yes,                        false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "morearrows config physics gravityblockexclusions add minecraft:sand,        true",
    "morearrows config physics gravityblockexclusions add sand,                  true",
    "morearrows config physics gravityblockexclusions remove minecraft:sand,     true",
    "morearrows config physics gravityblockexclusions clear,                     true",
    "morearrows config physics gravityblockexclusions add,                       false",
    "morearrows config physics gravityblockexclusions clear extra,               false",
    "morearrows config physics gravityblockexclusions add minecraft:Sand,        false",
  })
  void theExclusionListIsEditedThroughAddRemoveAndClear(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({
    "ALREADY_PRESENT, command.more-arrows.exclusions.already_present",
    "NOT_PRESENT,     command.more-arrows.exclusions.not_present",
    "LIST_FULL,       command.more-arrows.exclusions.full",
    "INVALID_ID,      command.more-arrows.exclusions.invalid",
  })
  void eachRejectionReasonGetsItsOwnMessage(GravityExclusions.Outcome outcome, String expected) {
    assertEquals(expected, PhysicsCommandNodes.rejectionKey(outcome));
  }
}
