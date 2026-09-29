package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.chaos.PufferInflation;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class PufferInflationGameTest implements FabricGameTest {
  private static final String BATCH = "puffer-inflation";
  private static final BlockPos STAND = new BlockPos(8, 3, 8);
  private static final int SHAFT_WATCH_TICKS = 80;
  private static final double EPSILON = 1.0E-6;

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aTargetInTheOpenDoublesAndNoLongerFitsAOneBlockGap(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);

    context.assertEquals(Optional.of(2.0), PufferInflation.inflate(zombie), "Inflation");
    context.assertTrue(PufferInflation.isInflated(zombie), "The zombie should be inflated");
    context.assertTrue(
        Math.abs(zombie.getScale() - 2.0f) < EPSILON, "The zombie should be twice its size");
    context.runAtTick(
        2,
        () -> {
          context.assertTrue(
              zombie.getWidth() > 1.0f, "An inflated zombie is too wide for a one-block gap");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aTargetInAOneBlockGapIsNotInflatedAtAll(TestContext context) {
    wallIn(context, STAND, 2);
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);

    context.assertTrue(PufferInflation.inflate(zombie).isEmpty(), "No room to grow");
    context.assertFalse(PufferInflation.isInflated(zombie), "The zombie should be unchanged");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = SHAFT_WATCH_TICKS + 20)
  public void aTargetInATightShaftGrowsOnlyAsFarAsItFitsAndNeverSuffocates(TestContext context) {
    wallIn(context, STAND, 3);
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    zombie.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
    final float health = zombie.getHealth();

    final Optional<Double> factor = PufferInflation.inflate(zombie);
    context.assertEquals(Optional.of(1.5), factor, "The largest size that fits the shaft");

    context.runAtTick(
        SHAFT_WATCH_TICKS,
        () -> {
          context.assertFalse(zombie.isInsideWall(), "The zombie should not be in a wall");
          context.assertEquals(health, zombie.getHealth(), "The zombie's health");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void deflatingRestoresTheOriginalSize(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    final float width = zombie.getWidth();
    PufferInflation.inflate(zombie);

    context.runAtTick(
        2,
        () -> {
          context.assertTrue(zombie.getWidth() > width, "Inflated first");
          context.assertTrue(PufferInflation.deflate(zombie), "Deflation");
          context.assertFalse(PufferInflation.isInflated(zombie), "No longer inflated");
          context.assertFalse(PufferInflation.deflate(zombie), "Nothing left to deflate");
        });
    context.runAtTick(
        4,
        () -> {
          context.assertTrue(Math.abs(zombie.getWidth() - width) < EPSILON, "Width restored");
          context.complete();
        });
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void anInflatedTargetIsNotInflatedTwice(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);
    PufferInflation.inflate(zombie);

    context.assertTrue(PufferInflation.inflate(zombie).isEmpty(), "No second inflation");
    context.assertTrue(Math.abs(zombie.getScale() - 2.0f) < EPSILON, "Still twice its size");
    context.complete();
  }

  @GameTest(templateName = MobArena.TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void onlyAnInflatedTargetTakesExtraKnockback(TestContext context) {
    final ZombieEntity zombie = context.spawnMob(EntityType.ZOMBIE, STAND);

    context.assertEquals(0.4, PufferInflation.knockbackFor(zombie, 0.4), "Ordinary knockback");
    PufferInflation.inflate(zombie);
    context.assertEquals(
        0.4 * PufferInflation.KNOCKBACK_MULTIPLIER,
        PufferInflation.knockbackFor(zombie, 0.4),
        "Inflated knockback");
    context.complete();
  }

  private static void wallIn(final TestContext context, final BlockPos stand, final int height) {
    for (int up = 0; up < height; up++) {
      for (final Direction side : Direction.Type.HORIZONTAL) {
        context.setBlockState(stand.up(up).offset(side), Blocks.STONE);
      }
    }
    context.setBlockState(stand.up(height), Blocks.STONE);
  }
}
