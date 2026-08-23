package com.grahambartley.morearrows.compat.info;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.morearrows.MoreArrows;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InfoEntryTest {

  private static final Identifier TNT_ARROW = Identifier.of(MoreArrows.MOD_ID, "tnt_arrow");
  private static final Identifier ROPE_ARROW = Identifier.of(MoreArrows.MOD_ID, "rope_arrow");

  @Test
  void describesTheItemsItWasBuiltFor() {
    final InfoEntry entry =
        new InfoEntry(List.of(TNT_ARROW, ROPE_ARROW), List.of("info.more-arrows.tnt_arrow"));

    assertEquals(List.of(TNT_ARROW, ROPE_ARROW), entry.itemIds());
  }

  @Test
  void buildsASingleItemEntryFromVarargKeys() {
    final InfoEntry entry = InfoEntry.of(TNT_ARROW, "first.key", "second.key");

    assertEquals(List.of(TNT_ARROW), entry.itemIds());
    assertEquals(List.of("first.key", "second.key"), entry.translationKeys());
  }

  @Test
  void resolvesEveryKeyThroughTheLanguageFileInOrder() {
    final InfoEntry entry = InfoEntry.of(TNT_ARROW, "first.key", "second.key");

    assertEquals(
        List.of(Text.translatable("first.key"), Text.translatable("second.key")), entry.texts());
  }

  @Test
  void copiesItsListsSoALaterEditCannotChangeWhatAViewerShows() {
    final List<Identifier> itemIds = new ArrayList<>(List.of(TNT_ARROW));
    final List<String> keys = new ArrayList<>(List.of("info.more-arrows.tnt_arrow"));
    final InfoEntry entry = new InfoEntry(itemIds, keys);

    itemIds.add(ROPE_ARROW);
    keys.add("info.more-arrows.rope_arrow");

    assertEquals(List.of(TNT_ARROW), entry.itemIds());
    assertEquals(List.of("info.more-arrows.tnt_arrow"), entry.translationKeys());
  }

  @Test
  void rejectsAnEntryThatDescribesNoItem() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new InfoEntry(List.of(), List.of("info.more-arrows.tnt_arrow")));
  }

  @Test
  void rejectsAnEntryWithNothingToSay() {
    assertThrows(
        IllegalArgumentException.class, () -> new InfoEntry(List.of(TNT_ARROW), List.of()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\t"})
  void rejectsABlankTranslationKey(final String key) {
    assertThrows(IllegalArgumentException.class, () -> InfoEntry.of(TNT_ARROW, key));
  }

  @Test
  void rejectsANullTranslationKey() {
    final List<String> keys = Arrays.asList("info.more-arrows.tnt_arrow", null);

    assertThrows(NullPointerException.class, () -> new InfoEntry(List.of(TNT_ARROW), keys));
  }

  @Test
  void rejectsNullLists() {
    assertThrows(NullPointerException.class, () -> new InfoEntry(null, List.of("a.key")));
    assertThrows(NullPointerException.class, () -> new InfoEntry(List.of(TNT_ARROW), null));
  }
}
