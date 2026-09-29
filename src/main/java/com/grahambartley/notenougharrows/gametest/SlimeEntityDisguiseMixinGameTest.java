package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.disguise.DisguiseService;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class SlimeEntityDisguiseMixinGameTest implements FabricGameTest {
  private static final String BATCH = "disguise-slime";
  private static final BlockPos PLAYER = new BlockPos(8, 3, 8);
  private static final int BIG = 4;
  private static final int LONG = 2000;
  private static final int PAST_JOIN_PROTECTION = 70;

  @GameTest(
      templateName = MobArena.TEMPLATE,
      batchId = BATCH,
      tickLimit = PAST_JOIN_PROTECTION + 20)
  public void aDisguisedSlimeDoesNotHurtAPlayerItTouches(TestContext context) {
    final ServerPlayerEntity player = ChaosTestSupport.sturdyPlayerAt(context, PLAYER);
    final SlimeEntity slime = bigSlime(context);
    DisguiseService.disguise(context.getWorld(), slime, LONG);

    context.runAtTick(
        PAST_JOIN_PROTECTION,
        () -> {
          slime.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0f, 0f);
          slime.onPlayerCollision(player);
          context.assertEquals(player.getMaxHealth(), player.getHealth(), "The player's health");
          DisguiseService.revert(context.getWorld(), slime);
          context.complete();
        });
  }

  @GameTest(
      templateName = MobArena.TEMPLATE,
      batchId = BATCH,
      tickLimit = PAST_JOIN_PROTECTION + 20)
  public void anOrdinarySlimeStillHurtsAPlayerItTouches(TestContext context) {
    final ServerPlayerEntity player = ChaosTestSupport.sturdyPlayerAt(context, PLAYER);
    final SlimeEntity slime = bigSlime(context);

    context.runAtTick(
        PAST_JOIN_PROTECTION,
        () -> {
          slime.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0f, 0f);
          slime.onPlayerCollision(player);
          context.assertTrue(player.getHealth() < player.getMaxHealth(), "The slime should hurt");
          context.complete();
        });
  }

  private static SlimeEntity bigSlime(final TestContext context) {
    final SlimeEntity slime = context.spawnMob(EntityType.SLIME, PLAYER);
    slime.setSize(BIG, true);
    return slime;
  }
}
