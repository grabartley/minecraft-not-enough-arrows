package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.disguise.DisguiseBroadcaster;
import com.grahambartley.notenougharrows.disguise.DisguiseService;
import com.grahambartley.notenougharrows.network.DisguisePayloads.DisguiseS2CPayload;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class DisguiseBroadcasterGameTest implements FabricGameTest {
  private static final String BATCH = "disguise-broadcaster";
  private static final BlockPos STAND = new BlockPos(8, 3, 8);
  private static final int LONG = 2000;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerWhoComesIntoRangeIsToldADisguisedMobIsDisguised(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    DisguiseService.disguise(context.getWorld(), zombie, LONG);

    final Optional<DisguiseS2CPayload> catchUp = DisguiseBroadcaster.catchUpFor(zombie);

    context.assertTrue(catchUp.isPresent(), "A late viewer should be told");
    context.assertEquals(zombie.getId(), catchUp.get().entityId(), "Which mob");
    context.assertEquals(
        DisguiseService.formOf(context.getWorld(), zombie.getUuid()),
        catchUp.get().form(),
        "Which form");
    DisguiseService.revert(context.getWorld(), zombie);
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aPlayerWhoComesIntoRangeOfAnOrdinaryMobIsToldNothing(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);

    context.assertTrue(DisguiseBroadcaster.catchUpFor(zombie).isEmpty(), "Nothing to tell");
    context.complete();
  }
}
