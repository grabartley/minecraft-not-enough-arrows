package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModItemGroups;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

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
}
