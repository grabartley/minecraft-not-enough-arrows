package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.entity.BaseArrowEntity;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.grapple.GrappleSession;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class GrappleArrowEntityGameTest implements FabricGameTest {
  private static final String TEMPLATE = "more-arrows:fire_pad";

  private static final BlockPos SHOOTER_STAND = new BlockPos(0, 3, 0);
  private static final BlockPos WALL_BASE = new BlockPos(6, 3, 0);
  private static final int WALL_HEIGHT = 3;
  private static final float EASTWARD_YAW = 270.0f;
  private static final float LEVEL_PITCH = 0.0f;
  private static final float BOW_SPEED = 3.0f;
  private static final int FULLY_DRAWN = 0;
  private static final int LANDING_TICK = 20;

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void anArrowFiredFromABowGrapplesTheShooterToTheBlockItLandsIn(TestContext context) {
    raiseWall(context);
    final ServerPlayerEntity shooter = MockPlayerSupport.playerAt(context, SHOOTER_STAND);
    fireFromBow(context, shooter);

    context.runAtTick(
        LANDING_TICK,
        () -> {
          final GrappleSession session =
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid());

          context.assertTrue(
              session != null,
              "An arrow fired from a bow into a block should grapple the player who fired it");
          final BlockPos wall = context.getAbsolutePos(WALL_BASE);
          context.assertEquals(
              session.anchor().getX(), wall.getX(), "Column the grapple took hold in");
          context.assertEquals(
              session.anchor().getZ(), wall.getZ(), "Row the grapple took hold in");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = MockPlayerSupport.BATCH, tickLimit = 60)
  public void anArrowFiredWithNoPlayerBehindItGrapplesNobody(TestContext context) {
    raiseWall(context);
    final PigEntity shooter = context.spawnEntity(EntityType.PIG, SHOOTER_STAND);
    shooter.setAiDisabled(true);
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);
    fireFromBowHeldBy(context, shooter);

    context.runAtTick(
        LANDING_TICK,
        () -> {
          context.assertTrue(
              GrappleService.sessionOf(context.getWorld(), shooter.getUuid()) == null,
              "An arrow with no player behind it should pull nothing");
          context.assertTrue(
              AnchorService.anchorOf(context.getWorld(), shooter.getUuid()) == null,
              "An arrow with no player behind it should take no anchor");
          context.complete();
        });
  }

  private static void raiseWall(final TestContext context) {
    for (int height = 0; height < WALL_HEIGHT; height++) {
      context.setBlockState(WALL_BASE.up(height), Blocks.STONE);
    }
  }

  private static void fireFromBow(final TestContext context, final ServerPlayerEntity shooter) {
    shooter.setYaw(EASTWARD_YAW);
    shooter.setPitch(LEVEL_PITCH);
    shooter.getInventory().setStack(0, new ItemStack(ModArrows.GRAPPLE_ARROW.item(), 8));

    final ItemStack bow = new ItemStack(Items.BOW);
    Items.BOW.onStoppedUsing(bow, context.getWorld(), shooter, FULLY_DRAWN);
  }

  private static void fireFromBowHeldBy(final TestContext context, final PigEntity shooter) {
    final BaseArrowEntity arrow =
        (BaseArrowEntity)
            ModArrows.GRAPPLE_ARROW
                .item()
                .createArrow(
                    context.getWorld(),
                    new ItemStack(ModArrows.GRAPPLE_ARROW.item()),
                    shooter,
                    new ItemStack(Items.BOW));
    arrow.setVelocity(shooter, shooter.getPitch(), shooter.getYaw(), 0.0f, BOW_SPEED, 0.0f);
    context.getWorld().spawnEntity(arrow);
  }
}
