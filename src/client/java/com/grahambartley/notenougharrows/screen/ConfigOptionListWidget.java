package com.grahambartley.notenougharrows.screen;

import com.grahambartley.notenougharrows.config.option.ConfigOption;
import com.grahambartley.notenougharrows.config.option.ConfigSection;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

public final class ConfigOptionListWidget
    extends ElementListWidget<ConfigOptionListWidget.OptionEntry> {
  public static final int ROW_HEIGHT = 24;
  public static final int WIDGET_HEIGHT = 20;
  public static final int ROW_WIDTH = 310;

  public ConfigOptionListWidget(
      final MinecraftClient client, final int width, final int height, final int y) {
    super(client, width, height, y, ROW_HEIGHT);
  }

  @Override
  public int getRowWidth() {
    return ROW_WIDTH;
  }

  public <S> void addSection(
      final ConfigSection<S> section, final ConfigDraft<S> draft, final boolean editable) {
    addEntry(new HeadingEntry(OptionLabels.section(section)));
    for (final ConfigOption<S> option : section.options()) {
      if (!OptionWidgets.rendersOwnLabel(option)) {
        addEntry(new HeadingEntry(OptionLabels.option(option)));
      }
      addEntry(
          new WidgetEntry(
              OptionWidgets.create(
                  option, draft, getRowWidth(), WIDGET_HEIGHT, client.textRenderer, editable)));
    }
  }

  public void addHeading(final Text text) {
    addEntry(new HeadingEntry(text));
  }

  public abstract static class OptionEntry extends ElementListWidget.Entry<OptionEntry> {}

  private final class HeadingEntry extends OptionEntry {
    private final Text text;

    private HeadingEntry(final Text text) {
      this.text = text;
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
      context.drawCenteredTextWithShadow(
          client.textRenderer, text, x + entryWidth / 2, y + (entryHeight - 8) / 2, 0xFFFFFF);
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

  private static final class WidgetEntry extends OptionEntry {
    private final ClickableWidget widget;

    private WidgetEntry(final ClickableWidget widget) {
      this.widget = widget;
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
      widget.setX(x);
      widget.setY(y + (entryHeight - widget.getHeight()) / 2);
      widget.render(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public List<? extends Element> children() {
      return List.of(widget);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
      return List.of(widget);
    }
  }
}
