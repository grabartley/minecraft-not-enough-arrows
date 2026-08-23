package com.grahambartley.morearrows.config.option;

import java.util.List;

final class OptionMutations {
  static final String SAMPLE_IDENTIFIER = "more-arrows:sample_block";

  private OptionMutations() {}

  static <S> S toDifferentValue(final ConfigOption<S> option, final S subject) {
    return switch (option) {
      case BooleanOption<S> booleanOption ->
          booleanOption.write(subject, !booleanOption.read(subject));
      case IntOption<S> intOption ->
          intOption.write(
              subject,
              intOption.read(subject) == intOption.max() ? intOption.min() : intOption.max());
      case FloatOption<S> floatOption ->
          floatOption.write(
              subject,
              floatOption.read(subject) == floatOption.max()
                  ? floatOption.min()
                  : floatOption.max());
      case IdentifierListOption<S> listOption ->
          listOption.write(
              subject, listOption.read(subject).isEmpty() ? List.of(SAMPLE_IDENTIFIER) : List.of());
    };
  }
}
