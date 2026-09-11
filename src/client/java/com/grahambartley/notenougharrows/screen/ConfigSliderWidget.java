package com.grahambartley.notenougharrows.screen;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public final class ConfigSliderWidget extends SliderWidget {
  private final OptionScale scale;
  private final Text label;
  private final DoubleFunction<String> formatter;
  private final DoubleConsumer onChange;

  public ConfigSliderWidget(
      final int width,
      final int height,
      final Text label,
      final OptionScale scale,
      final double initialValue,
      final DoubleFunction<String> formatter,
      final DoubleConsumer onChange) {
    super(0, 0, width, height, Text.empty(), scale.toProgress(initialValue));
    this.scale = scale;
    this.label = label;
    this.formatter = formatter;
    this.onChange = onChange;
    updateMessage();
  }

  public double currentValue() {
    return scale.toValue(value);
  }

  @Override
  protected void updateMessage() {
    setMessage(Text.translatable(OptionLabels.ROW_KEY, label, formatter.apply(currentValue())));
  }

  @Override
  protected void applyValue() {
    onChange.accept(currentValue());
  }
}
