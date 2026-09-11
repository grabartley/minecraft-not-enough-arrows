package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ArrowRegistrarGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyRegisteredArrowResolvesInTheItemRegistry(TestContext context) {
    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      context.assertTrue(
          Registries.ITEM.containsId(arrow.id()),
          "Arrow item " + arrow.id() + " should resolve in the item registry");
      context.assertTrue(
          Registries.ITEM.get(arrow.id()) == arrow.item(),
          "Arrow item " + arrow.id() + " should resolve to the registered item instance");
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyRegisteredArrowResolvesInTheEntityTypeRegistry(TestContext context) {
    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      context.assertTrue(
          Registries.ENTITY_TYPE.containsId(arrow.id()),
          "Arrow entity type " + arrow.id() + " should resolve in the entity type registry");
      context.assertTrue(
          Registries.ENTITY_TYPE.get(arrow.id()) == arrow.entityType(),
          "Arrow entity type " + arrow.id() + " should resolve to the registered type instance");
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyCatalogedArrowIsRegistered(TestContext context) {
    context.assertEquals(
        ModArrows.registered().size(),
        ModArrows.catalog().size(),
        "Every cataloged arrow definition should produce exactly one registration");
    context.complete();
  }
}
