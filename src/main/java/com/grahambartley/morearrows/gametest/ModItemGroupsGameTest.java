package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.ModItemGroups;
import com.grahambartley.morearrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class ModItemGroupsGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void arrowsItemGroupResolvesByIdentifier(TestContext context) {
    context.assertTrue(
        Registries.ITEM_GROUP.containsId(ModItemGroups.ARROWS_ID),
        "The More Arrows item group should resolve by " + ModItemGroups.ARROWS_ID);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void arrowsItemGroupIconIsNotEmpty(TestContext context) {
    context.assertFalse(
        ModItemGroups.icon().isEmpty(), "The More Arrows item group icon should never be empty");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void everyRegisteredArrowIsOfferedByTheArrowsItemGroup(TestContext context) {
    final List<Identifier> offered = displayedItemIds(context);

    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      context.assertTrue(
          offered.contains(arrow.id()),
          "Arrow "
              + arrow.id()
              + " should be offered by the More Arrows creative tab, which offers "
              + offered);
    }
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theArrowsItemGroupOffersNothingElse(TestContext context) {
    final List<Identifier> offered = displayedItemIds(context);
    final List<Identifier> registered =
        ModArrows.registered().stream().map(RegisteredArrow::id).toList();

    context.assertEquals(
        offered.size(),
        registered.size(),
        "The More Arrows creative tab should offer exactly the mod's arrows, but offers "
            + offered);
    context.complete();
  }

  private static List<Identifier> displayedItemIds(final TestContext context) {
    final ServerWorld world = context.getWorld();
    final ItemGroup group = Registries.ITEM_GROUP.get(ModItemGroups.ARROWS_ID);
    context.assertTrue(group != null, "The More Arrows item group should be registered");

    group.updateEntries(
        new ItemGroup.DisplayContext(world.getEnabledFeatures(), true, world.getRegistryManager()));
    return group.getDisplayStacks().stream()
        .map(ItemStack::getItem)
        .map(Registries.ITEM::getId)
        .toList();
  }
}
