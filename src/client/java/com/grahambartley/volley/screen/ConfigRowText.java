package com.grahambartley.volley.screen;

import java.util.List;
import java.util.function.ObjIntConsumer;
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

  static void drawDescriptionLeft(
      final DrawContext context,
      final TextRenderer textRenderer,
      final Text description,
      final OptionRowLayout layout,
      final int x) {
    eachDescriptionLine(
        textRenderer,
        description,
        layout,
        (line, y) -> context.drawText(textRenderer, line, x, y, DESCRIPTION_COLOUR, true));
  }

  static void drawDescriptionCentred(
      final DrawContext context,
      final TextRenderer textRenderer,
      final Text description,
      final OptionRowLayout layout,
      final int centreX) {
    eachDescriptionLine(
        textRenderer,
        description,
        layout,
        (line, y) ->
            context.drawCenteredTextWithShadow(textRenderer, line, centreX, y, DESCRIPTION_COLOUR));
  }

  private static void eachDescriptionLine(
      final TextRenderer textRenderer,
      final Text description,
      final OptionRowLayout layout,
      final ObjIntConsumer<OrderedText> draw) {
    final List<OrderedText> lines =
        wrapped(
            textRenderer,
            description,
            layout.descriptionWidth(),
            OptionRowLayout.MAX_DESCRIPTION_LINES);
    for (int line = 0; line < lines.size(); line++) {
      draw.accept(lines.get(line), layout.descriptionLineY(line));
    }
  }
}
