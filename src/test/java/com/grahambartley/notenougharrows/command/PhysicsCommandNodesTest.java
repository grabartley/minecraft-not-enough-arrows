package com.grahambartley.notenougharrows.command;

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
    "notenougharrows config physics gravityimpactradius 0,                            true",
    "notenougharrows config physics gravityimpactradius 8,                            true",
    "notenougharrows config physics gravityimpactradius 9,                            false",
    "notenougharrows config physics gravityimpactradius -1,                           false",
    "notenougharrows config physics ricochetbouncecount 0,                            true",
    "notenougharrows config physics ricochetbouncecount 16,                           true",
    "notenougharrows config physics ricochetbouncecount 17,                           false",
    "notenougharrows config physics ricochetretainsdamage true,                       true",
    "notenougharrows config physics ricochetretainsdamage yes,                        false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config physics gravityblockexclusions add minecraft:sand,        true",
    "notenougharrows config physics gravityblockexclusions add sand,                  true",
    "notenougharrows config physics gravityblockexclusions remove minecraft:sand,     true",
    "notenougharrows config physics gravityblockexclusions clear,                     true",
    "notenougharrows config physics gravityblockexclusions add,                       false",
    "notenougharrows config physics gravityblockexclusions clear extra,               false",
    "notenougharrows config physics gravityblockexclusions add minecraft:Sand,        false",
  })
  void theExclusionListIsEditedThroughAddRemoveAndClear(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({
    "ALREADY_PRESENT, command.not-enough-arrows.exclusions.already_present",
    "NOT_PRESENT,     command.not-enough-arrows.exclusions.not_present",
    "LIST_FULL,       command.not-enough-arrows.exclusions.full",
    "INVALID_ID,      command.not-enough-arrows.exclusions.invalid",
  })
  void eachRejectionReasonGetsItsOwnMessage(GravityExclusions.Outcome outcome, String expected) {
    assertEquals(expected, PhysicsCommandNodes.rejectionKey(outcome));
  }
}
