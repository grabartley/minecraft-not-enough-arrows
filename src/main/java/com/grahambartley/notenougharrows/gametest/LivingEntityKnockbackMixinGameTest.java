package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.PufferInflation;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class LivingEntityKnockbackMixinGameTest implements FabricGameTest {
  private static final String BATCH = "puffer-knockback";
  private static final double STRENGTH = 0.4;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anInflatedTargetIsKnockedFurtherThanAnOrdinaryOne(TestContext context) {
    final ZombieEntity ordinary = still(context, new BlockPos(8, 3, 4));
    final ZombieEntity inflated = still(context, new BlockPos(8, 3, 12));
    PufferInflation.inflate(inflated);

    ordinary.takeKnockback(STRENGTH, 1.0, 0.0);
    inflated.takeKnockback(STRENGTH, 1.0, 0.0);

    final double ordinaryPush = Math.abs(ordinary.getVelocity().x);
    final double inflatedPush = Math.abs(inflated.getVelocity().x);
    context.assertTrue(ordinaryPush > 0.0, "The ordinary zombie should be knocked back");
    context.assertTrue(
        inflatedPush > ordinaryPush * 1.5,
        "The inflated zombie should fly further, but moved "
            + inflatedPush
            + " against "
            + ordinaryPush);
    context.complete();
  }

  private static ZombieEntity still(final TestContext context, final BlockPos at) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, at);
    zombie.setAiDisabled(true);
    zombie.setVelocity(Vec3d.ZERO);
    return zombie;
  }
}
