package com.grahambartley.notenougharrows.disguise;

import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public final class DisguiseForms {
  public static final List<Identifier> FORMS =
      List.of(
          Identifier.ofVanilla("sheep"),
          Identifier.ofVanilla("pig"),
          Identifier.ofVanilla("chicken"),
          Identifier.ofVanilla("rabbit"),
          Identifier.ofVanilla("cow"));

  private DisguiseForms() {}

  public static Identifier pick(final Random random) {
    return FORMS.get(random.nextInt(FORMS.size()));
  }

  public static boolean isForm(final Identifier id) {
    return id != null && FORMS.contains(id);
  }
}
