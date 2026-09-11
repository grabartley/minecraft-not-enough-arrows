package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.config.UtilityArrowConfig;
import com.grahambartley.notenougharrows.glow.GlowService;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class GlowServiceGameTest implements FabricGameTest {
  private static final String BATCH = "glow-marking";
  private static final String TEMPLATE = "not-enough-arrows:fire_pad";
  private static final BlockPos TARGET_STAND = new BlockPos(3, 2, 3);
  private static final int DURATION_TICKS = 120;

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aLivingEntityIsMarkedWithGlowing(TestContext context) {
    final ArmorStandEntity target = target(context);

    context.assertTrue(
        GlowService.mark(context.getWorld(), target, null, DURATION_TICKS),
        "Marking a living entity should apply the glowing effect");
    context.expectEntityHasEffect(target, StatusEffects.GLOWING, 0);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMarkCarriesTheDurationItWasGiven(TestContext context) {
    final ArmorStandEntity target = target(context);

    GlowService.mark(context.getWorld(), target, null, DURATION_TICKS);

    final StatusEffectInstance glow = target.getStatusEffect(StatusEffects.GLOWING);
    context.assertTrue(glow != null, "A marked entity should carry a glowing effect");
    context.assertEquals(
        glow.getDuration(), DURATION_TICKS, "Glow duration applied to the marked entity");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anEntityWithoutStatusEffectsIsLeftAloneRatherThanErroring(TestContext context) {
    final BoatEntity target = context.spawnEntity(EntityType.BOAT, TARGET_STAND);

    context.assertFalse(
        GlowService.mark(context.getWorld(), target, null, DURATION_TICKS),
        "A boat has no status effects, so marking it should report nothing was marked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void nothingIsMarkedWhenThereIsNoTarget(TestContext context) {
    context.assertFalse(
        GlowService.mark(context.getWorld(), null, null, DURATION_TICKS),
        "Marking nothing should report nothing was marked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDurationOfZeroMarksNothingAtAll(TestContext context) {
    final ArmorStandEntity target = target(context);

    context.assertFalse(
        GlowService.mark(context.getWorld(), target, null, 0),
        "A zero duration should mark nothing");
    context.assertTrue(
        target.getStatusEffect(StatusEffects.GLOWING) == null,
        "A zero duration should leave the target unmarked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesHowLongAMarkLasts(TestContext context) {
    final ArmorStandEntity target = target(context);
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    try {
      ServerConfigHolder.set(configWithGlowDuration(DURATION_TICKS));
      GlowService.mark(context.getWorld(), target, null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    final StatusEffectInstance glow = target.getStatusEffect(StatusEffects.GLOWING);
    context.assertTrue(glow != null, "The configured duration should have marked the target");
    context.assertEquals(
        glow.getDuration(), DURATION_TICKS, "Glow duration taken from the live server config");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void theLiveServerConfigCanSwitchMarkingOff(TestContext context) {
    final ArmorStandEntity target = target(context);
    final NotEnoughArrowsConfig previous = ServerConfigService.get();
    final boolean marked;
    try {
      ServerConfigHolder.set(configWithGlowDuration(0));
      marked = GlowService.mark(context.getWorld(), target, null);
    } finally {
      ServerConfigHolder.set(previous);
    }

    context.assertFalse(marked, "A configured duration of zero should mark nothing");
    context.complete();
  }

  private static ArmorStandEntity target(final TestContext context) {
    return context.spawnEntity(EntityType.ARMOR_STAND, TARGET_STAND);
  }

  private static NotEnoughArrowsConfig configWithGlowDuration(final int durationTicks) {
    return NotEnoughArrowsConfig.defaults()
        .withUtility(UtilityArrowConfig.defaults().withGlowDurationTicks(durationTicks));
  }
}
