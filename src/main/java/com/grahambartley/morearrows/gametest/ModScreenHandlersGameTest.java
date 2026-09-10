package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModScreenHandlers;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ModScreenHandlersGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 10)
  public void theFletchingStationResolvesInTheScreenHandlerRegistry(TestContext context) {
    context.assertTrue(
        Registries.SCREEN_HANDLER.containsId(ModScreenHandlers.FLETCHING_STATION_ID),
        "Screen handler "
            + ModScreenHandlers.FLETCHING_STATION_ID
            + " should resolve in the screen handler registry");
    context.assertTrue(
        Registries.SCREEN_HANDLER.get(ModScreenHandlers.FLETCHING_STATION_ID)
            == ModScreenHandlers.FLETCHING_STATION,
        "Screen handler "
            + ModScreenHandlers.FLETCHING_STATION_ID
            + " should resolve to the registered instance");
    context.complete();
  }
}
