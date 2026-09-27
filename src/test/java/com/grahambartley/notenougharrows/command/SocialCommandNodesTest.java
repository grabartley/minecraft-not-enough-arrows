package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SocialCommandNodesTest {

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
  }

  @Test
  void mountsItsFamilyUnderTheFamilyName() {
    assertEquals("social", SocialCommandNodes.build().getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "notenougharrows config social courier maxpayload 1,                         true",
    "notenougharrows config social courier maxpayload 64,                        true",
    "notenougharrows config social courier maxpayload 0,                         false",
    "notenougharrows config social courier maxpayload 65,                        false",
    "notenougharrows config social courier undeliverable add minecraft:stone,    true",
    "notenougharrows config social courier undeliverable remove minecraft:stone, true",
    "notenougharrows config social courier undeliverable clear,                  true",
    "notenougharrows config social courier undeliverable add,                    false",
    "notenougharrows config social courier undeliverable minecraft:stone,        false",
    "notenougharrows config social snowgolem lifetimeticks 20,                   true",
    "notenougharrows config social snowgolem lifetimeticks 12000,                true",
    "notenougharrows config social snowgolem lifetimeticks 19,                   false",
    "notenougharrows config social snowgolem lifetimeticks 12001,                false",
    "notenougharrows config social magnet radius 0,                              true",
    "notenougharrows config social magnet radius 16,                             true",
    "notenougharrows config social magnet radius -1,                             false",
    "notenougharrows config social magnet radius 17,                             false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config social courier maxpayload 1",
    "notenougharrows config social courier maxpayload 64",
    "notenougharrows config social courier undeliverable add minecraft:stone",
    "notenougharrows config social courier undeliverable remove minecraft:stone",
    "notenougharrows config social courier undeliverable clear",
    "notenougharrows config social snowgolem lifetimeticks 20",
    "notenougharrows config social snowgolem lifetimeticks 12000",
    "notenougharrows config social magnet radius 0",
    "notenougharrows config social magnet radius 16",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
