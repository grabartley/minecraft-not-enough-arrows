package com.grahambartley.volley.config.option;

import java.util.List;

public record ConfigSection<S>(String id, List<ConfigOption<S>> options) {

  public ConfigSection {
    OptionIds.require(id);
    options = List.copyOf(options);
  }
}
