package com.grahambartley.notenougharrows.arrow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.Identifier;

public final class ArrowTextAudit {

  public record ArrowText(
      String nameKey, Optional<String> name, String descriptionKey, Optional<String> description) {

    public ArrowText {
      Objects.requireNonNull(nameKey, "nameKey");
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(descriptionKey, "descriptionKey");
      Objects.requireNonNull(description, "description");
    }
  }

  private ArrowTextAudit() {}

  public static List<String> violations(final Map<Identifier, ArrowText> texts) {
    Objects.requireNonNull(texts, "texts");

    final List<String> violations = new ArrayList<>();
    texts.forEach(
        (arrow, text) -> {
          if (!isProse(text.name(), text.nameKey())) {
            violations.add(arrow + " has no name under " + text.nameKey());
          }
          if (!isProse(text.description(), text.descriptionKey())) {
            violations.add(arrow + " has no description under " + text.descriptionKey());
          }
        });
    violations.addAll(duplicates(texts, ArrowText::name, "name"));
    violations.addAll(duplicates(texts, ArrowText::description, "description"));
    return List.copyOf(violations);
  }

  private static boolean isProse(final Optional<String> text, final String key) {
    return text.map(String::strip)
        .filter(value -> !value.isEmpty() && !value.equals(key))
        .isPresent();
  }

  private static List<String> duplicates(
      final Map<Identifier, ArrowText> texts,
      final Function<ArrowText, Optional<String>> field,
      final String label) {
    final Map<String, Identifier> firstByText = new LinkedHashMap<>();
    final List<String> violations = new ArrayList<>();
    texts.forEach(
        (arrow, text) ->
            field
                .apply(text)
                .map(String::strip)
                .filter(value -> !value.isEmpty())
                .ifPresent(
                    value -> {
                      final Identifier first = firstByText.putIfAbsent(value, arrow);
                      if (first != null) {
                        violations.add(arrow + " has the same " + label + " as " + first);
                      }
                    }));
    return violations;
  }
}
