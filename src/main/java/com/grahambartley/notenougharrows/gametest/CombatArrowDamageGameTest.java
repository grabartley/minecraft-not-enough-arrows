package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class CombatArrowDamageGameTest implements FabricGameTest {
  private static final String BATCH = "combat-arrow-damage";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final Vec3d SPAWN_SPOT = new Vec3d(0.0, 0.0, 0.0);
  private static final float ONE_HEART = 2.0f;

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPlainCombatArrowCarriesExactlyWhatAVanillaArrowCarries(TestContext context) {
    final double vanilla = vanillaDamage(context);

    for (final RegisteredArrow<?> arrow :
        java.util.List.of(
            ModArrows.RUST_ARROW,
            ModArrows.MILK_ARROW,
            ModArrows.HOMING_ARROW,
            ModArrows.RAILGUN_ARROW,
            ModArrows.SHOCK_ARROW,
            ModArrows.VOLLEY_ARROW,
            ModArrows.LIFESTEAL_ARROW)) {
      context.assertEquals(
          damageOf(context, arrow), vanilla, "the damage " + arrow.id() + " carries");
    }
    context.complete();
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aHasteArrowCostsItsTargetAtMostOneHeart(TestContext context) {
    assertCostsAtMostOneHeart(context, ModArrows.HASTE_ARROW.item(), "haste");
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aGuardArrowCostsItsTargetAtMostOneHeart(TestContext context) {
    assertCostsAtMostOneHeart(context, ModArrows.GUARD_ARROW.item(), "guard");
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aPlainArrowCostsMoreThanAHeartOnTheSameRange(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final float before = target.getHealth();
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        net.minecraft.item.Items.ARROW);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          final float dealt = before - target.getHealth();
          context.assertTrue(
              dealt > ONE_HEART,
              "A plain arrow on this range should cost more than a heart, or the support arrows'"
                  + " gentleness proves nothing. It cost "
                  + dealt);
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aBowFiredCombatArrowLeavesTheBowCritical(TestContext context) {
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
              fired.isCritical(), "A fully drawn bow should send a combat arrow out critical");
          context.complete();
        });
  }

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aGuardArrowLeavesItsTargetWithAbsorptionToShowForIt(TestContext context) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        ModArrows.GUARD_ARROW.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              target.getAbsorptionAmount() > 0.0f,
              "A guard arrow should leave its target with absorption to show for it");
          context.complete();
        });
  }

  private static void assertCostsAtMostOneHeart(
      final TestContext context, final Item arrow, final String name) {
    final CowEntity target = FiringRangeSupport.liveTargetOnPedestalAt(context, TARGET_STAND);
    final float before = target.getHealth();
    MockPlayerSupport.fireEastStraight(
        context, MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND), arrow);

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          final float dealt = before - target.getHealth();
          context.assertTrue(dealt > 0.0f, "The " + name + " arrow should have landed");
          context.assertTrue(
              dealt <= ONE_HEART,
              "A " + name + " arrow should cost at most a heart to receive, it cost " + dealt);
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
