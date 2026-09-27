package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.config.FrostArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHold;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.ControlPersistence;
import com.grahambartley.notenougharrows.control.DisarmDrop;
import com.grahambartley.notenougharrows.control.DisarmFetchService;
import com.grahambartley.notenougharrows.control.FrostGripService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class ControlPersistenceGameTest implements FabricGameTest {
  private static final String BATCH = "control-persistence";
  private static final BlockPos DEFENDED_STAND = new BlockPos(3, 3, 5);
  private static final BlockPos MOB_STAND = new BlockPos(3, 3, 3);
  private static final int GONE = 3;
  private static final int BACK = 6;
  private static final int SETTLED = 10;

  private static ZombieEntity reloaded(final TestContext context, final ZombieEntity original) {
    final NbtCompound saved = new NbtCompound();
    original.saveSelfNbt(saved);
    original.discard();
    final ZombieEntity copy = EntityType.ZOMBIE.create(context.getWorld());
    copy.readNbt(saved);
    return copy;
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anAllyIsStillAnAllyAfterItIsSavedAndLoaded(TestContext context) {
    final CowEntity defended = ControlTestSupport.stillCowAt(context, DEFENDED_STAND);
    final ZombieEntity ally = ControlTestSupport.stillZombieAt(context, MOB_STAND);
    ControlHoldService.enlist(context.getWorld(), ally, defended, AllegianceArrowConfig.defaults());
    final ControlHold before = ally.getAttached(ControlPersistence.HOLD);
    final ZombieEntity[] copy = new ZombieEntity[1];

    context.runAtTick(GONE, () -> copy[0] = reloaded(context, ally));
    context.runAtTick(
        BACK,
        () -> {
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), copy[0]).isEmpty(),
              "Once the mob is gone its hold should be forgotten in memory, or this proves"
                  + " nothing");
          context.getWorld().spawnEntity(copy[0]);
        });
    context.runAtTick(
        SETTLED,
        () -> {
          context.assertTrue(
              before.equals(copy[0].getAttached(ControlPersistence.HOLD)),
              "The allegiance should be written into the mob's saved data unchanged");
          context.assertTrue(
              ControlHoldService.heldIn(context.getWorld(), copy[0]).isPresent(),
              "A reloaded ally should pick its allegiance back up");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void anAllegianceThatRanOutWhileUnloadedIsDroppedOnLoad(TestContext context) {
    final CowEntity defended = ControlTestSupport.stillCowAt(context, DEFENDED_STAND);
    final ZombieEntity mob = EntityType.ZOMBIE.create(context.getWorld());
    mob.refreshPositionAndAngles(context.getAbsolutePos(MOB_STAND), 0.0f, 0.0f);
    mob.setAttached(
        ControlPersistence.HOLD,
        ControlHold.defending(
            mob.getUuid(),
            mob.getPos(),
            defended.getUuid(),
            16.0,
            context.getWorld().getTime() - 1));

    context.getWorld().spawnEntity(mob);

    context.assertFalse(
        ControlHoldService.heldIn(context.getWorld(), mob).isPresent(),
        "An allegiance that expired while the mob was unloaded should not come back");
    context.assertFalse(
        mob.hasAttached(ControlPersistence.HOLD),
        "An expired allegiance should be cleared from the mob's saved data");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aFrozenMobIsStillFrozenAfterItIsSavedAndLoaded(TestContext context) {
    final ZombieEntity frozen = ControlTestSupport.stillZombieAt(context, MOB_STAND);
    FrostGripService.grip(context.getWorld(), frozen, FrostArrowConfig.defaults());
    final ZombieEntity[] copy = new ZombieEntity[1];

    context.runAtTick(GONE, () -> copy[0] = reloaded(context, frozen));
    context.runAtTick(BACK, () -> context.getWorld().spawnEntity(copy[0]));
    context.runAtTick(
        SETTLED,
        () -> {
          context.assertTrue(
              copy[0].isFrozen(), "A reloaded mob should still be held frozen by the arrow");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aMobFetchingItsWeaponKeepsFetchingAfterItIsSavedAndLoaded(TestContext context) {
    final ZombieEntity disarmed = ControlTestSupport.stillZombieAt(context, MOB_STAND);
    disarmed.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    DisarmDrop.disarm(context.getWorld(), disarmed, null, true, 2.0);
    final ZombieEntity[] copy = new ZombieEntity[1];

    context.runAtTick(GONE, () -> copy[0] = reloaded(context, disarmed));
    context.runAtTick(BACK, () -> context.getWorld().spawnEntity(copy[0]));
    context.runAtTick(
        SETTLED,
        () -> {
          context.assertTrue(
              DisarmFetchService.isFetching(context.getWorld(), copy[0]),
              "A reloaded mob should still be going after its weapon");
          context.complete();
        });
  }
}
