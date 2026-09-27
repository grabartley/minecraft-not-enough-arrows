package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AgricultureCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("agriculture", AgricultureCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config agriculture blossomradius 0,       true",
    "notenougharrows config agriculture blossomradius 8,       true",
    "notenougharrows config agriculture blossomradius -1,      false",
    "notenougharrows config agriculture blossomradius 9,       false",
    "notenougharrows config agriculture tillradius 0,          true",
    "notenougharrows config agriculture tillradius 8,          true",
    "notenougharrows config agriculture tillradius -1,         false",
    "notenougharrows config agriculture tillradius 9,          false",
    "notenougharrows config agriculture harvestradius 0,       true",
    "notenougharrows config agriculture harvestradius 8,       true",
    "notenougharrows config agriculture harvestradius -1,      false",
    "notenougharrows config agriculture harvestradius 9,       false",
    "notenougharrows config agriculture beecount 1,            true",
    "notenougharrows config agriculture beecount 8,            true",
    "notenougharrows config agriculture beecount 0,            false",
    "notenougharrows config agriculture beecount 9,            false",
    "notenougharrows config agriculture beelifetimeticks 20,   true",
    "notenougharrows config agriculture beelifetimeticks 6000, true",
    "notenougharrows config agriculture beelifetimeticks 19,   false",
    "notenougharrows config agriculture beelifetimeticks 6001, false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config agriculture blossomradius 0",
    "notenougharrows config agriculture blossomradius 8",
    "notenougharrows config agriculture tillradius 0",
    "notenougharrows config agriculture tillradius 8",
    "notenougharrows config agriculture harvestradius 0",
    "notenougharrows config agriculture harvestradius 8",
    "notenougharrows config agriculture beecount 1",
    "notenougharrows config agriculture beecount 8",
    "notenougharrows config agriculture beelifetimeticks 20",
    "notenougharrows config agriculture beelifetimeticks 6000",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
