package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ArrowSoundAuditTest {

  private static Identifier id(final String path) {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }

  private static Map<Identifier, List<ArrowSound>> declared(final Object... arrowThenSound) {
    final Map<Identifier, List<ArrowSound>> declared = new LinkedHashMap<>();
    for (int i = 0; i < arrowThenSound.length; i += 2) {
      final ArrowSound sound = (ArrowSound) arrowThenSound[i + 1];
      declared.computeIfAbsent(id((String) arrowThenSound[i]), key -> new ArrayList<>()).add(sound);
    }
    return declared;
  }

  @Test
  void reportsNothingWhenEveryDeclaredSoundIsRegistered() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "smoke_arrow", ArrowSound.own(id("smoke_arrow_impact")),
            "taunt_arrow", ArrowSound.own(id("taunt_arrow_impact")));

    assertTrue(
        ArrowSoundAudit.unregistered(
                declared, List.of(id("smoke_arrow_impact"), id("taunt_arrow_impact")))
            .isEmpty());
  }

  @Test
  void reportsEveryArrowWhoseSoundWasNeverRegistered() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "smoke_arrow", ArrowSound.own(id("smoke_arrow_impact")),
            "taunt_arrow", ArrowSound.own(id("taunt_arrow_impact")),
            "repel_arrow", ArrowSound.own(id("repel_arrow_impact")));

    assertEquals(
        List.of(id("smoke_arrow"), id("repel_arrow")),
        ArrowSoundAudit.unregistered(declared, Set.of(id("taunt_arrow_impact"))));
  }

  @Test
  void reportsNothingWhenNoArrowDeclaresASound() {
    assertTrue(ArrowSoundAudit.unregistered(Map.of(), List.of(id("countdown_beep"))).isEmpty());
  }

  @Test
  void aSoundRegisteredUnderAnotherNamespaceDoesNotCount() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared("smoke_arrow", ArrowSound.own(id("smoke_arrow_impact")));

    assertEquals(
        List.of(id("smoke_arrow")),
        ArrowSoundAudit.unregistered(
            declared, List.of(Identifier.ofVanilla("smoke_arrow_impact"))));
  }

  @Test
  void reportsNoSharingWhenEveryArrowHasItsOwnSound() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "smoke_arrow", ArrowSound.own(id("smoke_arrow_impact")),
            "taunt_arrow", ArrowSound.own(id("taunt_arrow_impact")));

    assertTrue(ArrowSoundAudit.sharedWithoutASystem(declared).isEmpty());
  }

  @Test
  void allowsArrowsOfOneSystemToShareItsSound() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "gunpowder_arrow", ArrowSound.sharedBy("explosive_fuse", id("countdown_beep")),
            "tnt_arrow", ArrowSound.sharedBy("explosive_fuse", id("countdown_beep")),
            "fire_charge_arrow", ArrowSound.sharedBy("explosive_fuse", id("countdown_beep")));

    assertTrue(ArrowSoundAudit.sharedWithoutASystem(declared).isEmpty());
  }

  @Test
  void reportsTwoUnrelatedArrowsSharingOneSound() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "smoke_arrow", ArrowSound.own(id("puff")),
            "stink_arrow", ArrowSound.own(id("puff")));

    assertEquals(List.of(id("puff")), ArrowSoundAudit.sharedWithoutASystem(declared));
  }

  @Test
  void reportsASystemSoundBorrowedByAnArrowOutsideTheSystem() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "tnt_arrow", ArrowSound.sharedBy("explosive_fuse", id("countdown_beep")),
            "redstone_arrow", ArrowSound.own(id("countdown_beep")));

    assertEquals(List.of(id("countdown_beep")), ArrowSoundAudit.sharedWithoutASystem(declared));
  }

  @Test
  void reportsAnOwnSoundLaterClaimedByASystem() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "redstone_arrow", ArrowSound.own(id("countdown_beep")),
            "tnt_arrow", ArrowSound.sharedBy("explosive_fuse", id("countdown_beep")));

    assertEquals(List.of(id("countdown_beep")), ArrowSoundAudit.sharedWithoutASystem(declared));
  }

  @Test
  void reportsOneSoundSharedByTwoDifferentSystems() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "tnt_arrow", ArrowSound.sharedBy("explosive_fuse", id("whoosh")),
            "recall_arrow", ArrowSound.sharedBy("ender_teleport", id("whoosh")));

    assertEquals(List.of(id("whoosh")), ArrowSoundAudit.sharedWithoutASystem(declared));
  }

  @Test
  void reportsEachSharedSoundOnceInDeclarationOrder() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "a_arrow", ArrowSound.own(id("second")),
            "b_arrow", ArrowSound.own(id("first")),
            "c_arrow", ArrowSound.own(id("second")),
            "d_arrow", ArrowSound.own(id("first")),
            "e_arrow", ArrowSound.own(id("second")));

    assertEquals(
        List.of(id("second"), id("first")), ArrowSoundAudit.sharedWithoutASystem(declared));
  }

  @Test
  void rejectsNullInputs() {
    assertThrows(NullPointerException.class, () -> ArrowSoundAudit.unregistered(null, List.of()));
    assertThrows(NullPointerException.class, () -> ArrowSoundAudit.unregistered(Map.of(), null));
    assertThrows(NullPointerException.class, () -> ArrowSoundAudit.sharedWithoutASystem(null));
  }

  @Test
  void reportsAnArrowWhenAnyOfItsSoundsIsUnregistered() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "frost_arrow", ArrowSound.own(id("freeze_crack")),
            "frost_arrow", ArrowSound.own(id("thaw")));

    assertEquals(
        List.of(id("frost_arrow")),
        ArrowSoundAudit.unregistered(declared, List.of(id("freeze_crack"))));
  }

  @Test
  void anArrowRepeatingItsOwnSoundIsNotSharing() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "frost_arrow", ArrowSound.own(id("crunch")),
            "frost_arrow", ArrowSound.own(id("crunch")));

    assertTrue(ArrowSoundAudit.sharedWithoutASystem(declared).isEmpty());
  }

  @Test
  void checksEverySoundAnArrowDeclaresForSharing() {
    final Map<Identifier, List<ArrowSound>> declared =
        declared(
            "tnt_arrow", ArrowSound.sharedBy("explosive_fuse", id("countdown_beep")),
            "tnt_arrow", ArrowSound.sharedBy("explosive_fuse", id("blast")),
            "wind_arrow", ArrowSound.own(id("blast")));

    assertEquals(List.of(id("blast")), ArrowSoundAudit.sharedWithoutASystem(declared));
  }
}
