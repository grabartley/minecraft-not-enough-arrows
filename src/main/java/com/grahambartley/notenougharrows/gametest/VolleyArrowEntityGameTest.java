package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.config.VolleyArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;

public final class VolleyArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "volley-arrow";
  private static final int JUST_AFTER_THE_SPLIT = VolleyArrowConfig.DEFAULT_SPLIT_DELAY_TICKS + 1;
  private static final int A_SECOND_WINDOW = JUST_AFTER_THE_SPLIT * 3;

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void anArrowBecomesTheConfiguredNumberOfOrdinaryArrows(TestContext context) {
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.VOLLEY_ARROW.item());

    context.runAtTick(
        JUST_AFTER_THE_SPLIT,
        () -> {
          context.assertEquals(
              fragments(context).size(),
              VolleyArrowConfig.DEFAULT_FRAGMENT_COUNT,
              "volley fragments");
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void theVolleyArrowItselfIsGoneOnceItHasSplit(TestContext context) {
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.VOLLEY_ARROW.item());

    context.runAtTick(
        JUST_AFTER_THE_SPLIT,
        () -> {
          context.dontExpectEntity(ModArrows.VOLLEY_ARROW.entityType());
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 80)
  public void noFragmentSplitsAgain(TestContext context) {
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.VOLLEY_ARROW.item());

    context.runAtTick(
        JUST_AFTER_THE_SPLIT,
        () ->
            context.assertEquals(
                fragments(context).size(),
                VolleyArrowConfig.DEFAULT_FRAGMENT_COUNT,
                "fragments just after the split"));
    context.runAtTick(
        A_SECOND_WINDOW,
        () -> {
          context.assertTrue(
              fragments(context).size() <= VolleyArrowConfig.DEFAULT_FRAGMENT_COUNT,
              "A fragment must never split again, the count grew to " + fragments(context).size());
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void everyFragmentIsUnrecoverable(TestContext context) {
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.VOLLEY_ARROW.item());

    context.runAtTick(
        JUST_AFTER_THE_SPLIT,
        () -> {
          final List<ArrowEntity> fragments = fragments(context);
          context.assertTrue(!fragments.isEmpty(), "The volley arrow should have split");
          for (final ArrowEntity fragment : fragments) {
            context.assertTrue(
                fragment.pickupType == PersistentProjectileEntity.PickupPermission.DISALLOWED,
                "A volley fragment must never be pickable");
          }
          context.complete();
        });
  }

  @GameTest(templateName = CombatTestSupport.LONG_RANGE, batchId = BATCH, tickLimit = 60)
  public void everyFragmentHitsSofterThanAnOrdinaryArrow(TestContext context) {
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.VOLLEY_ARROW.item());

    context.runAtTick(
        JUST_AFTER_THE_SPLIT,
        () -> {
          final List<ArrowEntity> fragments = fragments(context);
          context.assertTrue(!fragments.isEmpty(), "The volley arrow should have split");
          final double ordinaryDamage =
              context.spawnEntity(EntityType.ARROW, new Vec3d(0.0, 0.0, 0.0)).getDamage();
          for (final ArrowEntity fragment : fragments) {
            context.assertTrue(
                fragment.getDamage() < ordinaryDamage,
                "A volley fragment should hit softer than an ordinary arrow's "
                    + ordinaryDamage
                    + ", it was "
                    + fragment.getDamage());
          }
          context.complete();
        });
  }

  private static List<ArrowEntity> fragments(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(ArrowEntity.class, context.getTestBox(), fragment -> true);
  }
}
