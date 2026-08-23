package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.ConfigValueFormat;
import com.grahambartley.morearrows.config.ConfigValues;
import java.util.function.BiFunction;
import java.util.function.Function;

public record FloatOption<S>(
    String id,
    float min,
    float max,
    float step,
    Function<S, Float> reader,
    BiFunction<S, Float, S> writer)
    implements ConfigOption<S> {

  public FloatOption {
    OptionIds.require(id);
    if (min > max) {
      throw new IllegalArgumentException("Option " + id + " has min " + min + " above max " + max);
    }
    if (step <= 0.0f) {
      throw new IllegalArgumentException("Option " + id + " has non-positive step " + step);
    }
  }

  public float read(final S subject) {
    return reader.apply(subject);
  }

  public S write(final S subject, final float value) {
    return writer.apply(subject, ConfigValues.clampFloat(value, min, max));
  }

  @Override
  public String displayValue(final S subject) {
    return ConfigValueFormat.of(read(subject));
  }
}
