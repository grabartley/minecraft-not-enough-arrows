package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.BlockPos;

public final class ControlArrowEffectsGameTest implements FabricGameTest {
  private static final String BATCH = "control-effects";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int TICK_LIMIT = 60;

  private static final List<RegisteredArrow<?>> CONTROL_ARROWS =
      List.of(
          ModArrows.FROST_ARROW,
          ModArrows.LEVITATION_ARROW,
          ModArrows.TAUNT_ARROW,
          ModArrows.REPEL_ARROW,
          ModArrows.ALLEGIANCE_ARROW,
          ModArrows.SMOKE_ARROW,
          ModArrows.DISARM_ARROW);

  private static final List<RegistryEntry<StatusEffect>> VANILLA_TIPPED_ARROW_EFFECTS =
      List.of(StatusEffects.SLOWNESS, StatusEffects.POISON, StatusEffects.INSTANT_HEALTH);

  @CustomTestProvider
  public Collection<TestFunction> noControlArrowAppliesAnEffectVanillaSellsAsATippedArrow() {
    return CONTROL_ARROWS.stream()
        .map(
            arrow ->
                new TestFunction(
                    BATCH,
                    "notenougharrows.appliesnovanillatippedarroweffect." + arrow.id().getPath(),
                    FiringRangeSupport.TEMPLATE,
                    TICK_LIMIT,
                    0L,
                    true,
                    context -> assertAppliesNoForbiddenEffect(context, arrow)))
        .toList();
  }

  private static void assertAppliesNoForbiddenEffect(
      final TestContext context, final RegisteredArrow<?> arrow) {
    final CowEntity target = ControlTestSupport.stillCowAt(context, TARGET_STAND);
    MockPlayerSupport.fireEastStraight(
        context,
        MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
        arrow.item());

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          context.assertTrue(
              FiringRangeSupport.arrowWasSpent(context),
              "The " + arrow.id() + " should have struck the target, or this test proves nothing");
          assertNoneOfTheForbiddenEffects(context, target, arrow);
          context.complete();
        });
  }

  private static void assertNoneOfTheForbiddenEffects(
      final TestContext context, final LivingEntity target, final RegisteredArrow<?> arrow) {
    for (final RegistryEntry<StatusEffect> effect : VANILLA_TIPPED_ARROW_EFFECTS) {
      context.assertTrue(
          target.getStatusEffect(effect) == null,
          "The "
              + arrow.id()
              + " may not apply "
              + effect.getIdAsString()
              + ", which vanilla already sells as a tipped arrow");
    }
  }
}
