package com.grahambartley.notenougharrows.disguise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.junit.jupiter.api.Test;

class DisguiseFormsTest {

  @Test
  void theFormsAreHarmlessFarmAnimals() {
    assertEquals(
        List.of(
            Identifier.ofVanilla("sheep"),
            Identifier.ofVanilla("pig"),
            Identifier.ofVanilla("chicken"),
            Identifier.ofVanilla("rabbit"),
            Identifier.ofVanilla("cow")),
        DisguiseForms.FORMS);
  }

  @Test
  void everyPickIsOneOfTheForms() {
    final Random random = Random.create(42L);
    final Set<Identifier> seen = new HashSet<>();
    for (int i = 0; i < 200; i++) {
      final Identifier picked = DisguiseForms.pick(random);
      assertTrue(DisguiseForms.isForm(picked));
      seen.add(picked);
    }
    assertEquals(Set.copyOf(DisguiseForms.FORMS), seen);
  }

  @Test
  void anythingElseIsNotAForm() {
    assertFalse(DisguiseForms.isForm(Identifier.ofVanilla("zombie")));
    assertFalse(DisguiseForms.isForm(Identifier.ofVanilla("warden")));
    assertFalse(DisguiseForms.isForm(null));
  }
}
