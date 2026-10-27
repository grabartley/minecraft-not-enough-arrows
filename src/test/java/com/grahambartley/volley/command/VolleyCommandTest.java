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
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

class VolleyCommandTest {
  private static final List<String> FAMILIES =
      List.of(
          "explosive",
          "grapple",
          "utility",
          "physics",
          "ender",
          "combat",
          "control",
          "traversal",
          "terrain",
          "agriculture",
          "discovery",
          "chaos",
          "social",
          "fletching",
          "sound");

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

  private static String pathOf(final ConfigOption<VolleyConfig> option) {
    return "config " + option.id().toLowerCase(Locale.ROOT).replace('.', ' ');
  }

  private static String plain(final float value) {
    return new BigDecimal(Float.toString(value)).toPlainString();
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvFileSource(
      resources = {
        "/command-bounds/explosive.csv",
        "/command-bounds/grapple.csv",
        "/command-bounds/utility.csv",
        "/command-bounds/physics.csv",
        "/command-bounds/ender.csv",
        "/command-bounds/combat.csv",
        "/command-bounds/control.csv",
        "/command-bounds/traversal.csv",
        "/command-bounds/terrain.csv",
        "/command-bounds/agriculture.csv",
        "/command-bounds/discovery.csv",
        "/command-bounds/chaos.csv",
        "/command-bounds/social.csv",
        "/command-bounds/fletching.csv",
        "/command-bounds/sound.csv"
      })
  void eachOptionAcceptsOnlyTheValuesItsFamilyStatesAsLiterals(
      final String command, final boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, operator, command));
  }

  @Test
  void everyOptionHasALiteralCaseInItsFamilyFile() throws IOException {
    final List<String> rows = new ArrayList<>();
    for (final String family : FAMILIES) {
      try (InputStream csv =
          VolleyCommandTest.class.getResourceAsStream("/command-bounds/" + family + ".csv")) {
        new String(csv.readAllBytes(), StandardCharsets.UTF_8).lines().forEach(rows::add);
      }
    }

    final List<String> uncovered =
        ServerConfigOptions.all().stream()
            .filter(
                option -> {
                  final String path = VolleyCommand.ROOT + " " + pathOf(option) + " ";
                  return rows.stream().noneMatch(row -> row.startsWith(path));
                })
            .map(ConfigOption::id)
            .toList();

    assertEquals(List.of(), uncovered);
  }

  @Test
  void mountsEverySectionUnderConfigInCatalogOrder() {
    assertEquals(
        FAMILIES,
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
