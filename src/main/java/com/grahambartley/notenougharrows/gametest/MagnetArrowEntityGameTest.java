package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.SocialArrows;
import com.grahambartley.notenougharrows.entity.MagnetArrowEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class MagnetArrowEntityGameTest implements FabricGameTest {
  private static final String BATCH = "magnet-arrow";
  private static final BlockPos LOOSE_ITEM = new BlockPos(4, 3, 5);
  private static final BlockPos TARGET_STAND = new BlockPos(5, 3, 3);
  private static final int PULLED_TICK = 80;
  private static final double CLOSE = 2.0;
  private static final double UNMOVED = 0.2;

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = PULLED_TICK + 20)
  public void aMagnetArrowPullsLooseItemsToItsShooterAndIsSpent(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ItemEntity item = itemAt(context, LOOSE_ITEM);
    final ServerPlayerEntity shooter = fire(context);

    context.runAtTick(
        PULLED_TICK,
        () -> {
          context.assertTrue(
              item.isRemoved() || item.getPos().distanceTo(shooter.getPos()) < CLOSE,
              "The item should end at the shooter, ended "
                  + item.getPos().distanceTo(shooter.getPos())
                  + " away");
          context.assertTrue(
              FiringRangeSupport.firedArrow(context, MagnetArrowEntity.class) == null,
              "A magnet arrow is spent on impact");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = PULLED_TICK + 20)
  public void aMagnetArrowNeitherHurtsNorMovesTheCreatureItHits(TestContext context) {
    final CowEntity cow = ControlTestSupport.sturdyStillCowAt(context, TARGET_STAND);
    final float health = cow.getHealth();
    final Vec3d where = cow.getPos();
    fire(context);

    context.runAtTick(
        PULLED_TICK,
        () -> {
          context.assertEquals(health, cow.getHealth(), "The cow's health");
          context.assertTrue(cow.getPos().distanceTo(where) < UNMOVED, "The cow stays put");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = PULLED_TICK + 20)
  public void aDispensedMagnetArrowPullsNothing(TestContext context) {
    FiringRangeSupport.raiseBackstop(context);
    final ItemEntity item = itemAt(context, LOOSE_ITEM);
    final Vec3d where = item.getPos();
    FiringRangeSupport.dispenseEast(
        context, TerrainArrowTestSupport.DISPENSER_STAND, SocialArrows.MAGNET_ARROW.item());

    context.runAtTick(
        PULLED_TICK,
        () -> {
          context.assertTrue(
              item.getPos().distanceTo(where) < UNMOVED, "With no shooter the item stays put");
          context.complete();
        });
  }

  private static ServerPlayerEntity fire(final TestContext context) {
    return SocialTestSupport.fireEast(context, new ItemStack(SocialArrows.MAGNET_ARROW.item(), 8));
  }

  private static ItemEntity itemAt(final TestContext context, final BlockPos at) {
    final Vec3d pos = Vec3d.ofBottomCenter(context.getAbsolutePos(at));
    final ItemEntity item =
        new ItemEntity(
            context.getWorld(), pos.x, pos.y, pos.z, new ItemStack(Items.EMERALD, 5), 0, 0, 0);
    context.getWorld().spawnEntity(item);
    return item;
  }
}
