package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.fletching.FletchingStationScreenHandler;
import com.grahambartley.notenougharrows.fletching.FletchingStationSlots;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public final class FletchingStationScreenHandlerGameTest implements FabricGameTest {
  private static final String BATCH = "fletching-station";
  private static final int TOO_FEW_ARROWS = FletchingTestSupport.ARROWS_CONSUMED - 1;
  private static final int A_SPARE_ARROW = 1;
  private static final int ROOM_FOR_HALF_A_CRAFT = 60;

  @BeforeBatch(batchId = BATCH)
  public void installTheFletchingRecipeBeforeBatch(ServerWorld world) {
    FletchingTestSupport.installTestRecipe(world);
  }

  @AfterBatch(batchId = BATCH)
  public void removeTheFletchingRecipeAfterBatch(ServerWorld world) {
    FletchingTestSupport.removeTestRecipe(world);
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void validInputsShowTheResultTheRecipeDeclares(TestContext context) {
    final FletchingStationScreenHandler station = station(context);
    FletchingStationSupport.loadInputs(station);

    final ItemStack result = FletchingStationSupport.result(station);
    context.assertTrue(result.isOf(Items.ARROW), "Valid inputs should show the recipe result");
    context.assertEquals(
        FletchingTestSupport.ARROWS_PRODUCED,
        result.getCount(),
        "Result count the station shows for valid inputs");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void inputsThatMatchNoRecipeShowNoResult(TestContext context) {
    final FletchingStationScreenHandler station = station(context);
    station.getSlot(0).setStack(new ItemStack(Items.ARROW, TOO_FEW_ARROWS));
    station.getSlot(1).setStack(new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED));

    context.assertTrue(
        FletchingStationSupport.result(station).isEmpty(),
        "Inputs below a recipe's declared counts should show nothing");
    context.assertTrue(
        station.getAvailableRecipes().isEmpty(),
        "Inputs below a recipe's declared counts should offer no recipe");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEmptyStationShowsNoResult(TestContext context) {
    final FletchingStationScreenHandler station = station(context);

    context.assertTrue(
        FletchingStationSupport.result(station).isEmpty(), "An empty station should show nothing");
    context.assertEquals(
        FletchingStationScreenHandler.NO_SELECTION,
        station.getSelectedRecipe(),
        "Selected recipe of an empty station");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void takingTheResultConsumesExactlyTheDeclaredInputs(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    FletchingStationSupport.loadInputs(station);

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.RESULT_SLOT);

    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.ARROW),
        "Arrows left in the station after one craft");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.TNT),
        "Tnt left in the station after one craft");
    context.assertEquals(
        FletchingTestSupport.ARROWS_PRODUCED,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows granted to the player by one craft");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anItemNoIngredientClaimsBlocksTheRecipe(TestContext context) {
    final FletchingStationScreenHandler station = station(context);
    FletchingStationSupport.loadInputs(station);
    station.getSlot(2).setStack(new ItemStack(Items.ARROW, A_SPARE_ARROW));

    context.assertTrue(
        FletchingStationSupport.result(station).isEmpty(),
        "A recipe claims every occupied slot, so a leftover item should show no result");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void inputsThatMatchNoRecipeAreNeverConsumed(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    station.getSlot(0).setStack(new ItemStack(Items.ARROW, TOO_FEW_ARROWS));
    station.getSlot(1).setStack(new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED));

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.RESULT_SLOT);

    context.assertEquals(
        TOO_FEW_ARROWS,
        FletchingStationSupport.countInInputs(station, Items.ARROW),
        "Arrows left in the station after clicking an empty result");
    context.assertEquals(
        0,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows granted by clicking an empty result");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void twoTakesInOneTickProduceOnlyOneResult(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    FletchingStationSupport.loadInputs(station);

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.RESULT_SLOT);
    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.RESULT_SLOT);

    context.assertEquals(
        FletchingTestSupport.ARROWS_PRODUCED,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows granted by two takes against one set of inputs");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.TNT),
        "Tnt left after two takes against one set of inputs");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void twoPlayersEachGetTheirOwnInputsRatherThanSharingOne(TestContext context) {
    final ServerPlayerEntity first = player(context);
    final ServerPlayerEntity second = player(context);
    final FletchingStationScreenHandler firstStation = station(context, first);
    final FletchingStationScreenHandler secondStation = station(context, second);
    FletchingStationSupport.loadInputs(firstStation);

    context.assertTrue(
        FletchingStationSupport.result(secondStation).isEmpty(),
        "One player's inputs must never show up in another player's station");

    FletchingStationSupport.shiftClick(secondStation, second, FletchingStationSlots.RESULT_SLOT);

    context.assertEquals(
        0,
        FletchingStationSupport.countHeld(second, Items.ARROW),
        "Arrows a second player can take from another player's inputs");
    context.assertEquals(
        FletchingTestSupport.ARROWS_CONSUMED,
        FletchingStationSupport.countInInputs(firstStation, Items.ARROW),
        "Arrows left in the first player's station");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void closingTheStationReturnsEveryInputToThePlayer(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    FletchingStationSupport.loadInputs(station);

    station.onClosed(player);

    context.assertEquals(
        FletchingTestSupport.ARROWS_CONSUMED,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows returned to the player on close");
    context.assertEquals(
        FletchingTestSupport.TNT_CONSUMED,
        FletchingStationSupport.countHeld(player, Items.TNT),
        "Tnt returned to the player on close");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.ARROW),
        "Arrows the closed station still holds");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void closingTheStationDropsInputsThePlayerHasNoRoomFor(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    fillInventory(player);
    FletchingStationSupport.loadInputs(station);

    station.onClosed(player);

    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.TNT),
        "Tnt the closed station still holds when the player has no room");
    context.assertTrue(
        context
            .getWorld()
            .getEntitiesByClass(ItemEntity.class, context.getTestBox(), entity -> true)
            .stream()
            .anyMatch(entity -> entity.getStack().isOf(Items.TNT)),
        "Inputs a full player cannot hold should be dropped rather than destroyed");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aResultThePlayerHasOnlyPartialRoomForIsDroppedRatherThanDestroyed(
      TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    fillInventory(player);
    player.getInventory().setStack(0, new ItemStack(Items.ARROW, ROOM_FOR_HALF_A_CRAFT));
    FletchingStationSupport.loadInputs(station);

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.RESULT_SLOT);

    context.assertEquals(
        Items.ARROW.getMaxCount(),
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows the player has room for after a partial take");
    context.assertEquals(
        FletchingTestSupport.ARROWS_PRODUCED - (Items.ARROW.getMaxCount() - ROOM_FOR_HALF_A_CRAFT),
        droppedArrows(context),
        "Arrows dropped because the player had no room for them");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void oneShiftClickCraftsAsManyTimesAsTheInputsAllow(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    station
        .getSlot(0)
        .setStack(new ItemStack(Items.ARROW, FletchingTestSupport.ARROWS_CONSUMED * 2));
    station.getSlot(1).setStack(new ItemStack(Items.TNT, FletchingTestSupport.TNT_CONSUMED * 2));

    FletchingStationSupport.shiftClick(station, player, FletchingStationSlots.RESULT_SLOT);

    context.assertEquals(
        FletchingTestSupport.ARROWS_PRODUCED * 2,
        FletchingStationSupport.countHeld(player, Items.ARROW),
        "Arrows granted by one shift click over inputs worth two crafts");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.TNT),
        "Tnt left after one shift click over inputs worth two crafts");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theResultSlotRefusesAnythingPlacedIntoIt(TestContext context) {
    final FletchingStationScreenHandler station = station(context);

    context.assertFalse(
        station.getSlot(FletchingStationSlots.RESULT_SLOT).canInsert(new ItemStack(Items.ARROW)),
        "The result slot is an output, so it should refuse inserted items");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void selectingARecipeOutsideTheAvailableListIsRefused(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    FletchingStationSupport.loadInputs(station);
    final int selectedBefore = station.getSelectedRecipe();

    context.assertFalse(
        station.onButtonClick(player, station.getAvailableRecipes().size()),
        "A selection past the end of the available list should be refused");
    context.assertFalse(
        station.onButtonClick(player, -1), "A negative selection should be refused");
    context.assertEquals(
        selectedBefore, station.getSelectedRecipe(), "Selection after a refused button click");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStationAwayFromAFletchingTableIsNoLongerUsable(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);

    context.assertTrue(station.canUse(player), "A station on a fletching table should be usable");

    context.setBlockState(FletchingStationSupport.TABLE, Blocks.AIR);

    context.assertFalse(
        station.canUse(player), "A station whose fletching table is gone should stop being usable");
    context.complete();
  }

  @GameTest(templateName = FletchingTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void pickingUpTheResultPutsExactlyOneCraftOnTheCursor(TestContext context) {
    final ServerPlayerEntity player = player(context);
    final FletchingStationScreenHandler station = station(context, player);
    FletchingStationSupport.loadInputs(station);

    station.onSlotClick(FletchingStationSlots.RESULT_SLOT, 0, SlotActionType.PICKUP, player);

    context.assertTrue(
        station.getCursorStack().isOf(Items.ARROW), "Picking up the result should hold arrows");
    context.assertEquals(
        FletchingTestSupport.ARROWS_PRODUCED,
        station.getCursorStack().getCount(),
        "Arrows on the cursor after picking up one craft");
    context.assertEquals(
        0,
        FletchingStationSupport.countInInputs(station, Items.TNT),
        "Tnt left after picking up one craft");
    context.complete();
  }

  private static ServerPlayerEntity player(final TestContext context) {
    return MockPlayerSupport.playerAt(context, FletchingStationSupport.TABLE.east());
  }

  private static FletchingStationScreenHandler station(final TestContext context) {
    return station(context, player(context));
  }

  private static FletchingStationScreenHandler station(
      final TestContext context, final ServerPlayerEntity player) {
    return FletchingStationSupport.openStation(context, player);
  }

  private static int droppedArrows(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(
            ItemEntity.class, context.getTestBox(), entity -> entity.getStack().isOf(Items.ARROW))
        .stream()
        .mapToInt(entity -> entity.getStack().getCount())
        .sum();
  }

  private static void fillInventory(final ServerPlayerEntity player) {
    for (int slot = 0; slot < player.getInventory().main.size(); slot++) {
      player.getInventory().main.set(slot, new ItemStack(Items.STONE, Items.STONE.getMaxCount()));
    }
  }
}
