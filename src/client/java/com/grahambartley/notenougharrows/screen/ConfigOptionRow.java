package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.option.ConfigOption;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

final class ConfigOptionRow extends ConfigOptionListWidget.OptionEntry {
  private final TextRenderer textRenderer;
  private final Text label;
  private final Text description;
  @Nullable private final ClickableWidget control;

  ConfigOptionRow(
      final TextRenderer textRenderer,
      final ConfigOption<?> option,
      @Nullable final ClickableWidget control) {
    this.textRenderer = textRenderer;
    this.label = OptionLabels.option(option);
    this.description = OptionLabels.optionDescription(option);
    this.control = control;
  }

  @Override
  public void render(
      final DrawContext context,
      final int index,
      final int y,
      final int x,
      final int entryWidth,
      final int entryHeight,
      final int mouseX,
      final int mouseY,
      final boolean hovered,
      final float tickDelta) {
    final OptionRowLayout layout =
        OptionRowLayout.of(x, y, entryWidth, control == null ? 0 : control.getWidth());
    context.drawText(
        textRenderer,
        ConfigRowText.trimmed(textRenderer, label, layout.labelWidth()),
        layout.textX(),
        layout.labelY(),
        ConfigRowText.LABEL_COLOUR,
        true);
    final List<OrderedText> lines =
        ConfigRowText.wrapped(
            textRenderer,
            description,
            layout.descriptionWidth(),
            OptionRowLayout.MAX_DESCRIPTION_LINES);
    for (int line = 0; line < lines.size(); line++) {
      context.drawText(
          textRenderer,
          lines.get(line),
          layout.textX(),
          layout.descriptionLineY(line),
          ConfigRowText.DESCRIPTION_COLOUR,
          true);
    }
    if (control != null) {
      control.setPosition(layout.controlX(), layout.controlY());
      control.render(context, mouseX, mouseY, tickDelta);
    }
  }

  @Override
  public List<? extends Element> children() {
    return control == null ? List.of() : List.of(control);
  }

  @Override
  public List<? extends Selectable> selectableChildren() {
    return control == null ? List.of() : List.of(control);
  }
}
