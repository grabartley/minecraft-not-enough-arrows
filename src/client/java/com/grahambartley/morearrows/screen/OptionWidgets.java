package com.grahambartley.morearrows.screen;

import com.grahambartley.morearrows.config.ConfigValueFormat;
import com.grahambartley.morearrows.config.option.BooleanOption;
import com.grahambartley.morearrows.config.option.ConfigOption;
import com.grahambartley.morearrows.config.option.FloatOption;
import com.grahambartley.morearrows.config.option.IdentifierListOption;
import com.grahambartley.morearrows.config.option.IntOption;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class OptionWidgets {
  public static final int MAX_IDENTIFIER_TEXT_LENGTH = 2048;

  private OptionWidgets() {}

  public static boolean rendersOwnLabel(final ConfigOption<?> option) {
    return !(option instanceof IdentifierListOption<?>);
  }

  public static <S> ClickableWidget create(
      final ConfigOption<S> option,
      final ConfigDraft<S> draft,
      final int width,
      final int height,
      final TextRenderer textRenderer,
      final boolean editable) {
    final ClickableWidget widget =
        switch (option) {
          case BooleanOption<S> booleanOption -> toggle(booleanOption, draft, width, height);
          case IntOption<S> intOption -> intSlider(intOption, draft, width, height);
          case FloatOption<S> floatOption -> floatSlider(floatOption, draft, width, height);
          case IdentifierListOption<S> listOption ->
              identifierField(listOption, draft, width, height, textRenderer);
        };
    if (widget instanceof TextFieldWidget field) {
      field.setEditable(editable);
    }
    widget.active = editable;
    return widget;
  }

  private static <S> ClickableWidget toggle(
      final BooleanOption<S> option,
      final ConfigDraft<S> draft,
      final int width,
      final int height) {
    return CyclingButtonWidget.onOffBuilder(option.read(draft.current()))
        .build(
            0,
            0,
            width,
            height,
            OptionLabels.option(option),
            (button, value) -> draft.apply(current -> option.write(current, value)));
  }

  private static <S> ClickableWidget intSlider(
      final IntOption<S> option, final ConfigDraft<S> draft, final int width, final int height) {
    return new ConfigSliderWidget(
        width,
        height,
        OptionLabels.option(option),
        OptionScale.of(option),
        option.read(draft.current()),
        value -> ConfigValueFormat.of((int) Math.round(value)),
        value -> draft.apply(current -> option.write(current, (int) Math.round(value))));
  }

  private static <S> ClickableWidget floatSlider(
      final FloatOption<S> option, final ConfigDraft<S> draft, final int width, final int height) {
    return new ConfigSliderWidget(
        width,
        height,
        OptionLabels.option(option),
        OptionScale.of(option),
        option.read(draft.current()),
        value -> ConfigValueFormat.of((float) value),
        value -> draft.apply(current -> option.write(current, (float) value)));
  }

  private static <S> ClickableWidget identifierField(
      final IdentifierListOption<S> option,
      final ConfigDraft<S> draft,
      final int width,
      final int height,
      final TextRenderer textRenderer) {
    final TextFieldWidget field =
        new TextFieldWidget(textRenderer, 0, 0, width, height, OptionLabels.option(option));
    field.setMaxLength(MAX_IDENTIFIER_TEXT_LENGTH);
    field.setPlaceholder(Text.translatable(OptionLabels.IDENTIFIER_LIST_HINT_KEY));
    field.setText(IdentifierListText.join(option.read(draft.current())));
    field.setChangedListener(
        text -> draft.apply(current -> option.write(current, IdentifierListText.split(text))));
    return field;
  }
}
