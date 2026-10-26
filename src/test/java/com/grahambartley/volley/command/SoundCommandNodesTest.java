package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SoundCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("sound", SoundCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "volley config sound volume 0,    true",
    "volley config sound volume 0.5,  true",
    "volley config sound volume 1,    true",
    "volley config sound volume -0.1, false",
    "volley config sound volume 1.1,  false",
    "volley config sound volume,      false",
  })
  void theVolumeAcceptsOnlyValuesInsideItsBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({"volley config sound volume 0", "volley config sound volume 1"})
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
