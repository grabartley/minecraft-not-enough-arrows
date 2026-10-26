package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ConfigOptionNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({
    "grapple.maxRangeBlocks,             maxrangeblocks",
    "explosive.gunpowder.delayTicks,     delayticks",
    "explosive,                          explosive",
  })
  void theCommandLiteralIsTheLastSegmentLowercased(String setting, String expected) {
    assertEquals(expected, ConfigOptionNodes.literalFor(setting));
  }

  @ParameterizedTest(name = "accepts \"{0}\"")
  @ValueSource(
      strings = {
        "volley config grapple maxrangeblocks 4",
        "volley config grapple maxrangeblocks 128",
        "volley config grapple pullspeed 0.1",
        "volley config grapple pullspeed 4.0",
        "volley config grapple ropesdecay true",
        "volley config grapple ropesdecay false",
      })
  void valuesInsideTheConfiguredBoundsAreAccepted(String command) {
    assertTrue(CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "rejects \"{0}\"")
  @ValueSource(
      strings = {
        "volley config grapple maxrangeblocks 3",
        "volley config grapple maxrangeblocks 129",
        "volley config grapple pullspeed 0.05",
        "volley config grapple pullspeed 4.1",
        "volley config grapple ropesdecay maybe",
        "volley config grapple maxrangeblocks notanumber",
      })
  void valuesOutsideTheConfiguredBoundsAreRejected(String command) {
    assertFalse(CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "rejects incomplete \"{0}\"")
  @ValueSource(strings = {"volley config grapple maxrangeblocks", "volley config grapple"})
  void anOptionWithoutAValueIsNotRunnable(String command) {
    assertFalse(CommandParsing.accepts(dispatcher, operator, command));
  }
}
