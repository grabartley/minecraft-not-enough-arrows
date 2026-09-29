package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.arrow.ArrowTextAudit.ArrowText;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ArrowTextAuditTest {

  private static Identifier arrow(final String path) {
    return Identifier.of(NotEnoughArrows.MOD_ID, path);
  }

  private static ArrowText text(final String path, final String name, final String description) {
    return new ArrowText(
        "item.not-enough-arrows." + path,
        Optional.ofNullable(name),
        "info.not-enough-arrows." + path,
        Optional.ofNullable(description));
  }

  private static Map<Identifier, ArrowText> texts(final ArrowText... texts) {
    final Map<Identifier, ArrowText> byArrow = new LinkedHashMap<>();
    for (final ArrowText text : texts) {
      byArrow.put(arrow(text.nameKey().substring("item.not-enough-arrows.".length())), text);
    }
    return byArrow;
  }

  @Test
  void passesArrowsWithTheirOwnNameAndDescription() {
    assertTrue(
        ArrowTextAudit.violations(
                texts(
                    text("drill_arrow", "Drill Arrow", "Breaks the one block it strikes."),
                    text(
                        "drain_arrow", "Drain Arrow", "Soaks up the water around where it lands.")))
            .isEmpty());
  }

  @Test
  void failsAMissingName() {
    assertEquals(
        List.of(arrow("drill_arrow") + " has no name under item.not-enough-arrows.drill_arrow"),
        ArrowTextAudit.violations(texts(text("drill_arrow", null, "Breaks a block."))));
  }

  @Test
  void failsANameThatIsOnlyItsOwnKey() {
    assertEquals(
        List.of(arrow("drill_arrow") + " has no name under item.not-enough-arrows.drill_arrow"),
        ArrowTextAudit.violations(
            texts(text("drill_arrow", "item.not-enough-arrows.drill_arrow", "Breaks a block."))));
  }

  @Test
  void failsABlankDescription() {
    assertEquals(
        List.of(
            arrow("drill_arrow") + " has no description under info.not-enough-arrows.drill_arrow"),
        ArrowTextAudit.violations(texts(text("drill_arrow", "Drill Arrow", "   "))));
  }

  @Test
  void failsAMissingDescription() {
    assertEquals(
        List.of(
            arrow("drill_arrow") + " has no description under info.not-enough-arrows.drill_arrow"),
        ArrowTextAudit.violations(texts(text("drill_arrow", "Drill Arrow", null))));
  }

  @Test
  void failsTwoArrowsWithTheSameName() {
    assertEquals(
        List.of(arrow("pillar_arrow") + " has the same name as " + arrow("drill_arrow")),
        ArrowTextAudit.violations(
            texts(
                text("drill_arrow", "Drill Arrow", "Breaks a block."),
                text("pillar_arrow", "Drill Arrow", "Raises a pillar."))));
  }

  @Test
  void failsTwoArrowsWithTheSameDescription() {
    assertEquals(
        List.of(arrow("pillar_arrow") + " has the same description as " + arrow("drill_arrow")),
        ArrowTextAudit.violations(
            texts(
                text("drill_arrow", "Drill Arrow", "Breaks a block."),
                text("pillar_arrow", "Pillar Arrow", "Breaks a block."))));
  }

  @Test
  void ignoresSurroundingWhitespaceWhenComparing() {
    assertEquals(
        List.of(arrow("pillar_arrow") + " has the same description as " + arrow("drill_arrow")),
        ArrowTextAudit.violations(
            texts(
                text("drill_arrow", "Drill Arrow", "Breaks a block."),
                text("pillar_arrow", "Pillar Arrow", " Breaks a block. "))));
  }

  @Test
  void twoMissingDescriptionsAreNotReportedAsDuplicates() {
    assertEquals(
        2,
        ArrowTextAudit.violations(
                texts(text("a_arrow", "A Arrow", null), text("b_arrow", "B Arrow", null)))
            .size());
  }

  @Test
  void rejectsNullInputs() {
    assertThrows(NullPointerException.class, () -> ArrowTextAudit.violations(null));
    assertThrows(
        NullPointerException.class,
        () -> new ArrowText(null, Optional.empty(), "info.x", Optional.empty()));
  }
}
