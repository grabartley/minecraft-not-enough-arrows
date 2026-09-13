package com.grahambartley.notenougharrows.screen;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public final class ConfigRowText {
  public static final int LABEL_COLOUR = 0xFFFFFFFF;
  public static final int DESCRIPTION_COLOUR = 0xFFA0A0A0;

  private ConfigRowText() {}

  public static OrderedText trimmed(
      final TextRenderer textRenderer, final Text text, final int width) {
    final List<OrderedText> lines = textRenderer.wrapLines(text, width);
    return lines.isEmpty() ? OrderedText.EMPTY : lines.get(0);
  }
}
