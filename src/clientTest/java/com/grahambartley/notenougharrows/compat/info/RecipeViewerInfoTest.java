package com.grahambartley.notenougharrows.compat.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class RecipeViewerInfoTest {

  private static final Identifier TNT_ARROW = Identifier.of(NotEnoughArrows.MOD_ID, "tnt_arrow");
  private static final Identifier ROPE_ARROW = Identifier.of(NotEnoughArrows.MOD_ID, "rope_arrow");
  private static final Identifier WIND_ARROW = Identifier.of(NotEnoughArrows.MOD_ID, "wind_arrow");

  @Test
  void buildsOneEntryPerItemInTheOrderTheModRegisteredThem() {
    final List<InfoEntry> entries =
        RecipeViewerInfo.entries(List.of(WIND_ARROW, TNT_ARROW, ROPE_ARROW));

    assertEquals(
        List.of(List.of(WIND_ARROW), List.of(TNT_ARROW), List.of(ROPE_ARROW)),
        entries.stream().map(InfoEntry::itemIds).toList());
  }

  @Test
  void givesEveryItemItsOwnDescriptionFollowedByTheSharedFiringLine() {
    final List<InfoEntry> entries = RecipeViewerInfo.entries(List.of(TNT_ARROW, ROPE_ARROW));

    assertEquals(
        List.of(
            List.of("info.not-enough-arrows.tnt_arrow", InfoKeys.FIRING_KEY),
            List.of("info.not-enough-arrows.rope_arrow", InfoKeys.FIRING_KEY)),
        entries.stream().map(InfoEntry::translationKeys).toList());
  }

  @Test
  void sharesOneFiringKeyRatherThanACopyPerItem() {
    final List<InfoEntry> entries = RecipeViewerInfo.entries(List.of(TNT_ARROW, ROPE_ARROW));

    assertEquals(
        1,
        entries.stream()
            .flatMap(entry -> entry.translationKeys().stream())
            .filter(key -> key.equals(InfoKeys.FIRING_KEY))
            .distinct()
            .count());
  }

  @Test
  void buildsNothingWhenNoItemIsRegisteredYet() {
    assertTrue(RecipeViewerInfo.entries(List.of()).isEmpty());
  }

  @Test
  void rejectsATwiceListedItemSoAViewerCannotShowItTwice() {
    assertThrows(
        IllegalArgumentException.class,
        () -> RecipeViewerInfo.entries(List.of(TNT_ARROW, ROPE_ARROW, TNT_ARROW)));
  }

  @Test
  void rejectsANullItemId() {
    assertThrows(
        NullPointerException.class, () -> RecipeViewerInfo.entries(Arrays.asList(TNT_ARROW, null)));
  }

  @Test
  void rejectsANullCollection() {
    assertThrows(NullPointerException.class, () -> RecipeViewerInfo.entries(null));
  }
}
