package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ChaosArrows;
import com.grahambartley.notenougharrows.chaos.PufferInflation;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class PufferArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "puffer-arrow";
  private static final String DISABLED_BATCH = "puffer-arrow-disabled";
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);

  @BeforeBatch(batchId = DISABLED_BATCH)
  public void switchThePufferOffBeforeBatch(ServerWorld world) {
    ChaosTestSupport.useChaos(chaos -> chaos.withPuffer(chaos.puffer().withEnabled(false)));
  }

  @AfterBatch(batchId = DISABLED_BATCH)
  public void restoreDefaultConfigAfterDisabledBatch(ServerWorld world) {
    ServerConfigHolder.reset();
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aPufferArrowInflatesWhatItStrikesWithoutHurtingIt(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.PUFFER_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertTrue(PufferInflation.isInflated(cow), "The cow should be inflated");
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          context.assertTrue(
              FiringRangeSupport.arrowWasSpent(context), "The puffer arrow should be spent");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = DISABLED_BATCH,
      tickLimit = TerrainArrowTestSupport.TICK_LIMIT)
  public void aDisabledPufferArrowHitsLikeAnArrowAndInflatesNothing(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    TerrainArrowTestSupport.fireFromBow(context, ChaosArrows.PUFFER_ARROW.item());

    context.runAtTick(
        TerrainArrowTestSupport.SETTLED_TICK,
        () -> {
          context.assertFalse(PufferInflation.isInflated(cow), "The cow should be unchanged");
          context.assertTrue(cow.getHealth() < health, "A switched off puffer arrow hurts");
          context.complete();
        });
  }
}
