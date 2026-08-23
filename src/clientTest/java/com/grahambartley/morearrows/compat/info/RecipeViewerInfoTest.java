package com.grahambartley.morearrows.compat.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class RecipeViewerInfoTest {

  private static final Identifier TNT_ARROW = Identifier.of(MoreArrows.MOD_ID, "tnt_arrow");
  private static final Identifier ROPE_ARROW = Identifier.of(MoreArrows.MOD_ID, "rope_arrow");
  private static final Identifier WIND_ARROW = Identifier.of(MoreArrows.MOD_ID, "wind_arrow");

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
            List.of("info.more-arrows.tnt_arrow", InfoKeys.FIRING_KEY),
            List.of("info.more-arrows.rope_arrow", InfoKeys.FIRING_KEY)),
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
  void coversEveryArrowTheModRegisteredWithoutDroppingOrReorderingAny() {
    assertEquals(
        ModArrows.registered().stream().map(RegisteredArrow::id).toList(),
        RecipeViewerInfo.arrowEntries().stream()
            .flatMap(entry -> entry.itemIds().stream())
            .toList());
  }

  @Test
  void rejectsANullCollection() {
    assertThrows(NullPointerException.class, () -> RecipeViewerInfo.entries(null));
  }
}
