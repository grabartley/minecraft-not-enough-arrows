package com.grahambartley.volley.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConfigRowTextTest {
  private static final Text TEXT = Text.literal("Explosions Damage Terrain");

  private final TextRenderer textRenderer = mock(TextRenderer.class);

  @Test
  void keepsOnlyTheFirstLineTheTextRendererWrappedTo() {
    final OrderedText first = OrderedText.EMPTY;
    final OrderedText second = OrderedText.styledForwardsVisitedString("overflow", null);
    when(textRenderer.wrapLines(any(Text.class), anyInt())).thenReturn(List.of(first, second));

    assertSame(first, ConfigRowText.trimmed(textRenderer, TEXT, 120));
  }

  @Test
  void fallsBackToEmptyWhenThereIsNothingToDraw() {
    when(textRenderer.wrapLines(any(Text.class), anyInt())).thenReturn(List.of());

    assertEquals(OrderedText.EMPTY, ConfigRowText.trimmed(textRenderer, TEXT, 120));
  }

  @Test
  void keepsEveryLineWhenTheTextFitsInsideTheLinesItIsAllowed() {
    final List<OrderedText> lines =
        List.of(OrderedText.EMPTY, OrderedText.styledForwardsVisitedString("second", null));
    when(textRenderer.wrapLines(any(Text.class), anyInt())).thenReturn(lines);

    assertEquals(lines, ConfigRowText.wrapped(textRenderer, TEXT, 234, 2));
  }

  @Test
  void dropsTheLinesThatWouldOverflowTheRow() {
    final OrderedText first = OrderedText.styledForwardsVisitedString("first", null);
    final OrderedText second = OrderedText.styledForwardsVisitedString("second", null);
    final OrderedText third = OrderedText.styledForwardsVisitedString("third", null);
    when(textRenderer.wrapLines(any(Text.class), anyInt()))
        .thenReturn(List.of(first, second, third));

    assertEquals(List.of(first, second), ConfigRowText.wrapped(textRenderer, TEXT, 234, 2));
  }

  @Test
  void wrapsToTheWidthItWasGivenWhenThatWidthIsUsable() {
    when(textRenderer.wrapLines(any(Text.class), anyInt())).thenReturn(List.of());

    ConfigRowText.trimmed(textRenderer, TEXT, 234);

    verify(textRenderer).wrapLines(eq(TEXT), eq(234));
  }

  @ParameterizedTest(name = "{0} wrapped lines, centred = {1}")
  @CsvSource({"1, false", "2, false", "3, false", "1, true", "2, true", "3, true"})
  void drawsEachKeptDescriptionLineOnItsOwnRow(final int wrappedLines, final boolean centred) {
    final List<OrderedText> lines =
        java.util.stream.IntStream.range(0, wrappedLines)
            .mapToObj(i -> OrderedText.styledForwardsVisitedString("line " + i, null))
            .toList();
    when(textRenderer.wrapLines(any(Text.class), anyInt())).thenReturn(lines);
    final DrawContext context = mock(DrawContext.class);
    final OptionRowLayout layout = new OptionRowLayout(10, 100, 200, 5, 20, 0, 0);

    if (centred) {
      ConfigRowText.drawDescriptionCentred(context, textRenderer, TEXT, layout, 170);
    } else {
      ConfigRowText.drawDescriptionLeft(context, textRenderer, TEXT, layout, 10);
    }

    final int kept = Math.min(wrappedLines, OptionRowLayout.MAX_DESCRIPTION_LINES);
    for (int line = 0; line < kept; line++) {
      final int y = layout.descriptionLineY(line);
      if (centred) {
        verify(context)
            .drawCenteredTextWithShadow(
                textRenderer, lines.get(line), 170, y, ConfigRowText.DESCRIPTION_COLOUR);
      } else {
        verify(context)
            .drawText(textRenderer, lines.get(line), 10, y, ConfigRowText.DESCRIPTION_COLOUR, true);
      }
    }
    verify(context, times(centred ? 0 : kept))
        .drawText(
            any(TextRenderer.class),
            any(OrderedText.class),
            anyInt(),
            anyInt(),
            anyInt(),
            eq(true));
  }
}
