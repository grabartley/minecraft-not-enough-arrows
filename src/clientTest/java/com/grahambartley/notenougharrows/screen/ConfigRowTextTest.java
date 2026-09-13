package com.grahambartley.notenougharrows.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.junit.jupiter.api.Test;

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
  void wrapsToTheWidthItWasGivenWhenThatWidthIsUsable() {
    when(textRenderer.wrapLines(any(Text.class), anyInt())).thenReturn(List.of());

    ConfigRowText.trimmed(textRenderer, TEXT, 234);

    verify(textRenderer).wrapLines(eq(TEXT), eq(234));
  }
}
