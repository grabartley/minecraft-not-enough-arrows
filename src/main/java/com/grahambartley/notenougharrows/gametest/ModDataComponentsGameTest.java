package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModDataComponents;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ModDataComponentsGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theArrowChoiceComponentResolvesByIdentifier(final TestContext context) {
    context.assertTrue(
        Registries.DATA_COMPONENT_TYPE.get(ModDataComponents.ARROW_CHOICE_ID)
            == ModDataComponents.ARROW_CHOICE,
        "The arrow choice component should resolve by " + ModDataComponents.ARROW_CHOICE_ID);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theArrowChoiceComponentIsSavedAndSynced(final TestContext context) {
    context.assertFalse(
        ModDataComponents.ARROW_CHOICE.shouldSkipSerialization(),
        "The arrow choice should be written to disk, or a restart would lose it");
    context.assertTrue(
        ModDataComponents.ARROW_CHOICE.getPacketCodec() != null,
        "The arrow choice should reach the client, or the tint could not be drawn");
    context.complete();
  }
}
