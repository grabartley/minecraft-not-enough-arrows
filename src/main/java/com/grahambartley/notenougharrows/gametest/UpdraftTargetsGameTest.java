package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.updraft.UpdraftTargets;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;

public final class UpdraftTargetsGameTest implements FabricGameTest {
  private static final String BATCH = "updraft-targets";
  private static final BlockPos SPOT = new BlockPos(3, 2, 3);

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void mobsPlayersItemsAndVehiclesAreAllLifted(TestContext context) {
    context.assertTrue(
        UpdraftTargets.canBeLifted(context.spawnEntity(EntityType.COW, SPOT)), "Cow");
    context.assertTrue(
        UpdraftTargets.canBeLifted(context.createMockCreativeServerPlayerInWorld()), "Player");
    context.assertTrue(UpdraftTargets.canBeLifted(context.spawnItem(Items.STICK, SPOT)), "Item");
    context.assertTrue(
        UpdraftTargets.canBeLifted(context.spawnEntity(EntityType.BOAT, SPOT)), "Boat");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aPassengerRisesWithItsVehicleRatherThanOnItsOwn(TestContext context) {
    final BoatEntity boat = context.spawnEntity(EntityType.BOAT, SPOT);
    final CowEntity cow = context.spawnEntity(EntityType.COW, SPOT);
    cow.startRiding(boat, true);

    context.assertFalse(UpdraftTargets.canBeLifted(cow), "A seated cow");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void somethingThatDoesNotFallIsLeftAlone(TestContext context) {
    final ArmorStandEntity stand = context.spawnEntity(EntityType.ARMOR_STAND, SPOT);
    stand.setNoGravity(true);
    final ServerPlayerEntity flyer = context.createMockCreativeServerPlayerInWorld();
    flyer.getAbilities().flying = true;

    context.assertFalse(UpdraftTargets.canBeLifted(stand), "A gravityless armor stand");
    context.assertFalse(UpdraftTargets.canBeLifted(flyer), "A player already flying");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aSpectatorAndAHangingDecorationAreLeftAlone(TestContext context) {
    final PlayerEntity spectator = context.createMockPlayer(GameMode.SPECTATOR);
    context.setBlockState(SPOT.north(), Blocks.STONE);
    final ItemFrameEntity frame =
        new ItemFrameEntity(context.getWorld(), context.getAbsolutePos(SPOT), Direction.SOUTH);

    context.assertFalse(UpdraftTargets.canBeLifted(spectator), "Spectator");
    context.assertFalse(UpdraftTargets.canBeLifted(frame), "Item frame");
    context.assertFalse(UpdraftTargets.canBeLifted(null), "Nothing");
    context.complete();
  }
}
