package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.DisarmDrop;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public final class DisarmDropGameTest implements FabricGameTest {
  private static final String BATCH = "disarm-drop";
  private static final double NO_THROW = 0.0;
  private static final BlockPos TARGET_STAND = new BlockPos(3, 3, 3);
  private static final BlockPos PLAYER_STAND = new BlockPos(3, 3, 5);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerIsLeftArmedWhenTheServerSettingIsOff(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));

    context.assertFalse(
        DisarmDrop.disarm(context.getWorld(), player, false, NO_THROW),
        "With the setting off the server should refuse to disarm a player");
    context.assertTrue(
        player.getStackInHand(Hand.MAIN_HAND).isOf(Items.IRON_SWORD),
        "A player refused by the setting should still be holding what they held");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMobIsDisarmedEvenWhenTheServerSettingIsOff(TestContext context) {
    final ZombieEntity target = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    target.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));

    context.assertTrue(
        DisarmDrop.disarm(context.getWorld(), target, false, NO_THROW),
        "The setting guards players only, so a mob is disarmed either way");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerIsDisarmedWhenTheServerSettingIsOn(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));

    context.assertTrue(
        DisarmDrop.disarm(context.getWorld(), player, true, NO_THROW),
        "With the setting on a player should be disarmed like anything else");
    context.assertTrue(
        player.getStackInHand(Hand.MAIN_HAND).isEmpty(),
        "A disarmed player should be left with an empty main hand");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEmptyHandedTargetDropsNothing(TestContext context) {
    final ZombieEntity target = ControlTestSupport.stillZombieAt(context, TARGET_STAND);

    context.assertFalse(
        DisarmDrop.disarm(context.getWorld(), target, true, NO_THROW),
        "A target holding nothing should have nothing taken from it");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDeadTargetIsLeftToVanillasOwnEquipmentDropRoll(TestContext context) {
    final ZombieEntity target = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    target.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    target.setHealth(0.0f);

    context.assertFalse(
        DisarmDrop.disarm(context.getWorld(), target, true, NO_THROW),
        "A kill has already run vanilla's drop roll, so a disarm must not hand out the equipment");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theSettingIsReadWhereTheEffectResolves(TestContext context) {
    final ServerPlayerEntity player = MockPlayerSupport.playerAt(context, PLAYER_STAND);
    final ZombieEntity mob = ControlTestSupport.stillZombieAt(context, TARGET_STAND);

    context.assertFalse(
        DisarmDrop.reaches(player, false), "The setting off should refuse a player");
    context.assertTrue(DisarmDrop.reaches(player, true), "The setting on should reach a player");
    context.assertTrue(DisarmDrop.reaches(mob, false), "The setting never guards a mob");
    context.assertFalse(DisarmDrop.reaches(null, true), "Nothing at all is never reached");
    mob.setHealth(0.0f);
    context.assertFalse(DisarmDrop.reaches(mob, true), "A dead target is never reached");
    context.complete();
  }
}
