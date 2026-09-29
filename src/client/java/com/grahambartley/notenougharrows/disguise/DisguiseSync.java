package com.grahambartley.notenougharrows.disguise;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;

public final class DisguiseSync {
  private static final Map<Integer, Identifier> FORMS = new HashMap<>();

  private DisguiseSync() {}

  public static void accept(final int entityId, final Optional<Identifier> form) {
    if (form.filter(DisguiseForms::isForm).isPresent()) {
      FORMS.put(entityId, form.get());
    } else {
      FORMS.remove(entityId);
    }
  }

  public static Optional<Identifier> formOf(final int entityId) {
    return Optional.ofNullable(FORMS.get(entityId));
  }

  public static void forget(final int entityId) {
    FORMS.remove(entityId);
  }

  public static void clear() {
    FORMS.clear();
  }
}
