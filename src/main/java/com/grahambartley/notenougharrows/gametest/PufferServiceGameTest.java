package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.PufferInflation;
import com.grahambartley.notenougharrows.chaos.PufferService;
import com.grahambartley.notenougharrows.config.PufferArrowConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class PufferServiceGameTest implements FabricGameTest {
  private static final String BATCH = "puffer-service";
  private static final BlockPos STAND = new BlockPos(8, 3, 8);
  private static final int SHORT = 20;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT + 20)
  public void anInflationIsRevertedWhenItsTimeIsUp(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    context.assertTrue(
        PufferService.inflate(context.getWorld(), zombie, lasting(SHORT)), "Inflated");

    context.runAtTick(
        SHORT - 5, () -> context.assertTrue(PufferInflation.isInflated(zombie), "Still inflated"));
    context.runAtTick(
        SHORT + 5,
        () -> {
          context.assertFalse(PufferInflation.isInflated(zombie), "Deflated on expiry");
          context.assertFalse(
              PufferService.isTracked(context.getWorld(), zombie.getUuid()), "Forgotten");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHORT * 3)
  public void aSecondHitExtendsTheInflation(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    PufferService.inflate(context.getWorld(), zombie, lasting(SHORT));
    context.runAtTick(
        SHORT - 5,
        () ->
            context.assertTrue(
                PufferService.inflate(context.getWorld(), zombie, lasting(SHORT)),
                "The second hit refreshes the inflation"));
    context.runAtTick(
        SHORT + 5, () -> context.assertTrue(PufferInflation.isInflated(zombie), "Still inflated"));
    context.runAtTick(
        SHORT * 2 + 5,
        () -> {
          context.assertFalse(PufferInflation.isInflated(zombie), "Deflated after the refresh");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anUnloadedTargetIsDeflatedAndForgotten(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    PufferService.inflate(context.getWorld(), zombie, lasting(SHORT * 10));

    zombie.discard();

    context.assertFalse(PufferInflation.isInflated(zombie), "Deflated on unload");
    context.assertFalse(
        PufferService.isTracked(context.getWorld(), zombie.getUuid()), "Forgotten on unload");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aZeroDurationInflatesNothing(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);

    context.assertFalse(
        PufferService.inflate(context.getWorld(), zombie, lasting(0)), "Nothing inflated");
    context.assertFalse(PufferInflation.isInflated(zombie), "The zombie is unchanged");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anInflationIsNotSavedWithTheEntity(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    PufferService.inflate(context.getWorld(), zombie, lasting(SHORT * 10));

    final ZombieEntity reloaded = EntityType.ZOMBIE.create(context.getWorld());
    reloaded.readNbt(zombie.writeNbt(new NbtCompound()));

    context.assertFalse(
        PufferInflation.isInflated(reloaded), "A saved and reloaded zombie is its normal size");
    context.complete();
  }

  private static PufferArrowConfig lasting(final int ticks) {
    return PufferArrowConfig.defaults().withDurationTicks(ticks);
  }
}
