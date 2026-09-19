package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public final class DisarmArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "disarm-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  private static ZombieEntity armedZombieAt(final TestContext context, final BlockPos stand) {
    final ZombieEntity zombie = ControlTestSupport.stillZombieAt(context, stand);
    zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    return zombie;
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void anArrowFiredFromABowEmptiesTheTargetsMainHand(TestContext context) {
    final ZombieEntity target = armedZombieAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.DISARM_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getStackInHand(Hand.MAIN_HAND).isEmpty(),
              "A disarm arrow should leave its target's main hand empty");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void theItemLandsOnTheGroundWhereItCanBePickedBackUp(TestContext context) {
    armedZombieAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.DISARM_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.expectEntityAround(EntityType.ITEM, TARGET_STAND, 3.0);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void wornArmourIsLeftWhereItIs(TestContext context) {
    final ZombieEntity target = armedZombieAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.DISARM_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.IRON_HELMET),
              "A disarm arrow should never take a worn armour piece");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aTargetHoldingNothingDropsNothing(TestContext context) {
    ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.DISARM_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.dontExpectEntity(EntityType.ITEM);
          context.complete();
        });
  }
}
