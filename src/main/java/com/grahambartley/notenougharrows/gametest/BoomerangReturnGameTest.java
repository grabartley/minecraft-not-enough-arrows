package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.chaos.BoomerangReturn;
import com.grahambartley.notenougharrows.chaos.BoomerangReturn.Outcome;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BoomerangReturnGameTest implements FabricGameTest {
  private static final String BATCH = "boomerang-return";
  private static final BlockPos SHOOTER = new BlockPos(8, 3, 8);
  private static final BlockPos FAR_OFF = new BlockPos(30, 3, 8);

  private static final Item BOOMERANG = ChaosArrows.BOOMERANG_ARROW.item();

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aShooterWithRoomIsGrantedTheArrow(TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, SHOOTER);

    context.assertEquals(Outcome.GRANTED, resolve(context, shooter, true), "Outcome");
    context.assertEquals(1, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
    context.assertEquals(0, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aShooterWithAFullInventoryHasItDroppedAtTheirFeet(TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, SHOOTER);
    AgricultureTestSupport.fillInventory(shooter);

    context.assertEquals(Outcome.DROPPED_AT_FEET, resolve(context, shooter, true), "Outcome");
    context.assertEquals(1, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aFullCreativeShooterHasItDroppedAtTheirFeetRatherThanDeleted(TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, SHOOTER);
    shooter.changeGameMode(net.minecraft.world.GameMode.CREATIVE);
    AgricultureTestSupport.fillInventory(shooter);

    context.assertEquals(Outcome.DROPPED_AT_FEET, resolve(context, shooter, true), "Outcome");
    context.assertEquals(1, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aShooterThatCannotHoldItemsHasItDroppedAtTheirFeet(TestContext context) {
    final ZombieEntity shooter = context.spawnMob(EntityType.ZOMBIE, SHOOTER);

    context.assertEquals(
        Outcome.DROPPED_AT_FEET,
        BoomerangReturn.resolve(
            context.getWorld(), new ItemStack(BOOMERANG), shooter, farOff(context), true),
        "Outcome");
    context.assertEquals(1, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDeadShooterLeavesItWhereItFell(TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, SHOOTER);
    shooter.kill();

    context.assertEquals(Outcome.DROPPED_WHERE_IT_FELL, resolve(context, shooter, true), "Outcome");
    context.assertEquals(0, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void noShooterLeavesItWhereItFell(TestContext context) {
    context.assertEquals(Outcome.DROPPED_WHERE_IT_FELL, resolve(context, null, true), "Outcome");
    context.assertEquals(1, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anArrowNobodyMayPickUpReturnsNothing(TestContext context) {
    final ServerPlayerEntity shooter = ChaosTestSupport.survivalPlayerAt(context, SHOOTER);

    context.assertEquals(Outcome.NOTHING_TO_RETURN, resolve(context, shooter, false), "Outcome");
    context.assertEquals(0, ChaosTestSupport.countOf(shooter, BOOMERANG), "Held");
    context.assertEquals(0, ChaosTestSupport.droppedNearby(context, BOOMERANG), "Dropped");
    context.complete();
  }

  private static Outcome resolve(
      final TestContext context, final ServerPlayerEntity shooter, final boolean recoverable) {
    return BoomerangReturn.resolve(
        context.getWorld(), new ItemStack(BOOMERANG), shooter, farOff(context), recoverable);
  }

  private static Vec3d farOff(final TestContext context) {
    return Vec3d.ofBottomCenter(context.getAbsolutePos(FAR_OFF));
  }
}
