package com.grahambartley.notenougharrows.config.option;

public sealed interface ConfigOption<S>
    permits BooleanOption, FloatOption, IdentifierListOption, IntOption {

  String id();

  String displayValue(S subject);
}
