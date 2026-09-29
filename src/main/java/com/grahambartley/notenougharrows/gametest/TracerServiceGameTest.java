package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.discovery.TracerPath;
import com.grahambartley.notenougharrows.discovery.TracerService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class TracerServiceGameTest implements FabricGameTest {
  private static final String BATCH = "tracer-service";
  private static final BlockPos SPOT = new BlockPos(3, 2, 3);

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aPathIsSentToTheShooter(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    context.assertTrue(
        TracerService.draw(carrier(context), twoPoints(), shooter, 100).contains(shooter),
        "The shooter sees their own path");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aZeroLifetimeDrawsNothing(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    context.assertTrue(
        TracerService.draw(carrier(context), twoPoints(), shooter, 0).isEmpty(),
        "Nobody is sent a path with no lifetime");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aSinglePointIsNoPath(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final TracerPath dot = new TracerPath();
    dot.record(Vec3d.ZERO);

    context.assertTrue(
        TracerService.draw(carrier(context), dot, shooter, 100).isEmpty(),
        "Nobody is sent a single point");
    context.complete();
  }

  private static CowEntity carrier(final TestContext context) {
    return context.spawnEntity(EntityType.COW, SPOT);
  }

  private static TracerPath twoPoints() {
    final TracerPath path = new TracerPath();
    path.record(Vec3d.ZERO);
    path.record(new Vec3d(4, 1, 0));
    return path;
  }
}
