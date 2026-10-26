package com.grahambartley.volley.command;

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
    "volley config terrain drill enabled true,         true",
    "volley config terrain drill enabled false,        true",
    "volley config terrain drill enabled yes,          false",
    "volley config terrain drill tooltier 0,           true",
    "volley config terrain drill tooltier 2,           true",
    "volley config terrain drill tooltier -1,          false",
    "volley config terrain drill tooltier 3,           false",
    "volley config terrain pillar enabled true,        true",
    "volley config terrain pillar enabled false,       true",
    "volley config terrain pillar enabled yes,         false",
    "volley config terrain pillar heightblocks 1,      true",
    "volley config terrain pillar heightblocks 16,     true",
    "volley config terrain pillar heightblocks 0,      false",
    "volley config terrain pillar heightblocks 17,     false",
    "volley config terrain pillar lifetimeticks 0,     true",
    "volley config terrain pillar lifetimeticks 12000, true",
    "volley config terrain pillar lifetimeticks -1,    false",
    "volley config terrain pillar lifetimeticks 12001, false",
    "volley config terrain drain enabled true,         true",
    "volley config terrain drain enabled false,        true",
    "volley config terrain drain enabled yes,          false",
    "volley config terrain drain radius 0,             true",
    "volley config terrain drain radius 8,             true",
    "volley config terrain drain radius -1,            false",
    "volley config terrain drain radius 9,             false",
    "volley config terrain drain maxblocks 1,          true",
    "volley config terrain drain maxblocks 512,        true",
    "volley config terrain drain maxblocks 0,          false",
    "volley config terrain drain maxblocks 513,        false",
    "volley config terrain freeze enabled true,        true",
    "volley config terrain freeze enabled false,       true",
    "volley config terrain freeze enabled yes,         false",
    "volley config terrain freeze radius 0,            true",
    "volley config terrain freeze radius 8,            true",
    "volley config terrain freeze radius -1,           false",
    "volley config terrain freeze radius 9,            false",
    "volley config terrain freeze maxblocks 1,         true",
    "volley config terrain freeze maxblocks 512,       true",
    "volley config terrain freeze maxblocks 0,         false",
    "volley config terrain freeze maxblocks 513,       false",
    "volley config terrain web enabled true,           true",
    "volley config terrain web enabled false,          true",
    "volley config terrain web enabled yes,            false",
    "volley config terrain web patchradius 0,          true",
    "volley config terrain web patchradius 3,          true",
    "volley config terrain web patchradius -1,         false",
    "volley config terrain web patchradius 4,          false",
    "volley config terrain web lifetimeticks 0,        true",
    "volley config terrain web lifetimeticks 12000,    true",
    "volley config terrain web lifetimeticks -1,       false",
    "volley config terrain web lifetimeticks 12001,    false",
    "volley config terrain paint enabled true,         true",
    "volley config terrain paint enabled false,        true",
    "volley config terrain paint enabled yes,          false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "volley config terrain drill enabled true",
    "volley config terrain drill enabled false",
    "volley config terrain drill tooltier 0",
    "volley config terrain drill tooltier 2",
    "volley config terrain pillar enabled true",
    "volley config terrain pillar enabled false",
    "volley config terrain pillar heightblocks 1",
    "volley config terrain pillar heightblocks 16",
    "volley config terrain pillar lifetimeticks 0",
    "volley config terrain pillar lifetimeticks 12000",
    "volley config terrain drain enabled true",
    "volley config terrain drain enabled false",
    "volley config terrain drain radius 0",
    "volley config terrain drain radius 8",
    "volley config terrain drain maxblocks 1",
    "volley config terrain drain maxblocks 512",
    "volley config terrain freeze enabled true",
    "volley config terrain freeze enabled false",
    "volley config terrain freeze radius 0",
    "volley config terrain freeze radius 8",
    "volley config terrain freeze maxblocks 1",
    "volley config terrain freeze maxblocks 512",
    "volley config terrain web enabled true",
    "volley config terrain web enabled false",
    "volley config terrain web patchradius 0",
    "volley config terrain web patchradius 3",
    "volley config terrain web lifetimeticks 0",
    "volley config terrain web lifetimeticks 12000",
    "volley config terrain paint enabled true",
    "volley config terrain paint enabled false",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
