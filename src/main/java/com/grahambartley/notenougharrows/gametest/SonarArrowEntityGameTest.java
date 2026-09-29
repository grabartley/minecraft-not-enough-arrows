package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.DiscoveryArrows;
import com.grahambartley.notenougharrows.entity.SonarArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class SonarArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "sonar-arrow";
  private static final BlockPos BEHIND_THE_WALL = new BlockPos(3, 2, 6);

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
