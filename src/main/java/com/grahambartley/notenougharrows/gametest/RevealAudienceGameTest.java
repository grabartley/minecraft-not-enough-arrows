package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.reveal.RevealAudience;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class RevealAudienceGameTest implements FabricGameTest {
  private static final String BATCH = "reveal-audience";
  private static final BlockPos SPOT = new BlockPos(3, 2, 3);

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void theShooterIsAlwaysTold(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();

    context.assertTrue(
        RevealAudience.around(context.getWorld(), context.getAbsolutePos(SPOT), shooter)
            .contains(shooter),
        "The shooter hears about their own reveal");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void theShooterIsToldOnceEvenWhenAlsoWatching(TestContext context) {
    final ServerPlayerEntity shooter = context.createMockCreativeServerPlayerInWorld();
    final CowEntity cow = context.spawnEntity(EntityType.COW, SPOT);

    context.assertEquals(
        1L,
        RevealAudience.watching(cow, shooter).stream().filter(shooter::equals).count(),
        "Times the shooter is told");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void aMobShooterIsNotAnAudience(TestContext context) {
    final CowEntity cow = context.spawnEntity(EntityType.COW, SPOT);

    context.assertTrue(
        RevealAudience.around(context.getWorld(), context.getAbsolutePos(SPOT), cow).stream()
            .noneMatch(player -> player.getUuid().equals(cow.getUuid())),
        "Only players are told");
    context.complete();
  }
}
