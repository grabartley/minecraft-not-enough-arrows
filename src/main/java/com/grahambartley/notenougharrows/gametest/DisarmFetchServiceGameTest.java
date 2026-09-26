package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.DisarmFetchService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class DisarmFetchServiceGameTest implements FabricGameTest {
  private static final String BATCH = "disarm-fetch";
  private static final BlockPos TARGET_STAND = new BlockPos(3, 3, 3);

  private static ZombieEntity armedZombie(final TestContext context) {
    final ZombieEntity zombie = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    return zombie;
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMobThatCouldNotPickAnythingUpIsGrantedTheFetch(TestContext context) {
    final ZombieEntity target = armedZombie(context);

    context.assertFalse(target.canPickUpLoot(), "A plain zombie starts unable to pick things up");

    DisarmFetchService.letThemFetchItBack(context.getWorld(), target);

    context.assertTrue(target.canPickUpLoot(), "A disarmed mob should be able to fetch the item");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMobThatCouldAlreadyLootIsLeftExactlyAsItWas(TestContext context) {
    final ZombieEntity target = armedZombie(context);
    target.setCanPickUpLoot(true);
    final float before = target.getDropChance(EquipmentSlot.MAINHAND);

    DisarmFetchService.letThemFetchItBack(context.getWorld(), target);

    context.assertFalse(
        DisarmFetchService.isFetching(context.getWorld(), target),
        "A mob that could already loot needs no window, so none should be opened for it");
    context.assertEquals(
        target.getDropChance(EquipmentSlot.MAINHAND),
        before,
        "A mob that could already loot should keep its own drop chance");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aMobKilledDuringTheWindowStillDropsOnItsOwnOdds(TestContext context) {
    final ZombieEntity target = armedZombie(context);
    final float natural = target.getDropChance(EquipmentSlot.MAINHAND);
    DisarmFetchService.letThemFetchItBack(context.getWorld(), target);
    target.updateDropChances(EquipmentSlot.MAINHAND);

    context.runAtTick(
        10,
        () -> {
          context.assertEquals(
              target.getDropChance(EquipmentSlot.MAINHAND),
              natural,
              "A mob killed part way through the window must not be a guaranteed gear drop");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void fetchingTheItemBackWouldOtherwiseMakeItAGuaranteedDrop(TestContext context) {
    final ZombieEntity target = armedZombie(context);
    final float natural = target.getDropChance(EquipmentSlot.MAINHAND);

    target.updateDropChances(EquipmentSlot.MAINHAND);

    context.assertTrue(
        target.getDropChance(EquipmentSlot.MAINHAND) > natural,
        "Vanilla raises the drop chance when a mob picks equipment up, which is what the window"
            + " has to undo");
    context.complete();
  }
}
