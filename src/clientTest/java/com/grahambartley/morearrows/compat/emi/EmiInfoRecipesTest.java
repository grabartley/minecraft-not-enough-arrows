package com.grahambartley.morearrows.compat.emi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.compat.info.InfoEntry;
import com.grahambartley.morearrows.compat.info.InfoKeys;
import dev.emi.emi.api.recipe.EmiRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmiInfoRecipesTest {

  private static final Identifier TNT_ARROW = Identifier.of(MoreArrows.MOD_ID, "tnt_arrow");
  private static final Identifier ROPE_ARROW = Identifier.of(MoreArrows.MOD_ID, "rope_arrow");

  private final List<Built> built = new ArrayList<>();
  private final EmiInfoRecipes recipes = new EmiInfoRecipes(this::build);

  private record Built(List<Identifier> itemIds, List<Text> text, Identifier id) {}

  private EmiRecipe build(
      final List<Identifier> itemIds, final List<Text> text, final Identifier id) {
    built.add(new Built(itemIds, text, id));
    return mock(EmiRecipe.class);
  }

  private static InfoEntry entryFor(final Identifier itemId) {
    return InfoEntry.of(itemId, InfoKeys.description(itemId), InfoKeys.FIRING_KEY);
  }

  @Test
  void buildsOneRecipePerEntryInTheOrderTheModRegisteredThem() {
    final List<EmiRecipe> result = recipes.from(List.of(entryFor(ROPE_ARROW), entryFor(TNT_ARROW)));

    assertEquals(2, result.size());
    assertEquals(
        List.of(
            Identifier.of(MoreArrows.MOD_ID, "info/rope_arrow"),
            Identifier.of(MoreArrows.MOD_ID, "info/tnt_arrow")),
        built.stream().map(Built::id).toList());
  }

  @ParameterizedTest
  @ValueSource(strings = {"tnt_arrow", "rope_arrow", "wind_arrow"})
  void namesEachRecipeAfterTheItemItDescribesSoTwoNeverCollide(final String path) {
    final Identifier itemId = Identifier.of(MoreArrows.MOD_ID, path);

    assertEquals(
        Identifier.of(MoreArrows.MOD_ID, EmiInfoRecipes.ID_PREFIX + path),
        EmiInfoRecipes.idFor(entryFor(itemId)));
  }

  @Test
  void handsTheViewerTheItemsAndTheResolvedTextTheEntryCarries() {
    final InfoEntry entry = entryFor(TNT_ARROW);

    recipes.from(entry);

    assertEquals(List.of(TNT_ARROW), built.getFirst().itemIds());
    assertEquals(
        List.of(
            Text.translatable("info.more-arrows.tnt_arrow"),
            Text.translatable(InfoKeys.FIRING_KEY)),
        built.getFirst().text());
  }

  @Test
  void buildsNothingWhenNoArrowIsRegisteredYet() {
    assertTrue(recipes.from(List.of()).isEmpty());
    assertTrue(built.isEmpty());
  }

  @Test
  void rejectsAFactoryThatWasNeverSupplied() {
    assertThrows(NullPointerException.class, () -> new EmiInfoRecipes(null));
  }

  @Test
  void rejectsANullEntryCollection() {
    assertThrows(NullPointerException.class, () -> recipes.from((List<InfoEntry>) null));
  }

  @Test
  void rejectsANullEntry() {
    assertThrows(NullPointerException.class, () -> recipes.from((InfoEntry) null));
    assertThrows(NullPointerException.class, () -> EmiInfoRecipes.idFor(null));
  }
}
