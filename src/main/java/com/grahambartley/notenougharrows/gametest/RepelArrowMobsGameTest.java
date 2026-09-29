package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.MobAggression;
import com.grahambartley.notenougharrows.gametest.MobRoster.Mob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.math.Vec3d;

public final class RepelArrowMobsGameTest implements FabricGameTest {
  private static final int WATCH = 100;
  private static final double ARENA_END = 42.0;
  private static final int SAMPLE_EVERY = 5;
  private static final int WINDOW_SAMPLES = 4;
  private static final double GOT_AWAY = 1.0;
  private static final Set<EntityType<?>> FLEES_AWAITING_A_FIX = Set.of(EntityType.ELDER_GUARDIAN);
  private static final double KEEPS_AWAY_FROM = TargetingArrowConfig.defaults().repelDistance();

  @CustomTestProvider
  public Collection<TestFunction> everyMobThatMovesRunsWithoutTurningBack() {
    return MobArena.perMob(
        "repel",
        "flees",
        mob -> mob.movesAround() && !FLEES_AWAITING_A_FIX.contains(mob.type()),
        MobArena.LONG_LIMIT,
        RepelArrowMobsGameTest::flees);
  }

  private static void flees(final TestContext context, final Mob mob) {
    final ServerPlayerEntity player = MobArena.player(context);
    final MobEntity fleeing = MobArena.thinking(context, mob, MobArena.NEAR_STAND);
    final double[] start = new double[1];
    final Vec3d[] impact = new Vec3d[1];
    final double[] furthest = {Double.NEGATIVE_INFINITY};
    final List<Double> progress = new ArrayList<>();
    if (mob.fights()) {
      context.runAtTick(2, () -> MobAggression.aim(fleeing, player));
    }
    context.runAtTick(
        MobArena.SETTLED,
        () -> {
          impact[0] = player.getPos();
          start[0] = horizontalDistance(fleeing.getPos(), impact[0]);
          ControlHoldService.repel(context.getWorld(), impact[0], TargetingArrowConfig.defaults());
        });
    for (int t = MobArena.SETTLED + 10; t <= MobArena.SETTLED + WATCH; t += SAMPLE_EVERY) {
      final int tick = t;
      context.runAtTick(
          tick,
          () -> {
            if (context.getRelative(fleeing.getPos()).x >= ARENA_END) {
              return;
            }
            final double fled = horizontalDistance(fleeing.getPos(), impact[0]);
            if (mob.fights()) {
              MobArena.check(
                  context,
                  fleeing.getTarget() == null,
                  "A repelled "
                      + mob.name()
                      + " should drop its target while it runs, at tick "
                      + tick);
            }
            progress.add(fled);
            final int latest = progress.size() - 1;
            if (latest >= WINDOW_SAMPLES && mob.moves() != MobRoster.Moves.AIR) {
              if (progress.get(latest - WINDOW_SAMPLES) >= KEEPS_AWAY_FROM) {
                MobArena.check(
                    context,
                    fled >= KEEPS_AWAY_FROM - fleeing.getWidth() / 2.0,
                    "A repelled "
                        + mob.name()
                        + " should keep away once it has fled "
                        + KEEPS_AWAY_FROM
                        + " blocks, but came back to "
                        + fled
                        + " at tick "
                        + tick);
              } else {
                final double windowStart = progress.get(latest - WINDOW_SAMPLES);
                MobArena.check(
                    context,
                    fled >= windowStart - fleeing.getWidth() / 2.0,
                    "A repelled "
                        + mob.name()
                        + " lost ground over "
                        + WINDOW_SAMPLES * SAMPLE_EVERY
                        + " ticks, from "
                        + windowStart
                        + " to "
                        + fled
                        + " at tick "
                        + tick);
              }
            }
            furthest[0] = Math.max(furthest[0], fled);
          });
    }
    context.runAtTick(
        MobArena.SETTLED + WATCH + 1,
        () -> {
          MobArena.check(
              context,
              furthest[0] - start[0] >= GOT_AWAY,
              "A repelled "
                  + mob.name()
                  + " should get away, but only fled "
                  + (furthest[0] - start[0]));
          context.complete();
        });
  }

  private static double horizontalDistance(final Vec3d from, final Vec3d to) {
    return Math.hypot(from.x - to.x, from.z - to.z);
  }
}
