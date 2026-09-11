package com.grahambartley.morearrows.compat.emi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.compat.info.InfoEntry;
import com.grahambartley.morearrows.compat.info.InfoKeys;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

class MoreArrowsEmiPluginTest {

  private static final Identifier TNT_ARROW = Identifier.of(MoreArrows.MOD_ID, "tnt_arrow");
  private static final Identifier ROPE_ARROW = Identifier.of(MoreArrows.MOD_ID, "rope_arrow");

  private final Map<Identifier, EmiRecipe> byRecipeId = new HashMap<>();
  private final EmiInfoRecipes recipes = new EmiInfoRecipes(this::build);
  private final EmiRegistry registry = mock(EmiRegistry.class);

  private EmiRecipe build(
      final List<Identifier> itemIds, final List<Text> text, final Identifier id) {
    return byRecipeId.computeIfAbsent(id, unused -> mock(EmiRecipe.class));
  }

  private static InfoEntry entryFor(final Identifier itemId) {
    return InfoEntry.of(itemId, InfoKeys.description(itemId), InfoKeys.FIRING_KEY);
  }

  private final List<EmiRegistry> stationRegistrations = new ArrayList<>();
  private final EmiStationRegistrar station = stationRegistrations::add;

  private MoreArrowsEmiPlugin pluginFor(final InfoEntry... entries) {
    return new MoreArrowsEmiPlugin(recipes, () -> List.of(entries), station);
  }

  @Test
  void handsEmiOneInfoRecipePerArrowInRegistrationOrder() {
    pluginFor(entryFor(ROPE_ARROW), entryFor(TNT_ARROW)).register(registry);

    final InOrder inOrder = Mockito.inOrder(registry);
    inOrder
        .verify(registry)
        .addRecipe(byRecipeId.get(Identifier.of(MoreArrows.MOD_ID, "info/rope_arrow")));
    inOrder
        .verify(registry)
        .addRecipe(byRecipeId.get(Identifier.of(MoreArrows.MOD_ID, "info/tnt_arrow")));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void registersEachArrowExactlyOnceSoNoViewerShowsItTwice() {
    pluginFor(entryFor(TNT_ARROW)).register(registry);

    verify(registry).addRecipe(byRecipeId.get(Identifier.of(MoreArrows.MOD_ID, "info/tnt_arrow")));
    Mockito.verifyNoMoreInteractions(registry);
  }

  @Test
  void leavesEmiUntouchedWhenNoArrowIsRegisteredYet() {
    pluginFor().register(registry);

    verifyNoInteractions(registry);
  }

  @Test
  void handsTheStationItsOwnRegistrarExactlyOnce() {
    pluginFor(entryFor(TNT_ARROW)).register(registry);

    assertEquals(List.of(registry), stationRegistrations);
  }

  @Test
  void registersTheStationEvenWhenNoArrowCarriesAnInfoEntry() {
    pluginFor().register(registry);

    assertEquals(List.of(registry), stationRegistrations);
  }

  @Test
  void rejectsANullRegistry() {
    assertThrows(NullPointerException.class, () -> pluginFor().register(null));
  }

  @Test
  void rejectsCollaboratorsItWasNeverGiven() {
    assertThrows(
        NullPointerException.class, () -> new MoreArrowsEmiPlugin(null, List::of, station));
    assertThrows(NullPointerException.class, () -> new MoreArrowsEmiPlugin(recipes, null, station));
    assertThrows(
        NullPointerException.class, () -> new MoreArrowsEmiPlugin(recipes, List::of, null));
  }
}
