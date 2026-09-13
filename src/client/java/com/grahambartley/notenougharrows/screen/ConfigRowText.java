package com.grahambartley.notenougharrows.screen;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public final class ConfigRowText {
  public static final int MIN_WIDTH = 1;

  private ConfigRowText() {}

  public static OrderedText trimmed(
      final TextRenderer textRenderer, final Text text, final int width) {
    final List<OrderedText> lines = textRenderer.wrapLines(text, Math.max(MIN_WIDTH, width));
    return lines.isEmpty() ? OrderedText.EMPTY : lines.get(0);
  }
}
