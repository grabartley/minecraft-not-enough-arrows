package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FletchingCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "volley config fletching stationenabled true,   true",
    "volley config fletching stationenabled false,  true",
    "volley config fletching stationenabled,        false",
    "volley config fletching stationenabled maybe,  false",
    "volley config fletching,                       false",
  })
  void theStationToggleAcceptsOnlyABooleanValue(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @Test
  void aNonOperatorCannotReachTheStationToggle() {
    assertFalse(
        CommandParsing.accepts(
            dispatcher,
            CommandParsing.source(false),
            "volley config fletching stationenabled false"));
  }
}
