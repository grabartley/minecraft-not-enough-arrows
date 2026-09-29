package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.entity.SonarArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class SonarArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "sonar-arrow";
  private static final String PIERCING_BATCH = "sonar-arrow-piercing";
  private static final BlockPos BEHIND_THE_WALL = new BlockPos(3, 2, 6);
  private static final BlockPos BESIDE_THE_FAR_WALL =
      new BlockPos(
          DiscoveryTestSupport.PIERCING_WALL_X - 1, 3, DiscoveryTestSupport.PIERCING_LANE_Z + 2);
  private static final int SMALL_RADIUS = 3;

  @BeforeBatch(batchId = PIERCING_BATCH)
  public void narrowTheSonarBeforeBatch(ServerWorld world) {
    DiscoveryTestSupport.useDiscovery(
        discovery -> discovery.withSonar(discovery.sonar().withRadius(SMALL_RADIUS)));
  }

  @AfterBatch(batchId = PIERCING_BATCH)
  public void restoreDefaultConfigAfterPiercingBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = PIERCING_BATCH, tickLimit = 40)
  public void aSonarArrowPiercingThroughACreaturePulsesOnlyWhereItFirstHit(TestContext context) {
    final ZombieEntity pierced = DiscoveryTestSupport.piercingRange(context);
    final CowEntity byTheWall = context.spawnEntity(EntityType.COW, BESIDE_THE_FAR_WALL);
    byTheWall.setAiDisabled(true);
    final PersistentProjectileEntity arrow =
        DiscoveryTestSupport.firePiercingEast(context, DiscoveryArrows.SONAR_ARROW.item());
    final double[] furthest = {arrow.getX()};
    context.runAtEveryTick(() -> furthest[0] = Math.max(furthest[0], arrow.getX()));

    context.runAtTick(
        30,
        () -> {
          context.assertTrue(
              furthest[0] > context.getAbsolute(Vec3d.ofCenter(BESIDE_THE_FAR_WALL)).x - 2,
              "The arrow should have pierced the zombie and reached the far wall");
          context.assertTrue(
              pierced.hasStatusEffect(StatusEffects.GLOWING), "It pulsed where it first hit");
          context.assertFalse(
              byTheWall.hasStatusEffect(StatusEffects.GLOWING),
              "It must not pulse a second time when it reaches the wall");
          context.complete();
        });
  }

  @GameTest(
      templateName = DiscoveryTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aSonarArrowOutlinesACreatureItDidNotHitButNotItsShooter(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final CowEntity hidden = context.spawnEntity(EntityType.COW, BEHIND_THE_WALL);
    hidden.setAiDisabled(true);
    final ServerPlayerEntity shooter =
        TerrainArrowTestSupport.fireFromBow(context, DiscoveryArrows.SONAR_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(
              hidden.hasStatusEffect(StatusEffects.GLOWING), "The cow it never hit is outlined");
          context.assertFalse(
              shooter.hasStatusEffect(StatusEffects.GLOWING), "The shooter is never outlined");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, SonarArrowEntity.class) == null,
              "A sonar arrow is spent on impact");
          context.complete();
        });
  }
}
