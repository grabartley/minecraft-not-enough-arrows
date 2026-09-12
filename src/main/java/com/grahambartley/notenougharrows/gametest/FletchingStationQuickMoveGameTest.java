package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.fletching.FletchingStationScreenHandler;
import com.grahambartley.notenougharrows.fletching.FletchingStationSlots;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FletchingStationQuickMoveGameTest implements FabricGameTest {
  private static final String BATCH = "fletching-station-quick-move";
  private static final int A_HANDFUL = 5;
  private static final int FIRST_HOTBAR_INVENTORY_SLOT = 0;

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void shiftClickingFromTheInventoryMovesItemsIntoTheStation(TestContext context) {
    final ServerPlayerEntity player =
        MockPlayerSupport.playerAt(context, FletchingStationSupport.TABLE.east());
    final FletchingStationScreenHandler station =
        FletchingStationSupport.openStation(context, player);
    player
        .getInventory()
        .setStack(FIRST_HOTBAR_INVENTORY_SLOT, new ItemStack(Items.ARROW, A_HANDFUL));

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.FIRST_HOTBAR_SLOT);

    context.assertEquals(
        A_HANDFUL,
        FletchingStationSupport.countInInputs(station, Items.ARROW),
        "Arrows moved into the station by one shift click");
    context.assertEquals(
        0,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows left in the player inventory after shift clicking them in");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void shiftClickingAnInputMovesItBackToTheInventory(TestContext context) {
    final ServerPlayerEntity player =
        MockPlayerSupport.playerAt(context, FletchingStationSupport.TABLE.east());
    final FletchingStationScreenHandler station =
        FletchingStationSupport.openStation(context, player);
    station.getSlot(0).setStack(new ItemStack(Items.ARROW, A_HANDFUL));

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.FIRST_INPUT_SLOT);

    context.assertEquals(
        A_HANDFUL,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows returned to the player inventory by one shift click");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.ARROW),
        "Arrows left in the station after shift clicking them out");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void shiftClickingIntoAFullStationFallsBackToTheRestOfTheInventory(TestContext context) {
    final ServerPlayerEntity player =
        MockPlayerSupport.playerAt(context, FletchingStationSupport.TABLE.east());
    final FletchingStationScreenHandler station =
        FletchingStationSupport.openStation(context, player);
    for (int slot = 0; slot < FletchingStationSlots.INPUT_COUNT; slot++) {
      station.getSlot(slot).setStack(new ItemStack(Items.STONE, Items.STONE.getMaxCount()));
    }
    player
        .getInventory()
        .setStack(FIRST_HOTBAR_INVENTORY_SLOT, new ItemStack(Items.ARROW, A_HANDFUL));

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.FIRST_HOTBAR_SLOT);

    context.assertEquals(
        A_HANDFUL,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows kept by the player when the station has no room");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.ARROW),
        "Arrows squeezed into a full station");
    context.assertTrue(
        player.getInventory().getStack(FIRST_HOTBAR_INVENTORY_SLOT).isEmpty(),
        "A full station should send the stack on to the main inventory the way vanilla does");
    context.complete();
  }
}
