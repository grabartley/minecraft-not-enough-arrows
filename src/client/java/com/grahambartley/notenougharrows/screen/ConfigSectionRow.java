package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.option.ConfigSection;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.text.Text;

public final class ConfigSectionRow extends ConfigOptionListWidget.OptionEntry {
  private final TextRenderer textRenderer;
  private final Text heading;
  private final Text description;

  public ConfigSectionRow(final TextRenderer textRenderer, final ConfigSection<?> section) {
    this.textRenderer = textRenderer;
    this.heading = OptionLabels.section(section);
    this.description = OptionLabels.sectionDescription(section);
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
    final OptionRowLayout layout = OptionRowLayout.of(x, y, entryWidth, 0);
    final int centreX = x + entryWidth / 2;
    context.drawCenteredTextWithShadow(
        textRenderer,
        ConfigRowText.trimmed(textRenderer, heading, layout.labelWidth()),
        centreX,
        layout.labelY(),
        ConfigOptionRow.LABEL_COLOUR);
    context.drawCenteredTextWithShadow(
        textRenderer,
        ConfigRowText.trimmed(textRenderer, description, layout.descriptionWidth()),
        centreX,
        layout.descriptionY(),
        ConfigOptionRow.DESCRIPTION_COLOUR);
  }

  @Override
  public List<? extends Element> children() {
    return List.of();
  }

  @Override
  public List<? extends Selectable> selectableChildren() {
    return List.of();
  }
}
