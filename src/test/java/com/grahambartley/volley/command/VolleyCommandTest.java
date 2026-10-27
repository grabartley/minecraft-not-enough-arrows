package com.grahambartley.volley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.config.option.BooleanOption;
import com.grahambartley.volley.config.option.ConfigOption;
import com.grahambartley.volley.config.option.FloatOption;
import com.grahambartley.volley.config.option.IdentifierListOption;
import com.grahambartley.volley.config.option.IntOption;
import com.grahambartley.volley.config.option.ServerConfigOptions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class VolleyCommandTest {

  private static final String OLD_ALIAS = "nea";

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource operator;
  private ServerCommandSource everyone;

  @BeforeEach
  void setUp() {
    dispatcher = CommandParsing.dispatcher();
    operator = CommandParsing.source(true);
    everyone = CommandParsing.source(false);
  }

  static Stream<Arguments> operatorCommands() {
    return Stream.concat(
            Stream.of("config reset"),
            ServerConfigOptions.all().stream().flatMap(VolleyCommandTest::acceptedValues))
        .map(Arguments::of);
  }

  static Stream<Arguments> outOfBoundsCommands() {
    return ServerConfigOptions.all().stream()
        .flatMap(VolleyCommandTest::rejectedValues)
        .map(Arguments::of);
  }

  private static Stream<String> acceptedValues(final ConfigOption<VolleyConfig> option) {
    final String path = pathOf(option);
    return switch (option) {
      case IntOption<VolleyConfig> intOption ->
          Stream.of(path + " " + intOption.min(), path + " " + intOption.max());
      case FloatOption<VolleyConfig> floatOption ->
          Stream.of(path + " " + plain(floatOption.min()), path + " " + plain(floatOption.max()));
      case BooleanOption<VolleyConfig> booleanOption -> Stream.of(path + " true", path + " false");
      case IdentifierListOption<VolleyConfig> listOption ->
          Stream.of(path + " add minecraft:stone", path + " clear");
    };
  }

  private static Stream<String> rejectedValues(final ConfigOption<VolleyConfig> option) {
    final String path = pathOf(option);
    return switch (option) {
      case IntOption<VolleyConfig> intOption ->
          Stream.of(path + " " + (intOption.min() - 1), path + " " + (intOption.max() + 1));
      case FloatOption<VolleyConfig> floatOption ->
          Stream.of(
              path + " " + plain(floatOption.min() - 1f),
              path + " " + plain(floatOption.max() + 1f));
      case BooleanOption<VolleyConfig> booleanOption -> Stream.of(path + " maybe");
      case IdentifierListOption<VolleyConfig> listOption -> Stream.of(path + " 3");
    };
  }

  private static String pathOf(final ConfigOption<VolleyConfig> option) {
    return "config " + option.id().toLowerCase(Locale.ROOT).replace('.', ' ');
  }

  private static String plain(final float value) {
    return new BigDecimal(Float.toString(value)).toPlainString();
  }

  @ParameterizedTest(name = "an operator may not run \"volley {0}\"")
  @MethodSource("outOfBoundsCommands")
  void everyOptionRefusesAValueOutsideItsBounds(String command) {
    assertFalse(CommandParsing.accepts(dispatcher, operator, VolleyCommand.ROOT + " " + command));
  }

  @Test
  void mountsEverySectionUnderConfigInCatalogOrder() {
    assertEquals(
        ServerConfigOptions.sections().stream().map(section -> section.id()).toList(),
        dispatcher
            .getRoot()
            .getChild(VolleyCommand.ROOT)
            .getChild(VolleyCommand.CONFIG)
            .getChildren()
            .stream()
            .map(CommandNode::getName)
            .filter(name -> !name.equals(VolleyCommand.RESET))
            .toList());
  }

  @Test
  void theRootCommandRunsOnItsOwn() {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, VolleyCommand.ROOT));
  }

  @Test
  void statusIsReadableWithoutOperatorPermission() {
    assertTrue(CommandParsing.accepts(dispatcher, everyone, VolleyCommand.ROOT + " status"));
  }

  @ParameterizedTest(name = "an operator may run \"volley {0}\"")
  @MethodSource("operatorCommands")
  void everyOptionIsReachableByAnOperatorAtItsBounds(String command) {
    assertTrue(CommandParsing.accepts(dispatcher, operator, VolleyCommand.ROOT + " " + command));
  }

  @ParameterizedTest(name = "a non-operator may not run \"volley {0}\"")
  @MethodSource("operatorCommands")
  void mutatingCommandsAreGatedBehindOperatorPermission(String command) {
    assertFalse(CommandParsing.accepts(dispatcher, everyone, VolleyCommand.ROOT + " " + command));
  }

  @Test
  void anUnknownSubcommandIsNotAccepted() {
    assertFalse(
        CommandParsing.accepts(dispatcher, operator, VolleyCommand.ROOT + " config fletching 1"));
  }

  @Test
  void theOldAliasIsGone() {
    assertFalse(CommandParsing.accepts(dispatcher, everyone, OLD_ALIAS));
    assertFalse(CommandParsing.accepts(dispatcher, everyone, OLD_ALIAS + " status"));
  }
}
