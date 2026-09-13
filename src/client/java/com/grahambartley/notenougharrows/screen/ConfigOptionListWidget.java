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

public final class ConfigOptionListWidget
    extends ElementListWidget<ConfigOptionListWidget.OptionEntry> {
  public static final int ROW_HEIGHT = 34;
  public static final int WIDGET_HEIGHT = 20;
  public static final int CONTROL_WIDTH = 100;
  public static final int ROW_WIDTH = 340;
  public static final int EDGE_MARGIN = 20;

  public ConfigOptionListWidget(
      final MinecraftClient client, final int width, final int height, final int y) {
    super(client, width, height, y, ROW_HEIGHT);
  }

  @Override
  public int getRowWidth() {
    return Math.min(ROW_WIDTH, getWidth() - EDGE_MARGIN);
  }

  public <S> void addSection(
      final ConfigSection<S> section, final ConfigDraft<S> draft, final boolean editable) {
    addEntry(new ConfigSectionRow(client.textRenderer, section));
    for (final ConfigOption<S> option : section.options()) {
      addOption(option, draft, editable);
    }
  }

  private <S> void addOption(
      final ConfigOption<S> option, final ConfigDraft<S> draft, final boolean editable) {
    final boolean inline = OptionWidgets.fitsBesideItsLabel(option);
    final ClickableWidget widget =
        OptionWidgets.create(
            option,
            draft,
            inline ? CONTROL_WIDTH : getRowWidth(),
            WIDGET_HEIGHT,
            client.textRenderer,
            editable);
    addEntry(new ConfigOptionRow(client.textRenderer, option, inline ? widget : null));
    if (!inline) {
      addEntry(new WidgetEntry(widget));
    }
  }

  public abstract static class OptionEntry extends ElementListWidget.Entry<OptionEntry> {}

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
