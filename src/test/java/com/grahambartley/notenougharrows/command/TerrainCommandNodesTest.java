package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TerrainCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("terrain", TerrainCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config terrain drill enabled true,         true",
    "notenougharrows config terrain drill enabled false,        true",
    "notenougharrows config terrain drill enabled yes,          false",
    "notenougharrows config terrain drill tooltier 0,           true",
    "notenougharrows config terrain drill tooltier 2,           true",
    "notenougharrows config terrain drill tooltier -1,          false",
    "notenougharrows config terrain drill tooltier 3,           false",
    "notenougharrows config terrain pillar enabled true,        true",
    "notenougharrows config terrain pillar enabled false,       true",
    "notenougharrows config terrain pillar enabled yes,         false",
    "notenougharrows config terrain pillar heightblocks 1,      true",
    "notenougharrows config terrain pillar heightblocks 16,     true",
    "notenougharrows config terrain pillar heightblocks 0,      false",
    "notenougharrows config terrain pillar heightblocks 17,     false",
    "notenougharrows config terrain pillar lifetimeticks 0,     true",
    "notenougharrows config terrain pillar lifetimeticks 12000, true",
    "notenougharrows config terrain pillar lifetimeticks -1,    false",
    "notenougharrows config terrain pillar lifetimeticks 12001, false",
    "notenougharrows config terrain drain enabled true,         true",
    "notenougharrows config terrain drain enabled false,        true",
    "notenougharrows config terrain drain enabled yes,          false",
    "notenougharrows config terrain drain radius 0,             true",
    "notenougharrows config terrain drain radius 8,             true",
    "notenougharrows config terrain drain radius -1,            false",
    "notenougharrows config terrain drain radius 9,             false",
    "notenougharrows config terrain drain maxblocks 1,          true",
    "notenougharrows config terrain drain maxblocks 512,        true",
    "notenougharrows config terrain drain maxblocks 0,          false",
    "notenougharrows config terrain drain maxblocks 513,        false",
    "notenougharrows config terrain freeze enabled true,        true",
    "notenougharrows config terrain freeze enabled false,       true",
    "notenougharrows config terrain freeze enabled yes,         false",
    "notenougharrows config terrain freeze radius 0,            true",
    "notenougharrows config terrain freeze radius 8,            true",
    "notenougharrows config terrain freeze radius -1,           false",
    "notenougharrows config terrain freeze radius 9,            false",
    "notenougharrows config terrain freeze maxblocks 1,         true",
    "notenougharrows config terrain freeze maxblocks 512,       true",
    "notenougharrows config terrain freeze maxblocks 0,         false",
    "notenougharrows config terrain freeze maxblocks 513,       false",
    "notenougharrows config terrain web enabled true,           true",
    "notenougharrows config terrain web enabled false,          true",
    "notenougharrows config terrain web enabled yes,            false",
    "notenougharrows config terrain web patchradius 0,          true",
    "notenougharrows config terrain web patchradius 3,          true",
    "notenougharrows config terrain web patchradius -1,         false",
    "notenougharrows config terrain web patchradius 4,          false",
    "notenougharrows config terrain web lifetimeticks 0,        true",
    "notenougharrows config terrain web lifetimeticks 12000,    true",
    "notenougharrows config terrain web lifetimeticks -1,       false",
    "notenougharrows config terrain web lifetimeticks 12001,    false",
    "notenougharrows config terrain paint enabled true,         true",
    "notenougharrows config terrain paint enabled false,        true",
    "notenougharrows config terrain paint enabled yes,          false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config terrain drill enabled true",
    "notenougharrows config terrain drill enabled false",
    "notenougharrows config terrain drill tooltier 0",
    "notenougharrows config terrain drill tooltier 2",
    "notenougharrows config terrain pillar enabled true",
    "notenougharrows config terrain pillar enabled false",
    "notenougharrows config terrain pillar heightblocks 1",
    "notenougharrows config terrain pillar heightblocks 16",
    "notenougharrows config terrain pillar lifetimeticks 0",
    "notenougharrows config terrain pillar lifetimeticks 12000",
    "notenougharrows config terrain drain enabled true",
    "notenougharrows config terrain drain enabled false",
    "notenougharrows config terrain drain radius 0",
    "notenougharrows config terrain drain radius 8",
    "notenougharrows config terrain drain maxblocks 1",
    "notenougharrows config terrain drain maxblocks 512",
    "notenougharrows config terrain freeze enabled true",
    "notenougharrows config terrain freeze enabled false",
    "notenougharrows config terrain freeze radius 0",
    "notenougharrows config terrain freeze radius 8",
    "notenougharrows config terrain freeze maxblocks 1",
    "notenougharrows config terrain freeze maxblocks 512",
    "notenougharrows config terrain web enabled true",
    "notenougharrows config terrain web enabled false",
    "notenougharrows config terrain web patchradius 0",
    "notenougharrows config terrain web patchradius 3",
    "notenougharrows config terrain web lifetimeticks 0",
    "notenougharrows config terrain web lifetimeticks 12000",
    "notenougharrows config terrain paint enabled true",
    "notenougharrows config terrain paint enabled false",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
