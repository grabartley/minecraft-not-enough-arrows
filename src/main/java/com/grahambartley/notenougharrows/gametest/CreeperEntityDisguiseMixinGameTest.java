package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.disguise.DisguiseService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class CreeperEntityDisguiseMixinGameTest implements FabricGameTest {
  private static final String BATCH = "disguise-creeper";
  private static final BlockPos STAND = new BlockPos(8, 3, 8);
  private static final int PAST_THE_FUSE = 60;
  private static final int LONG = 2000;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = PAST_THE_FUSE + 20)
  public void aLitCreeperDoesNotExplodeWhileDisguised(TestContext context) {
    final CreeperEntity creeper = context.spawnEntity(EntityType.CREEPER, STAND);
    DisguiseService.disguise(context.getWorld(), creeper, LONG);
    creeper.ignite();

    context.runAtTick(
        PAST_THE_FUSE,
        () -> {
          context.assertTrue(creeper.isAlive(), "The disguised creeper should not have exploded");
          context.assertTrue(creeper.isIgnited(), "It is still lit underneath");
          DisguiseService.revert(context.getWorld(), creeper);
          creeper.discard();
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = PAST_THE_FUSE + 20)
  public void aLitCreeperExplodesOnceItsDisguiseEnds(TestContext context) {
    final CreeperEntity creeper = context.spawnEntity(EntityType.CREEPER, STAND);
    DisguiseService.disguise(context.getWorld(), creeper, 10);
    creeper.ignite();

    context.runAtTick(
        PAST_THE_FUSE,
        () -> {
          context.assertFalse(creeper.isAlive(), "The creeper's own fuse should finish the job");
          context.complete();
        });
  }
}
