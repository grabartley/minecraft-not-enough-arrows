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
    "notenougharrows config social couriermaxpayload 1,                         true",
    "notenougharrows config social couriermaxpayload 64,                        true",
    "notenougharrows config social couriermaxpayload 0,                         false",
    "notenougharrows config social couriermaxpayload 65,                        false",
    "notenougharrows config social courierundeliverable add minecraft:stone,    true",
    "notenougharrows config social courierundeliverable remove minecraft:stone, true",
    "notenougharrows config social courierundeliverable clear,                  true",
    "notenougharrows config social courierundeliverable add,                    false",
    "notenougharrows config social courierundeliverable minecraft:stone,        false",
    "notenougharrows config social snowgolemlifetimeticks 20,                   true",
    "notenougharrows config social snowgolemlifetimeticks 12000,                true",
    "notenougharrows config social snowgolemlifetimeticks 19,                   false",
    "notenougharrows config social snowgolemlifetimeticks 12001,                false",
    "notenougharrows config social magnetradius 0,                              true",
    "notenougharrows config social magnetradius 16,                             true",
    "notenougharrows config social magnetradius -1,                             false",
    "notenougharrows config social magnetradius 17,                             false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "notenougharrows config social couriermaxpayload 1",
    "notenougharrows config social couriermaxpayload 64",
    "notenougharrows config social courierundeliverable add minecraft:stone",
    "notenougharrows config social courierundeliverable remove minecraft:stone",
    "notenougharrows config social courierundeliverable clear",
    "notenougharrows config social snowgolemlifetimeticks 20",
    "notenougharrows config social snowgolemlifetimeticks 12000",
    "notenougharrows config social magnetradius 0",
    "notenougharrows config social magnetradius 16",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
