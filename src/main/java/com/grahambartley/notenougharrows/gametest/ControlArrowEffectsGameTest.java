package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class ControlArrowEffectsGameTest implements FabricGameTest {
  private static final String BATCH = "control-effects";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  private static final List<RegisteredArrow<?>> CONTROL_ARROWS =
      List.of(
          ModArrows.FROST_ARROW,
          ModArrows.LEVITATION_ARROW,
          ModArrows.TAUNT_ARROW,
          ModArrows.REPEL_ARROW,
          ModArrows.DAZE_ARROW,
          ModArrows.SMOKE_ARROW,
          ModArrows.DISARM_ARROW);

  private static final List<RegistryEntry<StatusEffect>> VANILLA_TIPPED_ARROW_EFFECTS =
      List.of(StatusEffects.SLOWNESS, StatusEffects.POISON, StatusEffects.INSTANT_HEALTH);

  @GameTest(templateName = FiringRangeSupport.TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void noControlArrowAppliesAnEffectVanillaAlreadySellsAsATippedArrow(TestContext context) {
    final CowEntity target = ControlTestSupport.stillCowAt(context, TARGET_STAND);

    for (final RegisteredArrow<?> arrow : CONTROL_ARROWS) {
      MockPlayerSupport.fireEastStraight(
          context,
          MockPlayerSupport.playerAt(context, FiringRangeSupport.SHOOTER_STAND),
          arrow.item());
    }

    context.runAtTick(
        FiringRangeSupport.LANDING_TICK,
        () -> {
          assertNoneOfTheForbiddenEffects(context, target);
          context.complete();
        });
  }

  private static void assertNoneOfTheForbiddenEffects(
      final TestContext context, final LivingEntity target) {
    for (final RegistryEntry<StatusEffect> effect : VANILLA_TIPPED_ARROW_EFFECTS) {
      context.assertTrue(
          target.getStatusEffect(effect) == null,
          "No control arrow may apply "
              + effect.getIdAsString()
              + ", which vanilla already sells as a tipped arrow");
    }
  }
}
