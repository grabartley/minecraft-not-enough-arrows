package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.StinkCloudService;
import com.grahambartley.notenougharrows.chaos.StinkRepel;
import com.grahambartley.notenougharrows.cloud.TimedCloud;
import com.grahambartley.notenougharrows.config.StinkArrowConfig;
import com.grahambartley.notenougharrows.control.MobAggression;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class StinkRepelGameTest implements FabricGameTest {
  private static final String BATCH = "stink-repel";
  private static final BlockPos PREY = new BlockPos(4, 3, 8);
  private static final BlockPos HUNTER = new BlockPos(20, 3, 8);
  private static final BlockPos CLOUD = new BlockPos(12, 3, 8);
  private static final int WATCH = 240;
  private static final int LONG_LIFETIME = WATCH + 10;

  @GameTest(
      templateName = MobArena.TEMPLATE,
      batchId = BATCH,
      tickLimit = WATCH + 20,
      required = false)
  public void aZombieHuntingAPlayerAcrossACloudNeverWalksIntoIt(TestContext context) {
    final ServerPlayerEntity prey = ChaosTestSupport.sturdyPlayerAt(context, PREY);
    final ZombieEntity hunter =
        ChaosTestSupport.shaded(context.spawnEntity(EntityType.ZOMBIE, HUNTER));
    final TimedCloud cloud = open(context);
    for (int provoking = 1; provoking < 10; provoking++) {
      context.runAtTick(provoking, () -> MobAggression.aim(hunter, prey));
    }
    context.runAtEveryTick(
        () ->
            MobArena.check(
                context,
                !cloud.contains(hunter.getBoundingBox().getCenter()),
                "A zombie should never step into a stink cloud, but it stood at "
                    + context.getRelative(hunter.getPos())));
    context.runAtTick(
        WATCH,
        () -> {
          context.assertTrue(
              hunter.getTarget() == prey, "The zombie should still be hunting the player");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 100)
  public void aZombieCaughtInsideACloudIsPushedOut(TestContext context) {
    final ZombieEntity zombie =
        ChaosTestSupport.shaded(context.spawnEntity(EntityType.ZOMBIE, CLOUD));
    final TimedCloud cloud = open(context);

    context.runAtTick(
        80,
        () -> {
          context.assertFalse(
              cloud.contains(zombie.getBoundingBox().getCenter()),
              "The zombie should have been pushed out of the cloud");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPathThroughTheCloudIsRecognisedAndOneAroundItIsNot(TestContext context) {
    final TimedCloud cloud = new TimedCloud(Vec3d.ofCenter(BlockPos.ORIGIN), 3.0, 100L);

    context.assertTrue(
        StinkRepel.headsInto(pathAlong(new BlockPos(-6, 0, 0), new BlockPos(6, 0, 0)), cloud),
        "A straight path through the middle enters the cloud");
    context.assertFalse(
        StinkRepel.headsInto(pathAlong(new BlockPos(-6, 0, 6), new BlockPos(6, 0, 6)), cloud),
        "A path six blocks to the side stays out");
    context.assertFalse(StinkRepel.headsInto(null, cloud), "No path, nowhere to go");
    context.complete();
  }

  private static TimedCloud open(final TestContext context) {
    final Vec3d centre = Vec3d.ofCenter(context.getAbsolutePos(CLOUD));
    StinkCloudService.open(
        context.getWorld(),
        centre,
        StinkArrowConfig.defaults().withCloudLifetimeTicks(LONG_LIFETIME));
    return new TimedCloud(centre, StinkCloudService.RADIUS, Long.MAX_VALUE);
  }

  private static Path pathAlong(final BlockPos from, final BlockPos to) {
    final List<PathNode> nodes =
        IntStream.rangeClosed(from.getX(), to.getX())
            .mapToObj(x -> new PathNode(x, from.getY(), from.getZ()))
            .toList();
    return new Path(nodes, to, true);
  }
}
