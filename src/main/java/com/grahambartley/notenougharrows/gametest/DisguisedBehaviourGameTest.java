package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.disguise.DisguiseService;
import com.grahambartley.notenougharrows.disguise.DisguisedBehaviour;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class DisguisedBehaviourGameTest implements FabricGameTest {
  private static final String BATCH = "disguised-behaviour";
  private static final BlockPos STAND = new BlockPos(20, 3, 8);
  private static final int WANDER_WATCH = 380;
  private static final int LONG = 2000;
  private static final double MOVED = 0.5;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aLitCreeperIsDefusedWhenDisguised(TestContext context) {
    final CreeperEntity creeper = context.spawnMob(EntityType.CREEPER, STAND);
    creeper.setFuseSpeed(1);

    DisguisedBehaviour.begin(creeper);

    context.assertEquals(-1, creeper.getFuseSpeed(), "Fuse speed");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDrawnBowIsLoweredWhenDisguised(TestContext context) {
    final SkeletonEntity skeleton = context.spawnMob(EntityType.SKELETON, STAND);
    skeleton.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));
    skeleton.setCurrentHand(Hand.MAIN_HAND);
    context.assertTrue(skeleton.isUsingItem(), "The skeleton should be drawing its bow");

    DisguisedBehaviour.begin(skeleton);

    context.assertFalse(skeleton.isUsingItem(), "The bow should be lowered");
    context.complete();
  }

  @GameTest(
      templateName = MobArena.TEMPLATE,
      batchId = BATCH,
      tickLimit = WANDER_WATCH + 20,
      maxAttempts = 3)
  public void aDisguisedMobStillWandersLikeAnAnimal(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    final Vec3d start = zombie.getPos();
    DisguiseService.disguise(context.getWorld(), zombie, LONG);

    context.runAtTick(
        WANDER_WATCH,
        () -> {
          context.assertTrue(
              zombie.getPos().distanceTo(start) > MOVED, "The disguised zombie should wander");
          DisguiseService.revert(context.getWorld(), zombie);
          context.complete();
        });
  }
}
