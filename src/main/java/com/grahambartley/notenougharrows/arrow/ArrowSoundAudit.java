package com.grahambartley.notenougharrows.arrow;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class ArrowSoundAudit {

  private ArrowSoundAudit() {}

  public static List<Identifier> unregistered(
      final Map<Identifier, List<ArrowSound>> declared, final Collection<Identifier> registered) {
    Objects.requireNonNull(declared, "declared");
    Objects.requireNonNull(registered, "registered");

    final Set<Identifier> known = Set.copyOf(registered);
    return declared.entrySet().stream()
        .filter(
            entry -> entry.getValue().stream().anyMatch(sound -> !known.contains(sound.sound())))
        .map(Map.Entry::getKey)
        .toList();
  }

  public static List<Identifier> sharedWithoutASystem(
      final Map<Identifier, List<ArrowSound>> declared) {
    Objects.requireNonNull(declared, "declared");

    final Map<Identifier, Identifier> firstArrowBySound = new LinkedHashMap<>();
    final Map<Identifier, ArrowSound> firstBySound = new LinkedHashMap<>();
    final Set<Identifier> clashes = new LinkedHashSet<>();
    declared.forEach(
        (arrow, sounds) -> {
          for (final ArrowSound sound : sounds) {
            final Identifier firstArrow = firstArrowBySound.putIfAbsent(sound.sound(), arrow);
            final ArrowSound first = firstBySound.putIfAbsent(sound.sound(), sound);
            if (firstArrow != null && !firstArrow.equals(arrow) && !sameSystem(first, sound)) {
              clashes.add(sound.sound());
            }
          }
        });
    return List.copyOf(clashes);
  }

  private static boolean sameSystem(final ArrowSound first, final ArrowSound second) {
    return first.sharedSystem().isPresent() && first.sharedSystem().equals(second.sharedSystem());
  }
}
