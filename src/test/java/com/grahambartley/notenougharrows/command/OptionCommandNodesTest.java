package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.option.BooleanOption;
import com.grahambartley.notenougharrows.config.option.ConfigOption;
import com.grahambartley.notenougharrows.config.option.ConfigSection;
import com.grahambartley.notenougharrows.config.option.FloatOption;
import com.grahambartley.notenougharrows.config.option.IdentifierListOption;
import com.grahambartley.notenougharrows.config.option.IntOption;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OptionCommandNodesTest {
  private static final ConfigSection<NotEnoughArrowsConfig> SAMPLE =
      new ConfigSection<>(
          "sample",
          List.<ConfigOption<NotEnoughArrowsConfig>>of(
              new IntOption<>("sample.count", 1, 8, config -> 3, (config, value) -> config),
              new FloatOption<>(
                  "sample.speed", 0.5f, 2.0f, 0.1f, config -> 1.0f, (config, value) -> config),
              new BooleanOption<>("sample.enabled", config -> true, (config, value) -> config),
              new IdentifierListOption<>(
                  "sample.blocks", 4, config -> List.of(), (config, value) -> config),
              new IntOption<>("sample.inner.first", 0, 4, config -> 0, (config, value) -> config),
              new BooleanOption<>(
                  "sample.inner.second", config -> false, (config, value) -> config)));

  private CommandDispatcher<ServerCommandSource> dispatcher;
  private ServerCommandSource source;

  @BeforeEach
  void setUp() {
    dispatcher = new CommandDispatcher<>();
    dispatcher.register(CommandManager.literal("root").then(OptionCommandNodes.section(SAMPLE)));
    source = CommandParsing.source(true);
  }

  @Test
  void refusesAnOptionWhoseIdSitsOutsideItsSection() {
    final ConfigSection<NotEnoughArrowsConfig> misfiled =
        new ConfigSection<>(
            "sample",
            List.<ConfigOption<NotEnoughArrowsConfig>>of(
                new BooleanOption<>("other.enabled", config -> true, (config, value) -> config)));

    assertThrows(IllegalArgumentException.class, () -> OptionCommandNodes.section(misfiled));
  }

  @Test
  void mountsASectionUnderItsOwnName() {
    assertEquals("sample", OptionCommandNodes.section(SAMPLE).getLiteral());
  }

  @Test
  void givesEveryOptionInTheSectionItsOwnNodeInCatalogOrder() {
    final CommandNode<ServerCommandSource> section = OptionCommandNodes.section(SAMPLE).build();

    assertEquals(
        List.of("count", "speed", "enabled", "blocks", "inner"),
        section.getChildren().stream().map(CommandNode::getName).toList());
  }

  @Test
  void groupsOptionsThatShareAnArrowUnderOneNode() {
    final CommandNode<ServerCommandSource> inner =
        OptionCommandNodes.section(SAMPLE).build().getChild("inner");

    assertEquals(
        List.of("first", "second"),
        inner.getChildren().stream().map(CommandNode::getName).toList());
  }

  @ParameterizedTest(name = "\"{0}\" accepted={1}")
  @CsvSource({
    "root sample count 1,                     true",
    "root sample count 8,                     true",
    "root sample count 0,                     false",
    "root sample count 9,                     false",
    "root sample speed 0.5,                   true",
    "root sample speed 2.0,                   true",
    "root sample speed 0.4,                   false",
    "root sample speed 2.1,                   false",
    "root sample enabled false,               true",
    "root sample enabled maybe,               false",
    "root sample blocks add minecraft:stone,  true",
    "root sample blocks clear,                true",
    "root sample blocks 3,                    false",
    "root sample count,                       false",
    "root sample inner first 4,               true",
    "root sample inner first 5,               false",
    "root sample inner second true,           true",
    "root sample inner 1,                     false",
  })
  void eachOptionKindTakesTheArgumentItsTypeAndBoundsAllow(String command, boolean accepted) {
    assertEquals(accepted, CommandParsing.accepts(dispatcher, source, command));
  }
}
