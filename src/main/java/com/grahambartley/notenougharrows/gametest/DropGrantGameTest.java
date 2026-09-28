package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.DropGrant;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;

public final class DropGrantGameTest implements FabricGameTest {
  private static final String BATCH = "drop-grant";

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDropGoesIntoTheReceiversInventoryWhenItHasRoom(TestContext context) {
    final PlayerEntity receiver = context.createMockPlayer(GameMode.SURVIVAL);

    grant(context, receiver, new ItemStack(Items.COBBLESTONE, 3));

    context.assertEquals(
        receiver.getInventory().count(Items.COBBLESTONE), 3, "Cobblestone in the inventory");
    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.COBBLESTONE),
        0,
        "Cobblestone on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDropWithNobodyToReceiveItFallsAtTheBlock(TestContext context) {
    grant(context, null, new ItemStack(Items.COBBLESTONE, 2));

    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.COBBLESTONE),
        2,
        "Cobblestone on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aFullInventoryNeverDestroysADrop(TestContext context) {
    final PlayerEntity receiver = context.createMockPlayer(GameMode.SURVIVAL);
    final PlayerInventory inventory = receiver.getInventory();
    for (int slot = 0; slot < inventory.main.size(); slot++) {
      inventory.main.set(slot, new ItemStack(Items.STONE, Items.STONE.getMaxCount()));
    }

    grant(context, receiver, new ItemStack(Items.DIAMOND));

    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.DIAMOND),
        1,
        "A diamond with nowhere to go should land on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void theStacksHandedInAreNeverChanged(TestContext context) {
    final ItemStack drop = new ItemStack(Items.COBBLESTONE, 4);

    grant(context, context.createMockPlayer(GameMode.SURVIVAL), drop);

    context.assertEquals(drop.getCount(), 4, "The caller's stack count");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCollectedDropIsTakenOffTheGroundIntoTheInventory(TestContext context) {
    final PlayerEntity receiver = context.createMockPlayer(GameMode.SURVIVAL);
    final ItemEntity drop = context.spawnItem(Items.WHITE_WOOL, TerrainTestSupport.CENTER);

    DropGrant.collect(List.of(drop), receiver);

    context.assertEquals(receiver.getInventory().count(Items.WHITE_WOOL), 1, "Wool received");
    context.assertTrue(drop.isRemoved(), "The wool is no longer on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCollectedDropWithNoRoomStaysOnTheGround(TestContext context) {
    final PlayerEntity receiver = context.createMockPlayer(GameMode.SURVIVAL);
    AgricultureTestSupport.fillInventory(receiver);
    final ItemEntity drop = context.spawnItem(Items.WHITE_WOOL, TerrainTestSupport.CENTER);

    DropGrant.collect(List.of(drop), receiver);

    context.assertTrue(drop.isAlive(), "The wool stays where it fell");
    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.WHITE_WOOL), 1, "Wool on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCollectedDropThatPartlyFitsLeavesTheRestOnTheGround(TestContext context) {
    final PlayerEntity receiver = context.createMockPlayer(GameMode.SURVIVAL);
    AgricultureTestSupport.fillInventory(receiver);
    receiver.getInventory().main.set(0, new ItemStack(Items.WHITE_WOOL, 62));
    final ItemEntity drop = context.spawnItem(Items.WHITE_WOOL, TerrainTestSupport.CENTER);
    drop.setStack(new ItemStack(Items.WHITE_WOOL, 5));

    DropGrant.collect(List.of(drop), receiver);

    context.assertEquals(receiver.getInventory().count(Items.WHITE_WOOL), 64, "Wool received");
    context.assertEquals(
        TerrainTestSupport.droppedCount(context, Items.WHITE_WOOL), 3, "Wool left on the ground");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aCollectedDropWithNobodyToReceiveItStaysOnTheGround(TestContext context) {
    final ItemEntity drop = context.spawnItem(Items.WHITE_WOOL, TerrainTestSupport.CENTER);

    DropGrant.collect(List.of(drop), null);

    context.assertTrue(drop.isAlive(), "The wool stays where it fell");
    context.complete();
  }

  private static void grant(
      final TestContext context, final PlayerEntity receiver, final ItemStack drop) {
    DropGrant.grant(
        context.getWorld(),
        context.getAbsolutePos(TerrainTestSupport.CENTER),
        List.of(drop),
        receiver);
  }
}
