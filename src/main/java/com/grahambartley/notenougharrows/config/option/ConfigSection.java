package com.grahambartley.notenougharrows.config.option;

import java.util.List;

public record ConfigSection<S>(String id, List<ConfigOption<S>> options) {

  public ConfigSection {
    OptionIds.require(id);
    options = List.copyOf(options);
  }
}
