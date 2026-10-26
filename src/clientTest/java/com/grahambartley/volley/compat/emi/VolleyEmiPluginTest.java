package com.grahambartley.volley.compat.emi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.grahambartley.volley.Volley;
import com.grahambartley.volley.compat.info.InfoEntry;
import com.grahambartley.volley.item.InfoKeys;
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

class VolleyEmiPluginTest {

  private static final Identifier TNT_ARROW = Identifier.of(Volley.MOD_ID, "tnt_arrow");
  private static final Identifier ROPE_ARROW = Identifier.of(Volley.MOD_ID, "rope_arrow");

  private static final EmiVariantComparisons NO_VARIANTS = new EmiVariantComparisons(List::of);

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

  private VolleyEmiPlugin pluginFor(final InfoEntry... entries) {
    return new VolleyEmiPlugin(recipes, () -> List.of(entries), station, NO_VARIANTS);
  }

  @Test
  void handsEmiOneInfoRecipePerArrowInRegistrationOrder() {
    pluginFor(entryFor(ROPE_ARROW), entryFor(TNT_ARROW)).register(registry);

    final InOrder inOrder = Mockito.inOrder(registry);
    inOrder
        .verify(registry)
        .addRecipe(byRecipeId.get(Identifier.of(Volley.MOD_ID, "info/rope_arrow")));
    inOrder
        .verify(registry)
        .addRecipe(byRecipeId.get(Identifier.of(Volley.MOD_ID, "info/tnt_arrow")));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void registersEachArrowExactlyOnceSoNoViewerShowsItTwice() {
    pluginFor(entryFor(TNT_ARROW)).register(registry);

    verify(registry).addRecipe(byRecipeId.get(Identifier.of(Volley.MOD_ID, "info/tnt_arrow")));
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
        NullPointerException.class,
        () -> new VolleyEmiPlugin(null, List::of, station, NO_VARIANTS));
    assertThrows(
        NullPointerException.class, () -> new VolleyEmiPlugin(recipes, null, station, NO_VARIANTS));
    assertThrows(
        NullPointerException.class,
        () -> new VolleyEmiPlugin(recipes, List::of, null, NO_VARIANTS));
    assertThrows(
        NullPointerException.class, () -> new VolleyEmiPlugin(recipes, List::of, station, null));
  }
}
