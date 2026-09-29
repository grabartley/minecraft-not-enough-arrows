package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.reveal.EntityRevealPulse;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class EntityRevealPulseGameTest implements FabricGameTest {
  private static final String BATCH = "entity-reveal-pulse";
  private static final BlockPos CENTER = new BlockPos(3, 2, 8);
  private static final int RADIUS = 6;
  private static final int DURATION_TICKS = 100;

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void outlinesEveryLivingThingInRangeWithTheGlowInkArrowsGlow(TestContext context) {
    final CowEntity near = stillCow(context, CENTER.east(4));
    final CowEntity far = stillCow(context, CENTER.east(RADIUS + 3));

    final List<LivingEntity> found = fire(context, null, DURATION_TICKS);

    context.assertTrue(found.contains(near), "The cow in range is found");
    context.assertFalse(found.contains(far), "The cow out of range is not");
    context.assertTrue(near.hasStatusEffect(StatusEffects.GLOWING), "The cow in range glows");
    context.assertFalse(far.hasStatusEffect(StatusEffects.GLOWING), "The far cow does not glow");
    context.assertEquals(
        DURATION_TICKS, near.getStatusEffect(StatusEffects.GLOWING).getDuration(), "Glow duration");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void neverOutlinesTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, shooter, Vec3d.ofBottomCenter(CENTER.east(1)));
    final ServerPlayerEntity bystander = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, bystander, Vec3d.ofBottomCenter(CENTER.east(2)));

    final List<LivingEntity> found = fire(context, shooter, DURATION_TICKS);

    context.assertTrue(found.contains(bystander), "Another player in range is outlined");
    context.assertFalse(found.contains(shooter), "The shooter is never outlined");
    context.assertFalse(
        shooter.hasStatusEffect(StatusEffects.GLOWING), "The shooter does not glow");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void aZeroDurationOutlinesNothing(TestContext context) {
    final CowEntity cow = stillCow(context, CENTER.east(2));

    context.assertTrue(fire(context, null, 0).isEmpty(), "A zero duration finds nothing");
    context.assertFalse(cow.hasStatusEffect(StatusEffects.GLOWING), "Nothing glows");
    context.complete();
  }

  private static CowEntity stillCow(final TestContext context, final BlockPos relative) {
    final CowEntity cow = context.spawnEntity(EntityType.COW, relative);
    cow.setAiDisabled(true);
    return cow;
  }

  private static List<LivingEntity> fire(
      final TestContext context, final PlayerEntity shooter, final int durationTicks) {
    return EntityRevealPulse.fire(
        context.getWorld(),
        Vec3d.ofBottomCenter(context.getAbsolutePos(CENTER)),
        RADIUS,
        durationTicks,
        null,
        shooter);
  }
}
