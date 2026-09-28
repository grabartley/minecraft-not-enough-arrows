package com.grahambartley.notenougharrows.arrow;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.Identifier;

public final class ArrowSoundAudit {

  private ArrowSoundAudit() {}

  public static List<Identifier> unregistered(
      final Map<Identifier, ArrowSound> declared, final Collection<Identifier> registered) {
    Objects.requireNonNull(declared, "declared");
    Objects.requireNonNull(registered, "registered");

    final Set<Identifier> known = Set.copyOf(registered);
    return declared.entrySet().stream()
        .filter(entry -> !known.contains(entry.getValue().sound()))
        .map(Map.Entry::getKey)
        .toList();
  }

  public static List<Identifier> sharedWithoutASystem(final Map<Identifier, ArrowSound> declared) {
    Objects.requireNonNull(declared, "declared");

    final Map<Identifier, ArrowSound> firstBySound = new LinkedHashMap<>();
    final Set<Identifier> clashes = new LinkedHashSet<>();
    declared.forEach(
        (arrow, sound) -> {
          final ArrowSound first = firstBySound.putIfAbsent(sound.sound(), sound);
          if (first != null && !sameSystem(first, sound)) {
            clashes.add(sound.sound());
          }
        });
    return List.copyOf(clashes);
  }

  private static boolean sameSystem(final ArrowSound first, final ArrowSound second) {
    final Optional<String> system = first.sharedSystem();
    return system.isPresent() && system.equals(second.sharedSystem());
  }
}
