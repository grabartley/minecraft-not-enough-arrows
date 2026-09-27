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
    "notenougharrows config terrain drillenabled true,         true",
    "notenougharrows config terrain drillenabled false,        true",
    "notenougharrows config terrain drillenabled yes,          false",
    "notenougharrows config terrain drilltooltier 0,           true",
    "notenougharrows config terrain drilltooltier 2,           true",
    "notenougharrows config terrain drilltooltier -1,          false",
    "notenougharrows config terrain drilltooltier 3,           false",
    "notenougharrows config terrain pillarenabled true,        true",
    "notenougharrows config terrain pillarenabled false,       true",
    "notenougharrows config terrain pillarenabled yes,         false",
    "notenougharrows config terrain pillarheightblocks 1,      true",
    "notenougharrows config terrain pillarheightblocks 16,     true",
    "notenougharrows config terrain pillarheightblocks 0,      false",
    "notenougharrows config terrain pillarheightblocks 17,     false",
    "notenougharrows config terrain pillarlifetimeticks 0,     true",
    "notenougharrows config terrain pillarlifetimeticks 12000, true",
    "notenougharrows config terrain pillarlifetimeticks -1,    false",
    "notenougharrows config terrain pillarlifetimeticks 12001, false",
    "notenougharrows config terrain drainenabled true,         true",
    "notenougharrows config terrain drainenabled false,        true",
    "notenougharrows config terrain drainenabled yes,          false",
    "notenougharrows config terrain drainradius 0,             true",
    "notenougharrows config terrain drainradius 8,             true",
    "notenougharrows config terrain drainradius -1,            false",
    "notenougharrows config terrain drainradius 9,             false",
    "notenougharrows config terrain drainmaxblocks 1,          true",
    "notenougharrows config terrain drainmaxblocks 512,        true",
    "notenougharrows config terrain drainmaxblocks 0,          false",
    "notenougharrows config terrain drainmaxblocks 513,        false",
    "notenougharrows config terrain freezeenabled true,        true",
    "notenougharrows config terrain freezeenabled false,       true",
    "notenougharrows config terrain freezeenabled yes,         false",
    "notenougharrows config terrain freezeradius 0,            true",
    "notenougharrows config terrain freezeradius 8,            true",
    "notenougharrows config terrain freezeradius -1,           false",
    "notenougharrows config terrain freezeradius 9,            false",
    "notenougharrows config terrain webenabled true,           true",
    "notenougharrows config terrain webenabled false,          true",
    "notenougharrows config terrain webenabled yes,            false",
    "notenougharrows config terrain webpatchradius 0,          true",
    "notenougharrows config terrain webpatchradius 3,          true",
    "notenougharrows config terrain webpatchradius -1,         false",
    "notenougharrows config terrain webpatchradius 4,          false",
    "notenougharrows config terrain weblifetimeticks 0,        true",
    "notenougharrows config terrain weblifetimeticks 12000,    true",
    "notenougharrows config terrain weblifetimeticks -1,       false",
    "notenougharrows config terrain weblifetimeticks 12001,    false",
    "notenougharrows config terrain paintenabled true,         true",
    "notenougharrows config terrain paintenabled false,        true",
    "notenougharrows config terrain paintenabled yes,          false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config terrain drillenabled true",
    "notenougharrows config terrain drillenabled false",
    "notenougharrows config terrain drilltooltier 0",
    "notenougharrows config terrain drilltooltier 2",
    "notenougharrows config terrain pillarenabled true",
    "notenougharrows config terrain pillarenabled false",
    "notenougharrows config terrain pillarheightblocks 1",
    "notenougharrows config terrain pillarheightblocks 16",
    "notenougharrows config terrain pillarlifetimeticks 0",
    "notenougharrows config terrain pillarlifetimeticks 12000",
    "notenougharrows config terrain drainenabled true",
    "notenougharrows config terrain drainenabled false",
    "notenougharrows config terrain drainradius 0",
    "notenougharrows config terrain drainradius 8",
    "notenougharrows config terrain drainmaxblocks 1",
    "notenougharrows config terrain drainmaxblocks 512",
    "notenougharrows config terrain freezeenabled true",
    "notenougharrows config terrain freezeenabled false",
    "notenougharrows config terrain freezeradius 0",
    "notenougharrows config terrain freezeradius 8",
    "notenougharrows config terrain webenabled true",
    "notenougharrows config terrain webenabled false",
    "notenougharrows config terrain webpatchradius 0",
    "notenougharrows config terrain webpatchradius 3",
    "notenougharrows config terrain weblifetimeticks 0",
    "notenougharrows config terrain weblifetimeticks 12000",
    "notenougharrows config terrain paintenabled true",
    "notenougharrows config terrain paintenabled false",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
