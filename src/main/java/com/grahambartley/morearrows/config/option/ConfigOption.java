package com.grahambartley.morearrows.config.option;

public sealed interface ConfigOption<S>
    permits BooleanOption, FloatOption, IdentifierListOption, IntOption {

  String id();

  String displayValue(S subject);
}
