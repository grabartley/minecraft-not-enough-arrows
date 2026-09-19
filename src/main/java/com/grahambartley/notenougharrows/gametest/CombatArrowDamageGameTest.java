package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class CombatArrowDamageGameTest implements FabricGameTest {
  private static final String BATCH = "combat-arrow-damage";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final BlockPos SECOND_TARGET_STAND = new BlockPos(5, 3, 5);
  private static final BlockPos SECOND_SHOOTER_STAND = new BlockPos(1, 2, 5);
  private static final Vec3d SPAWN_SPOT = new Vec3d(0.0, 0.0, 0.0);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPlainCombatArrowCarriesExactlyWhatAVanillaArrowCarries(TestContext context) {
    context.assertEquals(
        damageOf(context, ModArrows.RUST_ARROW),
        vanillaDamage(context),
        "the damage a rust arrow carries");
    context.assertEquals(
        damageOf(context, ModArrows.MILK_ARROW),
        vanillaDamage(context),
        "the damage a milk arrow carries");
    context.assertEquals(
        damageOf(context, ModArrows.HOMING_ARROW),
        vanillaDamage(context),
        "the damage a homing arrow carries");
    context.assertEquals(
        damageOf(context, ModArrows.RAILGUN_ARROW),
        vanillaDamage(context),
        "the damage a railgun arrow carries");
    context.assertEquals(
        damageOf(context, ModArrows.SHOCK_ARROW),
        vanillaDamage(context),
        "the damage a shock arrow carries");
    context.assertEquals(
        damageOf(context, ModArrows.VOLLEY_ARROW),
        vanillaDamage(context),
        "the damage a volley arrow carries");
    context.assertEquals(
        damageOf(context, ModArrows.LIFESTEAL_ARROW),
        vanillaDamage(context),
        "the damage a lifesteal arrow carries");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aSupportArrowIsTooGentleToBeAWeapon(TestContext context) {
    final double vanilla = vanillaDamage(context);

    context.assertTrue(
        damageOf(context, ModArrows.HASTE_ARROW) < vanilla,
        "A haste arrow should scratch rather than wound");
    context.assertTrue(
        damageOf(context, ModArrows.GUARD_ARROW) < vanilla,
        "A guard arrow should scratch rather than wound");
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aBowFiredCombatArrowIsCriticalJustAsAVanillaArrowIs(TestContext context) {
    MockPlayerSupport.fireEastFromBow(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.RUST_ARROW.item());

    context.runAtTick(
        1,
        () -> {
          final PersistentProjectileEntity fired =
              FiringRangeSupport.firedArrow(context, PersistentProjectileEntity.class);
          context.assertTrue(fired != null, "The arrow should be in flight");
          context.assertTrue(
              fired.isCritical(),
              "A fully drawn bow should send a combat arrow out critical, as it does a vanilla"
                  + " arrow");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aGuardArrowCostsItsTargetLessHealthThanAPlainArrow(TestContext context) {
    final CowEntity struckByPlainArrow =
        FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final CowEntity struckByGuard =
        FiringRangeSupport.liveTargetOnPedestalAt(context, SECOND_TARGET_STAND);
    final float plainBefore = struckByPlainArrow.getHealth();
    final float guardBefore = struckByGuard.getHealth();
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        Items.ARROW);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, SECOND_SHOOTER_STAND),
        ModArrows.GUARD_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          final float plainDealt = plainBefore - struckByPlainArrow.getHealth();
          final float guardDealt = guardBefore - struckByGuard.getHealth();
          context.assertTrue(plainDealt > 0.0f, "The plain arrow should have landed");
          context.assertTrue(
              guardDealt < plainDealt,
              "A guard arrow should cost its target less than a plain arrow's "
                  + plainDealt
                  + ", it cost "
                  + guardDealt);
          context.assertTrue(
              struckByGuard.getAbsorptionAmount() > 0.0f,
              "A guard arrow should leave its target with absorption to show for it");
          context.complete();
        });
  }

  private static double vanillaDamage(final TestContext context) {
    return context.spawnEntity(EntityType.ARROW, SPAWN_SPOT).getDamage();
  }

  private static double damageOf(final TestContext context, final RegisteredArrow<?> arrow) {
    return context.spawnEntity(arrow.entityType(), SPAWN_SPOT).getDamage();
  }
}
