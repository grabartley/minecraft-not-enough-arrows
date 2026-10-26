package com.grahambartley.volley.command;

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
    "volley config social courier maxpayload 1,                         true",
    "volley config social courier maxpayload 64,                        true",
    "volley config social courier maxpayload 0,                         false",
    "volley config social courier maxpayload 65,                        false",
    "volley config social courier undeliverable add minecraft:stone,    true",
    "volley config social courier undeliverable remove minecraft:stone, true",
    "volley config social courier undeliverable clear,                  true",
    "volley config social courier undeliverable add,                    false",
    "volley config social courier undeliverable minecraft:stone,        false",
    "volley config social snowgolem lifetimeticks 20,                   true",
    "volley config social snowgolem lifetimeticks 12000,                true",
    "volley config social snowgolem lifetimeticks 19,                   false",
    "volley config social snowgolem lifetimeticks 12001,                false",
    "volley config social magnet radius 0,                              true",
    "volley config social magnet radius 16,                             true",
    "volley config social magnet radius -1,                             false",
    "volley config social magnet radius 17,                             false",
  })
  void eachOptionAcceptsOnlyValuesInsideItsConfiguredBounds(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"{0}\"")
  @CsvSource({
    "volley config social courier maxpayload 1",
    "volley config social courier maxpayload 64",
    "volley config social courier undeliverable add minecraft:stone",
    "volley config social courier undeliverable remove minecraft:stone",
    "volley config social courier undeliverable clear",
    "volley config social snowgolem lifetimeticks 20",
    "volley config social snowgolem lifetimeticks 12000",
    "volley config social magnet radius 0",
    "volley config social magnet radius 16",
  })
  void everyMutatingNodeIsGatedBehindOperatorPermission(String command) {
    assertEquals(false, CommandParsing.accepts(dispatcher, CommandParsing.source(false), command));
  }
}
