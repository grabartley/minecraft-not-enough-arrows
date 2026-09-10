package com.grahambartley.morearrows.command;

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
    "morearrows config fletching stationenabled true,   true",
    "morearrows config fletching stationenabled false,  true",
    "morearrows config fletching stationenabled,        false",
    "morearrows config fletching stationenabled maybe,  false",
    "morearrows config fletching,                       false",
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
            "morearrows config fletching stationenabled false"));
  }
}
