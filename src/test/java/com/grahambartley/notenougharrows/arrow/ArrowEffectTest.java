package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ArrowEffectTest {

  @Test
  void carriesTheEffectAndHowItIsDelivered() {
    final ArrowEffect effect =
        new ArrowEffect(Identifier.ofVanilla("glowing"), ArrowEffect.Delivery.AREA);

    assertEquals(Identifier.ofVanilla("glowing"), effect.effect());
    assertEquals(ArrowEffect.Delivery.AREA, effect.delivery());
  }

  @Test
  void oneEffectDeliveredTwoWaysIsTwoDifferentEffects() {
    assertNotEquals(
        new ArrowEffect(Identifier.ofVanilla("glowing"), ArrowEffect.Delivery.AREA),
        new ArrowEffect(Identifier.ofVanilla("glowing"), ArrowEffect.Delivery.STRUCK_TARGET));
  }

  @Test
  void rejectsANullEffect() {
    assertThrows(
        NullPointerException.class,
        () -> new ArrowEffect(null, ArrowEffect.Delivery.STRUCK_TARGET));
  }

  @Test
  void rejectsANullDelivery() {
    assertThrows(
        NullPointerException.class, () -> new ArrowEffect(Identifier.ofVanilla("glowing"), null));
  }

  @Test
  void rejectsANullRegistryEntry() {
    assertThrows(NullPointerException.class, () -> ArrowEffect.onStruckTarget(null));
    assertThrows(NullPointerException.class, () -> ArrowEffect.overArea(null));
  }
}
