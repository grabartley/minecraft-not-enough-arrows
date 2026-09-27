package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TraversalCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("traversal", TraversalCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config traversal zipline maxspanblocks 2,         true",
    "notenougharrows config traversal zipline maxspanblocks 128,       true",
    "notenougharrows config traversal zipline maxspanblocks 1,         false",
    "notenougharrows config traversal zipline maxspanblocks 129,       false",
    "notenougharrows config traversal zipline pendingwindowticks 20,   true",
    "notenougharrows config traversal zipline pendingwindowticks 6000, true",
    "notenougharrows config traversal zipline pendingwindowticks 19,   false",
    "notenougharrows config traversal zipline pendingwindowticks 6001, false",
    "notenougharrows config traversal zipline ridespeed 0.1,           true",
    "notenougharrows config traversal zipline ridespeed 3.0,           true",
    "notenougharrows config traversal zipline ridespeed 0.0,           false",
    "notenougharrows config traversal zipline ridespeed 3.1,           false",
    "notenougharrows config traversal zipline lifetimeticks 0,         true",
    "notenougharrows config traversal zipline lifetimeticks 12000,     true",
    "notenougharrows config traversal zipline lifetimeticks -1,        false",
    "notenougharrows config traversal zipline lifetimeticks 12001,     false",
    "notenougharrows config traversal tow rangeblocks 1,               true",
    "notenougharrows config traversal tow rangeblocks 128,             true",
    "notenougharrows config traversal tow rangeblocks 0,               false",
    "notenougharrows config traversal tow rangeblocks 129,             false",
    "notenougharrows config traversal tow maxticks 1,                  true",
    "notenougharrows config traversal tow maxticks 1200,               true",
    "notenougharrows config traversal tow maxticks 0,                  false",
    "notenougharrows config traversal tow maxticks 1201,               false",
    "notenougharrows config traversal updraft heightblocks 1,          true",
    "notenougharrows config traversal updraft heightblocks 64,         true",
    "notenougharrows config traversal updraft heightblocks 0,          false",
    "notenougharrows config traversal updraft heightblocks 65,         false",
    "notenougharrows config traversal updraft lifetimeticks 0,         true",
    "notenougharrows config traversal updraft lifetimeticks 1200,      true",
    "notenougharrows config traversal updraft lifetimeticks -1,        false",
    "notenougharrows config traversal updraft lifetimeticks 1201,      false",
    "notenougharrows config traversal updraft strength 0.0,            true",
    "notenougharrows config traversal updraft strength 2.0,            true",
    "notenougharrows config traversal updraft strength -0.1,           false",
    "notenougharrows config traversal updraft strength 2.1,            false",
    "notenougharrows config traversal vine lengthblocks 1,             true",
    "notenougharrows config traversal vine lengthblocks 64,            true",
    "notenougharrows config traversal vine lengthblocks 0,             false",
    "notenougharrows config traversal vine lengthblocks 65,            false",
    "notenougharrows config traversal trampoline strength 0.0,         true",
    "notenougharrows config traversal trampoline strength 4.0,         true",
    "notenougharrows config traversal trampoline strength -0.1,        false",
    "notenougharrows config traversal trampoline strength 4.1,         false",
    "notenougharrows config traversal trampoline lifetimeticks 0,      true",
    "notenougharrows config traversal trampoline lifetimeticks 12000,  true",
    "notenougharrows config traversal trampoline lifetimeticks -1,     false",
    "notenougharrows config traversal trampoline lifetimeticks 12001,  false",
    "notenougharrows config traversal scaffold heightblocks 1,         true",
    "notenougharrows config traversal scaffold heightblocks 64,        true",
    "notenougharrows config traversal scaffold heightblocks 0,         false",
    "notenougharrows config traversal scaffold heightblocks 65,        false",
    "notenougharrows config traversal scaffold lifetimeticks 0,        true",
    "notenougharrows config traversal scaffold lifetimeticks 12000,    true",
    "notenougharrows config traversal scaffold lifetimeticks -1,       false",
    "notenougharrows config traversal scaffold lifetimeticks 12001,    false",
    "notenougharrows config traversal bridge lengthblocks 1,           true",
    "notenougharrows config traversal bridge lengthblocks 64,          true",
    "notenougharrows config traversal bridge lengthblocks 0,           false",
    "notenougharrows config traversal bridge lengthblocks 65,          false",
    "notenougharrows config traversal bridge lifetimeticks 0,          true",
    "notenougharrows config traversal bridge lifetimeticks 12000,      true",
    "notenougharrows config traversal bridge lifetimeticks -1,         false",
    "notenougharrows config traversal bridge lifetimeticks 12001,      false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config traversal zipline maxspanblocks 2",
    "notenougharrows config traversal zipline maxspanblocks 128",
    "notenougharrows config traversal zipline pendingwindowticks 20",
    "notenougharrows config traversal zipline pendingwindowticks 6000",
    "notenougharrows config traversal zipline ridespeed 0.1",
    "notenougharrows config traversal zipline ridespeed 3.0",
    "notenougharrows config traversal zipline lifetimeticks 0",
    "notenougharrows config traversal zipline lifetimeticks 12000",
    "notenougharrows config traversal tow rangeblocks 1",
    "notenougharrows config traversal tow rangeblocks 128",
    "notenougharrows config traversal tow maxticks 1",
    "notenougharrows config traversal tow maxticks 1200",
    "notenougharrows config traversal updraft heightblocks 1",
    "notenougharrows config traversal updraft heightblocks 64",
    "notenougharrows config traversal updraft lifetimeticks 0",
    "notenougharrows config traversal updraft lifetimeticks 1200",
    "notenougharrows config traversal updraft strength 0.0",
    "notenougharrows config traversal updraft strength 2.0",
    "notenougharrows config traversal vine lengthblocks 1",
    "notenougharrows config traversal vine lengthblocks 64",
    "notenougharrows config traversal trampoline strength 0.0",
    "notenougharrows config traversal trampoline strength 4.0",
    "notenougharrows config traversal trampoline lifetimeticks 0",
    "notenougharrows config traversal trampoline lifetimeticks 12000",
    "notenougharrows config traversal scaffold heightblocks 1",
    "notenougharrows config traversal scaffold heightblocks 64",
    "notenougharrows config traversal scaffold lifetimeticks 0",
    "notenougharrows config traversal scaffold lifetimeticks 12000",
    "notenougharrows config traversal bridge lengthblocks 1",
    "notenougharrows config traversal bridge lengthblocks 64",
    "notenougharrows config traversal bridge lifetimeticks 0",
    "notenougharrows config traversal bridge lifetimeticks 12000",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
