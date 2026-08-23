package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.ConfigValueFormat;
import com.grahambartley.morearrows.config.ConfigValues;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;

public record IntOption<S>(
    String id, int min, int max, ToIntFunction<S> reader, BiFunction<S, Integer, S> writer)
    implements ConfigOption<S> {

  public IntOption {
    OptionIds.require(id);
    if (min > max) {
      throw new IllegalArgumentException("Option " + id + " has min " + min + " above max " + max);
    }
  }

  public int read(final S subject) {
    return reader.applyAsInt(subject);
  }

  public S write(final S subject, final int value) {
    return writer.apply(subject, ConfigValues.clampInt(value, min, max));
  }

  @Override
  public String displayValue(final S subject) {
    return ConfigValueFormat.of(read(subject));
  }
}
