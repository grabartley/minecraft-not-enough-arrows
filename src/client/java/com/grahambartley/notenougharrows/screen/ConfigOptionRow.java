package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.option.ConfigOption;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class ConfigOptionRow extends ConfigOptionListWidget.OptionEntry {
  public static final int LABEL_COLOUR = 0xFFFFFFFF;
  public static final int DESCRIPTION_COLOUR = 0xFFA0A0A0;

  private final TextRenderer textRenderer;
  private final Text label;
  private final Text description;
  @Nullable private final ClickableWidget control;

  public ConfigOptionRow(
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
        LABEL_COLOUR,
        true);
    context.drawText(
        textRenderer,
        ConfigRowText.trimmed(textRenderer, description, layout.descriptionWidth()),
        layout.textX(),
        layout.descriptionY(),
        DESCRIPTION_COLOUR,
        true);
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
