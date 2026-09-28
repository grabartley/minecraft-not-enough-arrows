package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.tow.TowTargets;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public final class TowTargetsGameTest implements FabricGameTest {
  private static final String BATCH = "tow-targets";
  private static final BlockPos SPOT = new BlockPos(3, 2, 3);

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anythingAliveCanBeTowed(TestContext context) {
    context.assertTrue(
        TowTargets.isTowable(context.spawnEntity(EntityType.ZOMBIE, SPOT), false), "Zombie");
    context.assertTrue(
        TowTargets.isTowable(context.spawnEntity(EntityType.VILLAGER, SPOT), false), "Villager");
    context.assertTrue(
        TowTargets.isTowable(context.spawnEntity(EntityType.BAT, SPOT), false), "Bat");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anEmptyVehicleCanBeTowed(TestContext context) {
    context.assertTrue(
        TowTargets.isTowable(context.spawnEntity(EntityType.BOAT, SPOT), false), "Boat");
    context.assertTrue(
        TowTargets.isTowable(context.spawnEntity(EntityType.MINECART, SPOT), false), "Minecart");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void whatARecallRefusesATowRefuses(TestContext context) {
    final ItemEntity item = context.spawnItem(Items.STICK, SPOT);
    final Entity orb =
        new ExperienceOrbEntity(context.getWorld(), item.getX(), item.getY(), item.getZ(), 3);
    final Entity arrow = context.spawnEntity(EntityType.ARROW, SPOT);

    context.assertFalse(TowTargets.isTowable(item, true), "A dropped item");
    context.assertFalse(TowTargets.isTowable(orb, true), "An experience orb");
    context.assertFalse(TowTargets.isTowable(arrow, true), "A projectile");
    context.assertFalse(
        TowTargets.isTowable(EntityType.WITHER.create(context.getWorld()), true), "The wither");
    context.assertFalse(
        TowTargets.isTowable(EntityType.ENDER_DRAGON.create(context.getWorld()), true),
        "The ender dragon");
    context.assertFalse(TowTargets.isTowable(null, true), "Nothing");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aPlayerIsTowedOnlyWhereTheServerLetsRecallMovePlayers(TestContext context) {
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();

    context.assertFalse(TowTargets.isTowable(player, false), "Setting off");
    context.assertTrue(TowTargets.isTowable(player, true), "Setting on");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aMountCarryingAPlayerCountsAsAPlayer(TestContext context) {
    final HorseEntity horse = context.spawnEntity(EntityType.HORSE, SPOT);
    final ServerPlayerEntity rider = context.createMockCreativeServerPlayerInWorld();
    rider.startRiding(horse, true);

    context.assertFalse(TowTargets.isTowable(horse, false), "Setting off");
    context.assertTrue(TowTargets.isTowable(horse, true), "Setting on");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aSpectatorIsNeverTowed(TestContext context) {
    final PlayerEntity spectator = context.createMockPlayer(GameMode.SPECTATOR);

    context.assertFalse(TowTargets.isTowable(spectator, true), "Spectator");
    context.complete();
  }
}
