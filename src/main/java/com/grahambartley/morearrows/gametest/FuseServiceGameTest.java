package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.config.ExplosiveArrowConfig;
import com.grahambartley.morearrows.config.ExplosiveTierConfig;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.config.ServerConfigHolder;
import com.grahambartley.morearrows.fuse.Fuse;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class FuseServiceGameTest implements FabricGameTest {
  private static final String BATCH = "fuse-countdown";
  private static final String FORGET_BATCH = "fuse-forget";
  private static final String MUTED_BEEP_BATCH = "fuse-muted-beep";
  private static final String TIER_DELAY_BATCH = "fuse-tier-delay";
  private static final String TEMPLATE = "more-arrows:fire_pad";

  private static final BlockPos IMPACT = new BlockPos(3, 2, 3);
  private static final BlockPos DRIFT = new BlockPos(5, 2, 5);
  private static final int SHORT_DELAY_TICKS = 10;
  private static final int LONG_DELAY_TICKS = 100;
  private static final int LIGHT_TICK = 5;

  private static final Map<UUID, BlockPos> DETONATIONS = new ConcurrentHashMap<>();

  static {
    FuseService.whenExpired(
        (world, host, fuse) -> DETONATIONS.put(fuse.hostId(), host.getBlockPos()));
  }

  @BeforeBatch(batchId = BATCH)
  public void forgetEveryFuseBeforeBatch(ServerWorld world) {
    startClean();
  }

  @BeforeBatch(batchId = FORGET_BATCH)
  public void forgetEveryFuseBeforeForgetBatch(ServerWorld world) {
    startClean();
  }

  @BeforeBatch(batchId = MUTED_BEEP_BATCH)
  public void muteTheBeepBeforeBatch(ServerWorld world) {
    startClean();
    ServerConfigHolder.set(
        MoreArrowsConfig.defaults()
            .withExplosive(ExplosiveArrowConfig.defaults().withBeepVolume(0.0f)));
  }

  @AfterBatch(batchId = MUTED_BEEP_BATCH)
  public void restoreDefaultConfigAfterMutedBeepBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @BeforeBatch(batchId = TIER_DELAY_BATCH)
  public void setTheTierDelayBeforeBatch(ServerWorld world) {
    startClean();
    ServerConfigHolder.set(configWithTntDelay(SHORT_DELAY_TICKS));
  }

  @AfterBatch(batchId = TIER_DELAY_BATCH)
  public void restoreDefaultConfigAfterTierDelayBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  private static void startClean() {
    FuseService.forget();
    DETONATIONS.clear();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFuseEmbeddedInABlockSignalsOnceItsDelayHasBurnedDown(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());

    context.runAtTick(
        LIGHT_TICK,
        () -> {
          final Fuse fuse = FuseService.light(context.getWorld(), arrow, SHORT_DELAY_TICKS);
          context.assertTrue(fuse != null, "Lighting a delayed fuse should hand one back");
          context.assertTrue(
              FuseService.fuseOn(context.getWorld(), arrow.getUuid()) != null,
              "A burning fuse should be tracked against the host carrying it");

          context.runAtTick(
              LIGHT_TICK + SHORT_DELAY_TICKS + 10,
              () -> {
                assertDetonatedAt(context, arrow.getUuid(), IMPACT);
                context.assertTrue(
                    FuseService.fuseOn(context.getWorld(), arrow.getUuid()) == null,
                    "A fuse that has signalled should no longer be tracked");
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFuseStaysQuietUntilItsDelayHasBurnedDown(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());

    context.runAtTick(
        LIGHT_TICK,
        () -> {
          FuseService.light(context.getWorld(), arrow, LONG_DELAY_TICKS);

          context.runAtTick(
              LIGHT_TICK + 20,
              () -> {
                context.assertFalse(
                    DETONATIONS.containsKey(arrow.getUuid()),
                    "A fuse should not signal before its delay has burned down");
                context.assertTrue(
                    FuseService.fuseOn(context.getWorld(), arrow.getUuid()) != null,
                    "A fuse mid-countdown should still be tracked");
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFuseCarriedByAMobSignalsWhereThatMobEndedUp(TestContext context) {
    final PigEntity carrier = context.spawnEntity(EntityType.PIG, IMPACT);
    carrier.setAiDisabled(true);

    context.runAtTick(
        LIGHT_TICK,
        () -> {
          FuseService.light(context.getWorld(), carrier, SHORT_DELAY_TICKS);

          context.runAtTick(LIGHT_TICK + 3, () -> moveTo(context, carrier, DRIFT));
          context.runAtTick(
              LIGHT_TICK + SHORT_DELAY_TICKS + 10,
              () -> {
                assertDetonatedAt(context, carrier.getUuid(), DRIFT);
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aCarrierThatDiesMidCountdownTakesItsFuseWithIt(TestContext context) {
    final PigEntity carrier = context.spawnEntity(EntityType.PIG, IMPACT);
    carrier.setAiDisabled(true);

    context.runAtTick(
        LIGHT_TICK,
        () -> {
          FuseService.light(context.getWorld(), carrier, SHORT_DELAY_TICKS);

          context.runAtTick(LIGHT_TICK + 2, carrier::kill);
          context.runAtTick(
              LIGHT_TICK + SHORT_DELAY_TICKS + 10,
              () -> {
                context.assertFalse(
                    DETONATIONS.containsKey(carrier.getUuid()),
                    "A fuse whose carrier died should never signal");
                context.assertTrue(
                    FuseService.fuseOn(context.getWorld(), carrier.getUuid()) == null,
                    "A fuse whose carrier died should leave nothing tracked behind it");
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 60)
  public void aFuseWhoseHostLeavesTheWorldHoldsRatherThanSignalling(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());

    context.runAtTick(
        LIGHT_TICK,
        () -> {
          FuseService.light(context.getWorld(), arrow, SHORT_DELAY_TICKS);

          context.runAtTick(LIGHT_TICK + 2, arrow::discard);
          context.runAtTick(
              LIGHT_TICK + SHORT_DELAY_TICKS + 10,
              () -> {
                context.assertFalse(
                    DETONATIONS.containsKey(arrow.getUuid()),
                    "A fuse with no host to burn on should never signal");

                final Fuse held = FuseService.fuseOn(context.getWorld(), arrow.getUuid());
                context.assertTrue(held != null, "A fuse should wait for its host to come back");
                context.assertTrue(
                    held.lostTicks() > 0, "A fuse with no host should be counting it as lost");
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aDelayOfZeroSignalsOnContact(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());

    context.assertTrue(
        FuseService.light(context.getWorld(), arrow, 0) == null,
        "A fuse with no delay should leave nothing burning");
    assertDetonatedAt(context, arrow.getUuid(), IMPACT.up());
    context.assertTrue(
        FuseService.fuseOn(context.getWorld(), arrow.getUuid()) == null,
        "A fuse with no delay should never be tracked");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = MUTED_BEEP_BATCH, tickLimit = 60)
  public void mutingTheBeepStillLetsTheFuseBurnDown(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());

    context.runAtTick(
        LIGHT_TICK,
        () -> {
          FuseService.light(context.getWorld(), arrow, SHORT_DELAY_TICKS);

          context.runAtTick(
              LIGHT_TICK + SHORT_DELAY_TICKS + 10,
              () -> {
                assertDetonatedAt(context, arrow.getUuid(), IMPACT);
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = TIER_DELAY_BATCH, tickLimit = 20)
  public void theLiveServerConfigDecidesHowLongATierBurnsFor(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());

    final Fuse fuse =
        FuseService.light(
            context.getWorld(), arrow, ServerConfigService.get().explosive().tnt().delayTicks());

    context.assertTrue(fuse != null, "A configured tier delay should light a fuse");
    context.assertEquals(
        fuse.delayTicks(), SHORT_DELAY_TICKS, "Ticks the configured tier delay burns for");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = FORGET_BATCH, tickLimit = 20)
  public void forgettingEveryFuseLeavesNothingBurning(TestContext context) {
    final ArrowEntity arrow = context.spawnEntity(EntityType.ARROW, IMPACT.up());
    FuseService.light(context.getWorld(), arrow, LONG_DELAY_TICKS);

    FuseService.forget();

    context.assertTrue(
        FuseService.fusesIn(context.getWorld()).isEmpty(),
        "Stopping the server should leave no fuse burning");
    context.complete();
  }

  private static void moveTo(
      final TestContext context, final PigEntity carrier, final BlockPos relativePos) {
    final Vec3d target = context.getAbsolute(Vec3d.ofBottomCenter(relativePos));
    carrier.refreshPositionAndAngles(target.getX(), target.getY(), target.getZ(), 0f, 0f);
    carrier.setVelocity(Vec3d.ZERO);
  }

  private static void assertDetonatedAt(
      final TestContext context, final UUID hostId, final BlockPos relativePos) {
    final BlockPos detonation = DETONATIONS.get(hostId);
    context.assertTrue(detonation != null, "An expired fuse should signal that it expired");

    final BlockPos expected = context.getAbsolutePos(relativePos);
    context.assertEquals(detonation.getX(), expected.getX(), "Column the fuse signalled from");
    context.assertEquals(detonation.getZ(), expected.getZ(), "Row the fuse signalled from");
    context.assertTrue(
        Math.abs(detonation.getY() - expected.getY()) <= 1,
        "A fuse should signal from its host, not from the world origin, but signalled at "
            + detonation
            + " rather than "
            + expected);
  }

  private static MoreArrowsConfig configWithTntDelay(final int delayTicks) {
    final ExplosiveArrowConfig explosive = ExplosiveArrowConfig.defaults();
    return MoreArrowsConfig.defaults()
        .withExplosive(
            explosive.withTnt(new ExplosiveTierConfig(delayTicks, explosive.tnt().power())));
  }
}
