package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.option.DiscoveryOptions;
import com.grahambartley.notenougharrows.config.option.IdentifierListOption;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

class IdentifierListNodesTest {
  private static final String ROOT = "root";
  private static final IdentifierListOption<NotEnoughArrowsConfig> OPTION =
      DiscoveryOptions.PROSPECTOR_BLOCKS;

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource source;

  @BeforeEach
  void setUp() {
    dispatcher = new CommandDispatcher<>();
    dispatcher.register(CommandManager.literal(ROOT).then(IdentifierListNodes.build(OPTION)));
    source = CommandParsing.source(true);
  }

  @Test
  void mountsTheListUnderItsOwnSettingName() {
    assertEquals("blocks", IdentifierListNodes.build(OPTION).getLiteral());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "root blocks add minecraft:stone,     true",
    "root blocks add stone,               true",
    "root blocks remove minecraft:stone,  true",
    "root blocks clear,                   true",
    "root blocks add,                     false",
    "root blocks remove,                  false",
    "root blocks clear extra,             false",
    "root blocks add minecraft:Stone,     false",
    "root blocks minecraft:stone,         false",
  })
  void editsTheListThroughAddRemoveAndClear(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, source, command));
  }

  @Test
  void anEditThatChangesNothingIsRejectedWithTheSettingNamed() throws CommandSyntaxException {
    final int result = dispatcher.execute("root blocks remove minecraft:stone", source);

    final ArgumentCaptor<Text> error = ArgumentCaptor.forClass(Text.class);
    verify(source).sendError(error.capture());
    final TranslatableTextContent content =
        assertInstanceOf(TranslatableTextContent.class, error.getValue().getContent());
    assertEquals(0, result);
    assertEquals("command.not-enough-arrows.list.not_present", content.getKey());
    assertArrayEquals(new Object[] {"discovery.prospector.blocks"}, content.getArgs());
  }

  @Test
  void addingAnEntryAlreadyOnTheListIsRejected() throws CommandSyntaxException {
    final int result = dispatcher.execute("root blocks add minecraft:diamond_ore", source);

    final ArgumentCaptor<Text> error = ArgumentCaptor.forClass(Text.class);
    verify(source).sendError(error.capture());
    assertEquals(0, result);
    assertEquals(
        "command.not-enough-arrows.list.already_present",
        ((TranslatableTextContent) error.getValue().getContent()).getKey());
  }

  @ParameterizedTest(name = "{0} is not a rejection")
  @EnumSource(
      value = IdentifierListEdits.Outcome.class,
      names = {"ADDED", "REMOVED", "CLEARED"})
  void aSuccessfulEditHasNoRejectionMessage(IdentifierListEdits.Outcome outcome) {
    assertThrows(IllegalArgumentException.class, () -> IdentifierListNodes.rejectionKey(outcome));
  }

  @ParameterizedTest(name = "{0} -> {1}")
  @CsvSource({
    "ALREADY_PRESENT, command.not-enough-arrows.list.already_present",
    "NOT_PRESENT,     command.not-enough-arrows.list.not_present",
    "LIST_FULL,       command.not-enough-arrows.list.full",
    "INVALID_ID,      command.not-enough-arrows.list.invalid",
    "TOO_LONG,        command.not-enough-arrows.list.too_long",
  })
  void eachRejectionReasonGetsItsOwnMessage(IdentifierListEdits.Outcome outcome, String expected) {
    assertEquals(expected, IdentifierListNodes.rejectionKey(outcome));
  }
}
