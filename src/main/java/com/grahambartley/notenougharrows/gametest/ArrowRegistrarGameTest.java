package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.ModDataComponents;
import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.TintedArrowEntity;
import com.grahambartley.notenougharrows.item.TintedArrowItem;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void aTintedDefinitionRegistersATintedItemDefaultingToItsFallback(TestContext context) {
    for (final ArrowDefinition<?> definition : ModArrows.catalog().definitions()) {
      final Item item = Registries.ITEM.get(definition.id());
      if (definition.palette().isEmpty()) {
        context.assertFalse(
            item instanceof TintedArrowItem,
            "An untinted arrow " + definition.id() + " should not register a tinted item");
        continue;
      }
      context.assertTrue(
          item instanceof TintedArrowItem,
          "A tinted arrow " + definition.id() + " should register a tinted item");
      context.assertTrue(
          Registries.ENTITY_TYPE.get(definition.id()).create(context.getWorld())
              instanceof TintedArrowEntity,
          "A tinted arrow " + definition.id() + " should fly as an entity that can carry a tint");
      context.assertEquals(
          new ItemStack(item).get(ModDataComponents.ARROW_CHOICE),
          definition.palette().get().fallback().asComponent(),
          "A tinted arrow " + definition.id() + " should default to its palette's fallback");
    }
    context.complete();
  }
}
