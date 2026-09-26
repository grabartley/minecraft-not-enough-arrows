package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;

public final class HarmlessArrowGameTest implements FabricGameTest {
  private static final String BATCH = "harmless-arrow";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int TICK_LIMIT = 60;

  private static final List<RegisteredArrow<?>> HARMLESS =
      List.of(
          ModArrows.FROST_ARROW,
          ModArrows.LEVITATION_ARROW,
          ModArrows.TAUNT_ARROW,
          ModArrows.REPEL_ARROW,
          ModArrows.ALLEGIANCE_ARROW,
          ModArrows.SMOKE_ARROW,
          ModArrows.DISARM_ARROW,
          ModArrows.HASTE_ARROW,
          ModArrows.GUARD_ARROW,
          ModArrows.GLOW_INK_ARROW,
          ModArrows.WIND_ARROW);

  private static final List<RegisteredArrow<?>> HURTFUL =
      List.of(ModArrows.RUST_ARROW, ModArrows.MILK_ARROW, ModArrows.LIFESTEAL_ARROW);

  @CustomTestProvider
  public Collection<TestFunction> anEffectArrowLeavesWhatItHitsUnhurt() {
    return perArrow(
        HARMLESS,
        "notenougharrows.effectarrowleaveswhatithitsunhurt",
        dealt -> dealt == 0.0f,
        "should leave what it hits unhurt");
  }

  @CustomTestProvider
  public Collection<TestFunction> aFightingArrowStillHurtsWhatItHits() {
    return perArrow(
        HURTFUL,
        "notenougharrows.fightingarrowstillhurtswhatithits",
        dealt -> dealt > 0.0f,
        "should still hurt what it hits");
  }

  private static Collection<TestFunction> perArrow(
      final List<RegisteredArrow<?>> arrows,
      final String behavior,
      final Predicate<Float> expected,
      final String promise) {
    return arrows.stream()
        .map(
            arrow ->
                new TestFunction(
                    BATCH,
                    behavior + "." + arrow.id().getPath(),
                    FiringRangeSupport.TEMPLATE,
                    TICK_LIMIT,
                    0L,
                    true,
                    context -> assertDealt(context, arrow, expected, promise)))
        .toList();
  }

  private static void assertDealt(
      final TestContext context,
      final RegisteredArrow<?> arrow,
      final Predicate<Float> expected,
      final String promise) {
    final CowEntity target = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float before = target.getHealth();
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        arrow.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.arrowWasSpent(context),
              "The " + arrow.id() + " should have struck the target");
          final float dealt = before - target.getHealth();
          context.assertTrue(
              expected.test(dealt), "A " + arrow.id() + " " + promise + ", but it dealt " + dealt);
          context.complete();
        });
  }
}
