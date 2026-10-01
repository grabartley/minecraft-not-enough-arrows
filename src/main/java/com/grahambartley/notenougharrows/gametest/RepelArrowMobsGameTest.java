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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RepelArrowMobsGameTest implements FabricGameTest {
  private static final int FIRST_RUN = 290;
  private static final int SECOND_RUN = 150;
  private static final double ARENA_END = 42.0;
  private static final double IMPACT_OFFSET = 4.0;
  private static final BlockPos OUT_OF_THE_WAY = new BlockPos(44, 3, 2);
  private static final int FIRST_LOOK = 10;
  private static final int SAMPLE_EVERY = 5;
  private static final int WINDOW_SAMPLES = 4;
  private static final double SLOW_GAIN = 2.0;
  private static final double TURNED_BACK = 1.5;
  private static final double DRIFT = 1.5;
  private static final TargetingArrowConfig LONG_ENOUGH_TO_GET_CLEAR =
      TargetingArrowConfig.defaults().withRepelDurationTicks(FIRST_RUN + 10);
  private static final double KEEPS_AWAY_FROM = TargetingArrowConfig.defaults().repelDistance();
  private static final Set<EntityType<?>> PASSES_THROUGH_WALLS = Set.of(EntityType.VEX);
  private static final Set<EntityType<?>> TOO_SLOW_TO_GET_CLEAR =
      Set.of(EntityType.CAMEL, EntityType.MAGMA_CUBE, EntityType.PANDA, EntityType.TURTLE);

  @CustomTestProvider
  public Collection<TestFunction> everyMobThatMovesRunsWithoutTurningBack() {
    return MobArena.perMob(
        "repel",
        "flees",
        MobRoster.Mob::movesAround,
        MobArena.SLOW_LIMIT,
        RepelArrowMobsGameTest::flees);
  }

  private static void flees(final TestContext context, final Mob mob) {
    final ServerPlayerEntity player = MobArena.player(context);
    final MobEntity fleeing = MobArena.thinking(context, mob, MobArena.NEAR_STAND);
    final Flight first = new Flight(context, mob, fleeing, "first", true);
    final Flight second = new Flight(context, mob, fleeing, "second", false);
    final int turn = MobArena.SETTLED + FIRST_RUN;
    if (mob.fights()) {
      context.runAtTick(2, () -> MobAggression.aim(fleeing, player));
    }
    first.repelFrom(MobArena.SETTLED, -IMPACT_OFFSET, LONG_ENOUGH_TO_GET_CLEAR);
    first.watchUntil(turn);
    context.runAtTick(
        turn + 1,
        () -> {
          if (MobRoster.outgrowsTheArena(mob)) {
            return;
          }
          if (TOO_SLOW_TO_GET_CLEAR.contains(mob.type())) {
            first.checkGained(SLOW_GAIN);
          } else {
            first.checkReached(KEEPS_AWAY_FROM);
          }
        });
    context.runAtTick(
        turn,
        () -> MockPlayerSupport.moveTo(context, player, Vec3d.ofBottomCenter(OUT_OF_THE_WAY)));
    second.repelFrom(turn, IMPACT_OFFSET, TargetingArrowConfig.defaults());
    second.watchUntil(turn + SECOND_RUN);
    context.runAtTick(
        turn + SECOND_RUN + 1,
        () -> {
          if (second.startedInTheArena() && !MobRoster.outgrowsTheArena(mob)) {
            second.checkGained(TURNED_BACK);
          }
          context.complete();
        });
  }

  private static boolean isInTheArena(final Vec3d relative) {
    return relative.x >= 0.0
        && relative.x < ARENA_END
        && relative.z >= 0.0
        && relative.z < MobArena.ARENA_WIDTH;
  }

  private static double horizontalDistance(final Vec3d from, final Vec3d to) {
    return Math.hypot(from.x - to.x, from.z - to.z);
  }

  private static final class Flight {
    private final TestContext context;
    private final Mob mob;
    private final MobEntity fleeing;
    private final String name;
    private final boolean keepsAway;
    private final List<Double> progress = new ArrayList<>();
    private Vec3d impact;
    private double start;
    private double furthest = Double.NEGATIVE_INFINITY;
    private int from;
    private boolean startedInTheArena;

    Flight(
        final TestContext context,
        final Mob mob,
        final MobEntity fleeing,
        final String name,
        final boolean keepsAway) {
      this.context = context;
      this.mob = mob;
      this.fleeing = fleeing;
      this.name = name;
      this.keepsAway = keepsAway;
    }

    void repelFrom(
        final int tick, final double offsetAlongX, final TargetingArrowConfig targeting) {
      from = tick;
      context.runAtTick(
          tick,
          () -> {
            startedInTheArena = isInTheArena(context.getRelative(fleeing.getPos()));
            impact = fleeing.getPos().add(offsetAlongX, 0.0, 0.0);
            start = horizontalDistance(fleeing.getPos(), impact);
            ControlHoldService.repel(context.getWorld(), impact, targeting);
          });
    }

    void watchUntil(final int last) {
      for (int t = from + FIRST_LOOK; t <= last; t += SAMPLE_EVERY) {
        final int tick = t;
        context.runAtTick(tick, () -> sample(tick));
      }
    }

    boolean startedInTheArena() {
      return startedInTheArena;
    }

    private void sample(final int tick) {
      final double fled = horizontalDistance(fleeing.getPos(), impact);
      final boolean inTheArena = isInTheArena(context.getRelative(fleeing.getPos()));
      if (inTheArena || PASSES_THROUGH_WALLS.contains(mob.type())) {
        furthest = Math.max(furthest, fled);
      }
      if (!inTheArena) {
        return;
      }
      if (mob.fights()) {
        MobArena.check(
            context,
            fleeing.getTarget() == null,
            "A repelled " + mob.name() + " should drop its target while it runs, at tick " + tick);
      }
      progress.add(fled);
      final int latest = progress.size() - 1;
      if (latest >= WINDOW_SAMPLES && mob.moves() != MobRoster.Moves.AIR) {
        final double windowStart = progress.get(latest - WINDOW_SAMPLES);
        final double slack = fleeing.getWidth() / 2.0;
        if (windowStart >= KEEPS_AWAY_FROM) {
          if (!keepsAway) {
            return;
          }
          MobArena.check(
              context,
              fled >= KEEPS_AWAY_FROM - Math.max(slack, DRIFT),
              "A repelled "
                  + mob.name()
                  + " should keep away once it has fled "
                  + KEEPS_AWAY_FROM
                  + " blocks, but came back to "
                  + fled
                  + " on its "
                  + name
                  + " run at tick "
                  + tick);
        } else {
          MobArena.check(
              context,
              fled >= windowStart - slack,
              "A repelled "
                  + mob.name()
                  + " lost ground over "
                  + WINDOW_SAMPLES * SAMPLE_EVERY
                  + " ticks, from "
                  + windowStart
                  + " to "
                  + fled
                  + " on its "
                  + name
                  + " run at tick "
                  + tick);
        }
      }
    }

    void checkReached(final double distance) {
      MobArena.check(
          context,
          furthest >= distance,
          "A repelled "
              + mob.name()
              + " should run "
              + distance
              + " blocks from the impact, but only reached "
              + furthest
              + " on its "
              + name
              + " run");
    }

    void checkGained(final double gain) {
      MobArena.check(
          context,
          furthest - start >= gain,
          "A repelled "
              + mob.name()
              + " should run at least "
              + gain
              + " blocks from the impact, but only fled "
              + (furthest - start)
              + " on its "
              + name
              + " run");
    }
  }
}
