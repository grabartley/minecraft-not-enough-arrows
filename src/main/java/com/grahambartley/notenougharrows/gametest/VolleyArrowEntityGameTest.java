package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.config.VolleyArrowConfig;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class VolleyArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "volley-arrow";
  private static final int JUST_AFTER_THE_SPLIT = VolleyArrowConfig.DEFAULT_SPLIT_DELAY_TICKS + 1;
  private static final int A_SECOND_WINDOW = JUST_AFTER_THE_SPLIT * 2;
  private static final int LANDED = 20;
  private static final BlockPos PLAIN_ARROW_STAND = new BlockPos(1, 2, 1);
  private static final BlockPos PLAIN_ARROW_TARGET = new BlockPos(6, 3, 1);
  private static final BlockPos VOLLEY_TARGET = new BlockPos(20, 3, 3);

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
  public void aFragmentTakesLessFromACowThanAPlainArrowDoes(TestContext context) {
    final CowEntity struckByPlainArrow =
        FiringRangeSupport.liveTargetOnPedestalAt(context, PLAIN_ARROW_TARGET);
    final CowEntity struckByVolley =
        FiringRangeSupport.liveTargetOnPedestalAt(context, VOLLEY_TARGET);
    final float plainBefore = struckByPlainArrow.getHealth();
    final float volleyBefore = struckByVolley.getHealth();
    MockPlayerSupport.fireEastStraight(
        context, MockPlayerSupport.playerAt(context, PLAIN_ARROW_STAND), Items.ARROW);
    CombatTestSupport.fireDownTheLongRange(context, ModArrows.VOLLEY_ARROW.item());

    context.runAtTick(
        LANDED,
        () -> {
          final float plainDealt = plainBefore - struckByPlainArrow.getHealth();
          final float volleyDealt = volleyBefore - struckByVolley.getHealth();
          context.assertTrue(plainDealt > 0.0f, "The plain arrow should have landed");
          context.assertTrue(volleyDealt > 0.0f, "At least one fragment should have landed");
          context.assertTrue(
              volleyDealt < plainDealt,
              "A fragment should take less from a cow than a plain arrow's "
                  + plainDealt
                  + ", it took "
                  + volleyDealt);
          context.complete();
        });
  }

  private static List<ArrowEntity> fragments(final TestContext context) {
    return context
        .getWorld()
        .getEntitiesByClass(ArrowEntity.class, context.getTestBox(), fragment -> true);
  }
}
