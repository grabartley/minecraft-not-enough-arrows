package com.grahambartley.volley.screen;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

final class ConfigRowText {
  static final int LABEL_COLOUR = 0xFFFFFFFF;
  static final int DESCRIPTION_COLOUR = 0xFFA0A0A0;

  private ConfigRowText() {}

  static OrderedText trimmed(final TextRenderer textRenderer, final Text text, final int width) {
    final List<OrderedText> lines = textRenderer.wrapLines(text, width);
    return lines.isEmpty() ? OrderedText.EMPTY : lines.get(0);
  }

  static List<OrderedText> wrapped(
      final TextRenderer textRenderer, final Text text, final int width, final int maxLines) {
    final List<OrderedText> lines = textRenderer.wrapLines(text, width);
    return lines.size() <= maxLines ? lines : List.copyOf(lines.subList(0, maxLines));
  }

  static void drawDescription(
      final DrawContext context,
      final TextRenderer textRenderer,
      final Text description,
      final OptionRowLayout layout,
      final int x,
      final boolean centred) {
    final List<OrderedText> lines =
        wrapped(
            textRenderer,
            description,
            layout.descriptionWidth(),
            OptionRowLayout.MAX_DESCRIPTION_LINES);
    for (int line = 0; line < lines.size(); line++) {
      final int y = layout.descriptionLineY(line);
      if (centred) {
        context.drawCenteredTextWithShadow(textRenderer, lines.get(line), x, y, DESCRIPTION_COLOUR);
      } else {
        context.drawText(textRenderer, lines.get(line), x, y, DESCRIPTION_COLOUR, true);
      }
    }
  }
}
