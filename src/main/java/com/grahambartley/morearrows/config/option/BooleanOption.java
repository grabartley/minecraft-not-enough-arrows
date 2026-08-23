package com.grahambartley.morearrows.config.option;

import com.grahambartley.morearrows.config.ConfigValueFormat;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public record BooleanOption<S>(String id, Predicate<S> reader, BiFunction<S, Boolean, S> writer)
    implements ConfigOption<S> {

  public BooleanOption {
    OptionIds.require(id);
  }

  public boolean read(final S subject) {
    return reader.test(subject);
  }

  public S write(final S subject, final boolean value) {
    return writer.apply(subject, value);
  }

  @Override
  public String displayValue(final S subject) {
    return ConfigValueFormat.of(read(subject));
  }
}
