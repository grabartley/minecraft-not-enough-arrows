package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.disguise.DisguiseForms;
import com.grahambartley.notenougharrows.disguise.DisguiseService;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class DisguiseServiceGameTest implements FabricGameTest {
  private static final String BATCH = "disguise-service";
  private static final BlockPos STAND = new BlockPos(8, 3, 8);
  private static final BlockPos PREY = new BlockPos(14, 3, 8);
  private static final int SHORT = 20;
  private static final int LONG = 2000;
  private static final float WOUND = 5.0f;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDisguisedMobWearsAHarmlessForm(TestContext context) {
    final ZombieEntity zombie = zombie(context);

    context.assertTrue(DisguiseService.disguise(context.getWorld(), zombie, LONG), "Disguised");
    context.assertTrue(DisguiseService.isDisguised(zombie), "The zombie is disguised");
    final Optional<Identifier> form = DisguiseService.formOf(context.getWorld(), zombie.getUuid());
    context.assertTrue(form.filter(DisguiseForms::isForm).isPresent(), "A harmless form");
    DisguiseService.revert(context.getWorld(), zombie);
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT + 20)
  public void aDisguiseRevertsWhenItsTimeIsUp(TestContext context) {
    final ZombieEntity zombie = zombie(context);
    DisguiseService.disguise(context.getWorld(), zombie, SHORT);

    context.runAtTick(
        SHORT - 5, () -> context.assertTrue(DisguiseService.isDisguised(zombie), "Still on"));
    context.runAtTick(
        SHORT + 5,
        () -> {
          context.assertFalse(DisguiseService.isDisguised(zombie), "Reverted on expiry");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT * 3)
  public void aSecondHitExtendsTheDisguiseAndKeepsItsForm(TestContext context) {
    final ZombieEntity zombie = zombie(context);
    DisguiseService.disguise(context.getWorld(), zombie, SHORT);
    final Optional<Identifier> form = DisguiseService.formOf(context.getWorld(), zombie.getUuid());

    context.runAtTick(SHORT - 5, () -> DisguiseService.disguise(context.getWorld(), zombie, SHORT));
    context.runAtTick(
        SHORT + 5,
        () -> {
          context.assertEquals(
              form, DisguiseService.formOf(context.getWorld(), zombie.getUuid()), "Same form");
          DisguiseService.revert(context.getWorld(), zombie);
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDisguiseRevertsWhenTheMobDies(TestContext context) {
    final ZombieEntity zombie = zombie(context);
    DisguiseService.disguise(context.getWorld(), zombie, LONG);

    zombie.kill();

    context.assertTrue(
        DisguiseService.formOf(context.getWorld(), zombie.getUuid()).isEmpty(),
        "Reverted on death");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDisguiseRevertsWhenTheMobUnloads(TestContext context) {
    final ZombieEntity zombie = zombie(context);
    DisguiseService.disguise(context.getWorld(), zombie, LONG);

    zombie.discard();

    context.assertTrue(
        DisguiseService.formOf(context.getWorld(), zombie.getUuid()).isEmpty(),
        "Reverted on unload");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDisguiseIsNeverSavedWithTheMob(TestContext context) {
    final ZombieEntity zombie = zombie(context);
    final NbtCompound before = zombie.writeNbt(new NbtCompound());
    DisguiseService.disguise(context.getWorld(), zombie, LONG);
    final NbtCompound during = zombie.writeNbt(new NbtCompound());
    DisguiseService.revert(context.getWorld(), zombie);

    context.assertFalse(during.contains("NoAI"), "The disguise does not switch the AI off");
    context.assertFalse(
        during.toString().contains("not-enough-arrows"), "Nothing of the disguise is saved");
    context.assertEquals(before.getKeys(), during.getKeys(), "The saved keys are unchanged");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT + 20)
  public void healthEquipmentNameAndTargetSurviveBothDirections(TestContext context) {
    final ZombieEntity zombie = zombie(context);
    final CowEntity prey = MobArena.cow(context, PREY);
    zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    zombie.setCustomName(Text.literal("Gerald"));
    zombie.setTarget(prey);
    zombie.damage(context.getWorld().getDamageSources().generic(), WOUND);
    final float health = zombie.getHealth();

    DisguiseService.disguise(context.getWorld(), zombie, SHORT);
    zombie.timeUntilRegen = 0;
    zombie.damage(context.getWorld().getDamageSources().generic(), WOUND);
    final float hurtWhileDisguised = zombie.getHealth();
    context.assertTrue(hurtWhileDisguised < health, "A disguised mob can still be hurt");

    context.runAtTick(
        SHORT + 5,
        () -> {
          context.assertFalse(DisguiseService.isDisguised(zombie), "Reverted");
          context.assertEquals(hurtWhileDisguised, zombie.getHealth(), "Health carried back");
          context.assertTrue(
              zombie.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.IRON_HELMET), "Helmet");
          context.assertTrue(
              zombie.getEquippedStack(EquipmentSlot.MAINHAND).isOf(Items.IRON_SWORD), "Sword");
          context.assertEquals(Text.literal("Gerald"), zombie.getCustomName(), "Name");
          context.assertTrue(zombie.getTarget() == prey, "Target");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anIneligibleMobOrAZeroDurationChangesNothing(TestContext context) {
    final VillagerEntity villager = context.spawnMob(EntityType.VILLAGER, STAND.north(3));
    final ZombieEntity zombie = zombie(context);

    context.assertFalse(DisguiseService.disguise(context.getWorld(), villager, LONG), "Villager");
    context.assertFalse(DisguiseService.isDisguised(villager), "The villager is unchanged");
    context.assertFalse(DisguiseService.disguise(context.getWorld(), zombie, 0), "Zero duration");
    context.assertFalse(DisguiseService.isDisguised(zombie), "The zombie is unchanged");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void revertingAMobThatIsNotDisguisedDoesNothing(TestContext context) {
    context.assertFalse(
        DisguiseService.revert(context.getWorld(), zombie(context)), "Nothing to revert");
    context.complete();
  }

  private static ZombieEntity zombie(final TestContext context) {
    return ChaosTestSupport.shaded(context.spawnEntity(EntityType.ZOMBIE, STAND));
  }
}
