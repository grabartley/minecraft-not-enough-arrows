package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.server.ModdedClients;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class ModdedClientsGameTest implements FabricGameTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, tickLimit = 20)
  public void aConnectionThatNeverDeclaredThisModsPayloadsDoesNotHaveTheMod(TestContext context) {
    context.assertFalse(
        ModdedClients.hasTheMod(context.createMockCreativeServerPlayerInWorld().networkHandler),
        "A connection with no client behind it never declared this mod's payloads");
    context.complete();
  }
}
