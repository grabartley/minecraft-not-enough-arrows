package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ArrowEffectAuditTest {
  private static final List<Identifier> BREWABLE =
      List.of(
          Identifier.ofVanilla("slowness"),
          Identifier.ofVanilla("poison"),
          Identifier.ofVanilla("weakness"));

  private static Identifier arrow(final String path) {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }

  private static ArrowEffect struck(final String effect) {
    return new ArrowEffect(Identifier.ofVanilla(effect), ArrowEffect.Delivery.STRUCK_TARGET);
  }

  private static ArrowEffect area(final String effect) {
    return new ArrowEffect(Identifier.ofVanilla(effect), ArrowEffect.Delivery.AREA);
  }

  private static Map<Identifier, List<ArrowEffect>> declared(final Object... arrowThenEffects) {
    final Map<Identifier, List<ArrowEffect>> declared = new LinkedHashMap<>();
    for (int i = 0; i < arrowThenEffects.length; i += 2) {
      @SuppressWarnings("unchecked")
      final List<ArrowEffect> effects = (List<ArrowEffect>) arrowThenEffects[i + 1];
      declared.put(arrow((String) arrowThenEffects[i]), effects);
    }
    return declared;
  }

  @Test
  void passesArrowsWhoseEffectsNoPotionBrews() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "haste_arrow", List.of(struck("haste")),
            "smoke_arrow", List.of(area("blindness")),
            "tnt_arrow", List.of());

    assertTrue(ArrowEffectAudit.soldAsTippedArrows(declared, BREWABLE).isEmpty());
  }

  @Test
  void failsAnArrowApplyingAnEffectVanillaSellsAsATippedArrow() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "haste_arrow", List.of(struck("haste")),
            "frost_arrow", List.of(struck("slowness")));

    assertEquals(
        List.of(arrow("frost_arrow")), ArrowEffectAudit.soldAsTippedArrows(declared, BREWABLE));
  }

  @Test
  void failsABrewableEffectHiddenAmongOthers() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared("rust_arrow", List.of(struck("mining_fatigue"), area("weakness")));

    assertEquals(
        List.of(arrow("rust_arrow")), ArrowEffectAudit.soldAsTippedArrows(declared, BREWABLE));
  }

  @Test
  void failsABrewableEffectHoweverItIsDelivered() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared("stink_arrow", List.of(area("poison")));

    assertEquals(
        List.of(arrow("stink_arrow")), ArrowEffectAudit.soldAsTippedArrows(declared, BREWABLE));
  }

  @Test
  void passesArrowsWhoseEffectSetsDiffer() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "haste_arrow", List.of(struck("haste")),
            "guard_arrow", List.of(struck("absorption")));

    assertTrue(ArrowEffectAudit.reproducingEachOther(declared).isEmpty());
  }

  @Test
  void passesOneEffectDeliveredTwoDifferentWays() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "glow_ink_arrow", List.of(struck("glowing")),
            "sonar_arrow", List.of(area("glowing")));

    assertTrue(ArrowEffectAudit.reproducingEachOther(declared).isEmpty());
  }

  @Test
  void failsTwoArrowsDeclaringTheSameEffectSet() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "glow_ink_arrow", List.of(struck("glowing")),
            "haste_arrow", List.of(struck("haste")),
            "tracer_arrow", List.of(struck("glowing")));

    assertEquals(
        List.of(arrow("glow_ink_arrow"), arrow("tracer_arrow")),
        ArrowEffectAudit.reproducingEachOther(declared));
  }

  @Test
  void comparesEffectSetsWhateverOrderTheyAreDeclaredIn() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "a_arrow", List.of(struck("haste"), struck("glowing")),
            "b_arrow", List.of(struck("glowing"), struck("haste")));

    assertEquals(
        List.of(arrow("a_arrow"), arrow("b_arrow")),
        ArrowEffectAudit.reproducingEachOther(declared));
  }

  @Test
  void passesASubsetOfAnotherArrowsEffects() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared(
            "a_arrow", List.of(struck("haste"), struck("glowing")),
            "b_arrow", List.of(struck("glowing")));

    assertTrue(ArrowEffectAudit.reproducingEachOther(declared).isEmpty());
  }

  @Test
  void arrowsWithNoEffectDoNotReproduceEachOther() {
    final Map<Identifier, List<ArrowEffect>> declared =
        declared("tnt_arrow", List.of(), "rope_arrow", List.of());

    assertTrue(ArrowEffectAudit.reproducingEachOther(declared).isEmpty());
  }

  @Test
  void rejectsNullInputs() {
    assertThrows(
        NullPointerException.class, () -> ArrowEffectAudit.soldAsTippedArrows(null, BREWABLE));
    assertThrows(
        NullPointerException.class, () -> ArrowEffectAudit.soldAsTippedArrows(Map.of(), null));
    assertThrows(NullPointerException.class, () -> ArrowEffectAudit.reproducingEachOther(null));
  }
}
