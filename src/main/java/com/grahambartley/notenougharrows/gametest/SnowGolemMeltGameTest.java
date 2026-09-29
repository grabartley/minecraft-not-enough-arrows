package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.social.SnowGolemBuild;
import com.grahambartley.notenougharrows.social.SnowGolemMelt;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class SnowGolemMeltGameTest implements FabricGameTest {
  private static final String BATCH = "snow-golem-melt";
  private static final BlockPos OPEN_GROUND = new BlockPos(8, 3, 8);
  private static final int SHORT_LIFETIME = 20;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT_LIFETIME + 20)
  public void aGolemStandsUntilItsTimeRunsOut(TestContext context) {
    final SnowGolemEntity golem = built(context);

    context.runAtTick(
        SHORT_LIFETIME / 2,
        () -> {
          context.assertTrue(golem.isAlive(), "The golem should still stand halfway through");
          context.assertTrue(
              SnowGolemMelt.isTracked(context.getWorld(), golem.getUuid()), "and be counted down");
        });
    context.runAtTick(
        SHORT_LIFETIME + 5,
        () -> {
          context.assertTrue(golem.isRemoved(), "The golem should have melted");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aGolemLoadedAfterItsTimeMeltsAtOnce(TestContext context) {
    final SnowGolemEntity golem = EntityType.SNOW_GOLEM.create(context.getWorld());
    final Vec3d at = Vec3d.ofBottomCenter(context.getAbsolutePos(OPEN_GROUND));
    golem.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
    SnowGolemMelt.schedule(golem, context.getWorld().getTime() - 1);
    context.getWorld().spawnEntity(golem);

    context.runAtTick(
        3,
        () -> {
          context.assertTrue(golem.isRemoved(), "An overdue golem melts as soon as it loads");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aSavedGolemKeepsItsMeltingTime(TestContext context) {
    final SnowGolemEntity golem = built(context);
    final long meltsAt = SnowGolemMelt.meltsAt(golem).orElseThrow();
    final NbtCompound saved = new NbtCompound();
    golem.saveNbt(saved);
    golem.discard();

    final Optional<Entity> reloaded = EntityType.getEntityFromNbt(saved, context.getWorld());

    context.assertTrue(reloaded.isPresent(), "A saved golem should load again");
    context.assertEquals(
        meltsAt, SnowGolemMelt.meltsAt(reloaded.get()).orElseThrow(), "Its melting time");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT_LIFETIME + 20)
  public void anOrdinaryGolemIsLeftAlone(TestContext context) {
    final SnowGolemEntity golem = context.spawnEntity(EntityType.SNOW_GOLEM, OPEN_GROUND);

    context.runAtTick(
        SHORT_LIFETIME + 5,
        () -> {
          context.assertFalse(
              SnowGolemMelt.isTracked(context.getWorld(), golem.getUuid()), "Not counted down");
          context.assertTrue(golem.isAlive(), "A golem built by hand never melts on a timer");
          context.complete();
        });
  }

  private static SnowGolemEntity built(final TestContext context) {
    return SnowGolemBuild.build(
            context.getWorld(),
            Vec3d.ofBottomCenter(context.getAbsolutePos(OPEN_GROUND)),
            null,
            SHORT_LIFETIME)
        .orElseThrow();
  }
}
