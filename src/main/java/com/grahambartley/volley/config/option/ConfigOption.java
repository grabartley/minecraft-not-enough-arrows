package com.grahambartley.volley.config.option;

public sealed interface ConfigOption<S>
    permits BooleanOption, FloatOption, IdentifierListOption, IntOption {

  String id();

  String displayValue(S subject);
}
