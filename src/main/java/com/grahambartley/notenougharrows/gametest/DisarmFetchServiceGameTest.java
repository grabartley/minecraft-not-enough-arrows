package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.control.DisarmDrop;
import com.grahambartley.notenougharrows.control.DisarmFetchService;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;

public final class DisarmFetchServiceGameTest implements FabricGameTest {
  private static final String BATCH = "disarm-fetch";
  private static final BlockPos TARGET_STAND = new BlockPos(3, 3, 3);
  private static final BlockPos YARD_PLAYER = new BlockPos(1, 2, 3);
  private static final BlockPos YARD_MOB = new BlockPos(4, 2, 3);
  private static final double A_REAL_THROW = 5.0;
  private static final int THROW_TICK = 5;
  private static final int FETCH_DEADLINE = 300;
  private static final int UNHARMABLE = 4;

  private record Armed(EntityType<? extends MobEntity> type, Item weapon) {}

  private static final List<Armed> ARMED_MOBS =
      List.of(
          new Armed(EntityType.SKELETON, Items.BOW),
          new Armed(EntityType.STRAY, Items.BOW),
          new Armed(EntityType.WITHER_SKELETON, Items.STONE_SWORD),
          new Armed(EntityType.ZOMBIE, Items.IRON_SWORD),
          new Armed(EntityType.HUSK, Items.IRON_SHOVEL),
          new Armed(EntityType.DROWNED, Items.TRIDENT),
          new Armed(EntityType.ZOMBIFIED_PIGLIN, Items.GOLDEN_SWORD),
          new Armed(EntityType.PIGLIN, Items.GOLDEN_SWORD),
          new Armed(EntityType.PIGLIN_BRUTE, Items.GOLDEN_AXE),
          new Armed(EntityType.VINDICATOR, Items.IRON_AXE),
          new Armed(EntityType.PILLAGER, Items.CROSSBOW));

  @CustomTestProvider
  public Collection<TestFunction> aDisarmedMobGoesAndFetchesItsWeapon() {
    return ARMED_MOBS.stream()
        .map(
            armed ->
                new TestFunction(
                    BATCH,
                    "notenougharrows.disarmedmobfetchesitsweapon."
                        + EntityType.getId(armed.type()).getPath(),
                    CombatTestSupport.LONG_RANGE,
                    FETCH_DEADLINE + 20,
                    0L,
                    true,
                    context -> assertFetchesItsWeapon(context, armed)))
        .toList();
  }

  private static void assertFetchesItsWeapon(final TestContext context, final Armed armed) {
    final PlayerEntity player = MockPlayerSupport.mortalPlayerAt(context, YARD_PLAYER);
    player.addStatusEffect(
        new StatusEffectInstance(StatusEffects.RESISTANCE, FETCH_DEADLINE * 2, UNHARMABLE));
    final MobEntity mob = context.spawnMob(armed.type(), YARD_MOB);
    mob.equipStack(EquipmentSlot.MAINHAND, new ItemStack(armed.weapon()));
    mob.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));

    context.runAtTick(
        THROW_TICK,
        () -> {
          mob.setTarget(player);
          context.assertTrue(
              DisarmDrop.disarm(context.getWorld(), mob, player.getPos(), true, A_REAL_THROW),
              "The " + armed.type().getUntranslatedName() + " should have been disarmed");
        });
    context.runAtTick(
        FETCH_DEADLINE,
        () -> {
          context.assertTrue(
              mob.getMainHandStack().isOf(armed.weapon()),
              "A disarmed "
                  + armed.type().getUntranslatedName()
                  + " should go and fetch its weapon, but it is holding "
                  + mob.getMainHandStack());
          context.complete();
        });
  }

  private static ZombieEntity armedZombie(final TestContext context) {
    final ZombieEntity zombie = ControlTestSupport.stillZombieAt(context, TARGET_STAND);
    zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    return zombie;
  }

  private static ItemEntity looseSword(final TestContext context) {
    final ItemEntity sword =
        new ItemEntity(
            context.getWorld(),
            context.getAbsolutePos(TARGET_STAND).getX(),
            context.getAbsolutePos(TARGET_STAND).getY(),
            context.getAbsolutePos(TARGET_STAND).getZ(),
            new ItemStack(Items.IRON_SWORD));
    context.getWorld().spawnEntity(sword);
    return sword;
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void fetchingNeedsNoLootingPermission(TestContext context) {
    final ZombieEntity target = armedZombie(context);

    DisarmFetchService.sendToFetch(context.getWorld(), target, looseSword(context));

    context.assertTrue(
        DisarmFetchService.isFetching(context.getWorld(), target),
        "A disarmed mob should be sent after its weapon");
    context.assertFalse(
        target.canPickUpLoot(),
        "The errand should not change what the mob may pick up for the rest of the save");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theErrandEndsWhenSomeoneElseTakesTheItem(TestContext context) {
    final ZombieEntity target = armedZombie(context);
    target.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, ItemStack.EMPTY);
    final ItemEntity sword = looseSword(context);
    DisarmFetchService.sendToFetch(context.getWorld(), target, sword);
    sword.discard();

    context.runAtTick(
        3,
        () -> {
          context.assertFalse(
              DisarmFetchService.isFetching(context.getWorld(), target),
              "With its weapon gone there is nothing left to fetch, so the mob is released");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aMobKilledDuringTheErrandStillDropsOnItsOwnOdds(TestContext context) {
    final ZombieEntity target = armedZombie(context);
    final float natural = target.getDropChance(EquipmentSlot.MAINHAND);
    DisarmFetchService.sendToFetch(context.getWorld(), target, looseSword(context));
    target.updateDropChances(EquipmentSlot.MAINHAND);

    context.runAtTick(
        2,
        () -> {
          context.assertEquals(
              target.getDropChance(EquipmentSlot.MAINHAND),
              natural,
              "A mob that got its gear back through vanilla looting must not be a guaranteed"
                  + " gear drop");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void vanillaLootingWouldOtherwiseMakeItAGuaranteedDrop(TestContext context) {
    final ZombieEntity target = armedZombie(context);
    final float natural = target.getDropChance(EquipmentSlot.MAINHAND);

    target.updateDropChances(EquipmentSlot.MAINHAND);

    context.assertTrue(
        target.getDropChance(EquipmentSlot.MAINHAND) > natural,
        "Vanilla raises the drop chance when a mob loots equipment, which is what the errand has"
            + " to undo for mobs that loot by nature");
    context.complete();
  }
}
