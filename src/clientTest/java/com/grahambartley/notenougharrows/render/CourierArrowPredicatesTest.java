package com.grahambartley.notenougharrows.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import org.junit.jupiter.api.Test;

class CourierArrowPredicatesTest {

  @Test
  void aLoadedArrowSelectsTheLoadedModel() {
    assertEquals(1f, CourierArrowPredicates.loadedness(true));
  }

  @Test
  void anEmptyArrowKeepsTheEmptyModel() {
    assertEquals(0f, CourierArrowPredicates.loadedness(false));
  }

  @Test
  void namesThePredicateTheItemModelOverridesOn() {
    assertEquals(NotEnoughArrows.MOD_ID + ":loaded", CourierArrowPredicates.LOADED.toString());
  }
}
