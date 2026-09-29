package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.arrow.ArrowRecipeAudit.ArrowRecipe;
import com.grahambartley.notenougharrows.arrow.ArrowRecipeAudit.Collision;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class ArrowRecipeAuditTest {
  private static final Set<Identifier> PLAIN_ARROW = Set.of(Identifier.ofVanilla("arrow"));
  private static final Set<Identifier> TNT_ARROW =
      Set.of(Identifier.of(NotEnoughArrows.MOD_ID, "tnt_arrow"));

  private static ArrowRecipe recipe(
      final String arrow, final Set<Identifier> base, final String... centre) {
    return new ArrowRecipe(
        Identifier.of(NotEnoughArrows.MOD_ID, arrow),
        Identifier.of(NotEnoughArrows.MOD_ID, arrow),
        base,
        Arrays.stream(centre).map(Identifier::ofVanilla).collect(Collectors.toSet()));
  }

  @Test
  void passesRecipesWithTheirOwnCentres() {
    assertTrue(
        ArrowRecipeAudit.sharedCentres(
                List.of(
                    recipe("drill_arrow", PLAIN_ARROW, "iron_pickaxe"),
                    recipe("drain_arrow", PLAIN_ARROW, "sponge")))
            .isEmpty());
  }

  @Test
  void failsTwoArrowsBuiltAroundOneCentreOverOneBase() {
    final ArrowRecipe fireCharge = recipe("fire_charge_arrow", PLAIN_ARROW, "fire_charge");
    final ArrowRecipe copy = recipe("torch_arrow", PLAIN_ARROW, "fire_charge");

    assertEquals(
        List.of(new Collision(fireCharge, copy)),
        ArrowRecipeAudit.sharedCentres(List.of(fireCharge, copy)));
  }

  @Test
  void passesOneCentreOverTwoDifferentBases() {
    assertTrue(
        ArrowRecipeAudit.sharedCentres(
                List.of(
                    recipe("fire_charge_arrow", PLAIN_ARROW, "fire_charge"),
                    recipe("incendiary_arrow", TNT_ARROW, "fire_charge")))
            .isEmpty());
  }

  @Test
  void failsCentresThatOverlapThroughATag() {
    final ArrowRecipe anyWool = recipe("web_arrow", PLAIN_ARROW, "white_wool", "red_wool");
    final ArrowRecipe redWool = recipe("paint_arrow", PLAIN_ARROW, "red_wool");

    assertEquals(
        List.of(new Collision(anyWool, redWool)),
        ArrowRecipeAudit.sharedCentres(List.of(anyWool, redWool)));
  }

  @Test
  void passesTheVariantsOfOneTintedArrow() {
    assertTrue(
        ArrowRecipeAudit.sharedCentres(
                List.of(
                    recipe("paint_arrow", PLAIN_ARROW, "red_dye"),
                    recipe("paint_arrow", PLAIN_ARROW, "blue_dye")))
            .isEmpty());
  }

  @Test
  void reportsEveryCollidingPairOnce() {
    final ArrowRecipe first = recipe("a_arrow", PLAIN_ARROW, "sponge");
    final ArrowRecipe second = recipe("b_arrow", PLAIN_ARROW, "sponge");
    final ArrowRecipe third = recipe("c_arrow", PLAIN_ARROW, "sponge");

    assertEquals(
        List.of(
            new Collision(first, second),
            new Collision(first, third),
            new Collision(second, third)),
        ArrowRecipeAudit.sharedCentres(List.of(first, second, third)));
  }

  @Test
  void namesBothArrowsInTheCollision() {
    final String message =
        new Collision(
                recipe("fire_charge_arrow", PLAIN_ARROW, "fire_charge"),
                recipe("torch_arrow", PLAIN_ARROW, "fire_charge"))
            .toString();

    assertTrue(message.contains("fire_charge_arrow") && message.contains("torch_arrow"), message);
  }

  @Test
  void rejectsNullInputs() {
    assertThrows(NullPointerException.class, () -> ArrowRecipeAudit.sharedCentres(null));
    assertThrows(
        NullPointerException.class,
        () -> new ArrowRecipe(null, Identifier.ofVanilla("arrow"), PLAIN_ARROW, PLAIN_ARROW));
  }
}
