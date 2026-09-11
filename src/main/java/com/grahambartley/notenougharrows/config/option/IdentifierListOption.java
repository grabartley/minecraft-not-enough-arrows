package com.grahambartley.notenougharrows.config.option;

import com.grahambartley.notenougharrows.config.ConfigValueFormat;
import com.grahambartley.notenougharrows.config.ConfigValues;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public record IdentifierListOption<S>(
    String id,
    int maxEntries,
    Function<S, List<String>> reader,
    BiFunction<S, List<String>, S> writer)
    implements ConfigOption<S> {

  public IdentifierListOption {
    OptionIds.require(id);
    if (maxEntries < 0) {
      throw new IllegalArgumentException("Option " + id + " has negative maxEntries " + maxEntries);
    }
  }

  public List<String> read(final S subject) {
    return reader.apply(subject);
  }

  public S write(final S subject, final List<String> value) {
    return writer.apply(subject, ConfigValues.normalizeIdentifiers(value, maxEntries));
  }

  @Override
  public String displayValue(final S subject) {
    return ConfigValueFormat.of(read(subject));
  }
}
