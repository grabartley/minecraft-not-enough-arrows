package com.grahambartley.notenougharrows.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class ConfigUpdatesTest {

  @AfterEach
  void tearDown() {
    ServerConfigHolder.reset();
  }

  @SuppressWarnings("unchecked")
  private static CommandContext<ServerCommandSource> contextFor(final ServerCommandSource source) {
    final CommandContext<ServerCommandSource> context = mock(CommandContext.class);
    when(context.getSource()).thenReturn(source);
    return context;
  }

  @Test
  void anUpdateThatCannotBeSavedReportsFailureRatherThanSuccess() {
    final ServerCommandSource source = mock(ServerCommandSource.class);

    assertEquals(
        0,
        ConfigUpdates.apply(
            contextFor(source), NotEnoughArrowsConfig.defaults(), "grapple.maxRangeBlocks", "64"));
  }

  @Test
  void anUpdateThatCannotBeSavedTellsTheOperatorWhichSettingFailed() {
    final ServerCommandSource source = mock(ServerCommandSource.class);

    ConfigUpdates.apply(
        contextFor(source), NotEnoughArrowsConfig.defaults(), "grapple.maxRangeBlocks", "64");

    verify(source).sendError(ArgumentMatchers.any(Text.class));
    verify(source, never()).sendFeedback(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
  }

  @Test
  void aFailedUpdateLeavesTheLiveConfigAlone() {
    final ServerCommandSource source = mock(ServerCommandSource.class);
    final NotEnoughArrowsConfig live =
        NotEnoughArrowsConfig.defaults()
            .withGrapple(NotEnoughArrowsConfig.defaults().grapple().withMaxRangeBlocks(96));
    ServerConfigHolder.set(live);

    ConfigUpdates.apply(
        contextFor(source), NotEnoughArrowsConfig.defaults(), "grapple.maxRangeBlocks", "32");

    assertEquals(live, ServerConfigHolder.get());
  }
}
