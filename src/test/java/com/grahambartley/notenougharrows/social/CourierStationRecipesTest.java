package com.grahambartley.notenougharrows.social;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

class CourierStationRecipesTest {

  @Test
  void ownsTheLoadingRecipe() {
    assertTrue(CourierStationRecipes.owns(CourierStationRecipes.LOAD_ID));
  }

  @Test
  void ownsTheUnloadingRecipe() {
    assertTrue(CourierStationRecipes.owns(CourierStationRecipes.UNLOAD_ID));
  }

  @Test
  void doesNotOwnAShippedArrowRecipe() {
    assertFalse(
        CourierStationRecipes.owns(Identifier.of(NotEnoughArrows.MOD_ID, "fletching/milk_arrow")));
  }

  @Test
  void doesNotOwnTheCourierArrowsOwnCraftingRecipe() {
    assertFalse(
        CourierStationRecipes.owns(
            Identifier.of(NotEnoughArrows.MOD_ID, "fletching/courier_arrow")));
  }

  @Test
  void doesNotOwnANullId() {
    assertFalse(CourierStationRecipes.owns(null));
  }
}
