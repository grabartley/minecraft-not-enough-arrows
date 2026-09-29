package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.entity.CourierArrowEntity;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class PersistentProjectileEntityDeliveryMixinGameTest implements FabricGameTest {
  private static final String PVP_OFF_BATCH = "courier-delivery-pvp-off";
  private static final String TEAMMATES_BATCH = "courier-delivery-teammates";
  private static final String TEAM = "nea_courier_friends";
  private static final BlockPos RECIPIENT_STAND = new BlockPos(4, 3, 3);

  @BeforeBatch(batchId = PVP_OFF_BATCH)
  public void turnPvpOffBeforeBatch(ServerWorld world) {
    world.getServer().setPvpEnabled(false);
  }

  @AfterBatch(batchId = PVP_OFF_BATCH)
  public void leavePvpOffAfterBatch(ServerWorld world) {
    world.getServer().setPvpEnabled(false);
  }

  @BeforeBatch(batchId = TEAMMATES_BATCH)
  public void turnPvpOnBeforeBatch(ServerWorld world) {
    world.getServer().setPvpEnabled(true);
  }

  @AfterBatch(batchId = TEAMMATES_BATCH)
  public void restorePvpAfterBatch(ServerWorld world) {
    world.getServer().setPvpEnabled(false);
    final Scoreboard scoreboard = world.getScoreboard();
    final Team team = scoreboard.getTeam(TEAM);
    if (team != null) {
      scoreboard.removeTeam(team);
    }
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = PVP_OFF_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aLoadedCourierArrowReachesAPlayerWithPvpOff(TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    SocialTestSupport.fireEast(context, SocialTestSupport.loadedCourier());

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              SocialTestSupport.PAYLOAD_COUNT,
              recipient.getInventory().count(SocialTestSupport.PAYLOAD_ITEM),
              "Diamonds delivered with PvP off");
          context.complete();
        });
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = PVP_OFF_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aPlainArrowStillPassesThroughAPlayerWithPvpOff(TestContext context) {
    assertPassesThrough(context, new ItemStack(Items.ARROW), ArrowEntity.class);
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = PVP_OFF_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void anEmptyCourierArrowStillPassesThroughAPlayerWithPvpOff(TestContext context) {
    assertPassesThrough(context, SocialTestSupport.emptyCourier(), CourierArrowEntity.class);
  }

  @GameTest(
      templateName = FiringRangeSupport.TEMPLATE,
      batchId = TEAMMATES_BATCH,
      tickLimit = SocialTestSupport.TICK_LIMIT)
  public void aLoadedCourierArrowReachesATeammateWithoutFriendlyFire(TestContext context) {
    final ServerPlayerEntity recipient = recipientAt(context);
    final ServerPlayerEntity shooter =
        ChaosTestSupport.survivalPlayerAt(context, FiringRangeSupport.SHOOTER_STAND);
    final Scoreboard scoreboard = context.getWorld().getScoreboard();
    final Team team =
        scoreboard.getTeam(TEAM) != null ? scoreboard.getTeam(TEAM) : scoreboard.addTeam(TEAM);
    team.setFriendlyFireAllowed(false);
    scoreboard.addScoreHolderToTeam(shooter.getNameForScoreboard(), team);
    scoreboard.addScoreHolderToTeam(recipient.getNameForScoreboard(), team);
    shooter.getInventory().clear();
    MockPlayerSupport.fireEastFromBow(context, shooter, SocialTestSupport.loadedCourier());
    PhysicsArrowTestSupport.stepOutOfTheLane(context, shooter);

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          context.assertEquals(
              SocialTestSupport.PAYLOAD_COUNT,
              recipient.getInventory().count(SocialTestSupport.PAYLOAD_ITEM),
              "Diamonds delivered to a teammate");
          context.complete();
        });
  }

  private static <E extends PersistentProjectileEntity> void assertPassesThrough(
      final TestContext context, final ItemStack arrow, final Class<E> type) {
    FiringRangeSupport.raiseBackstop(context);
    final ServerPlayerEntity recipient = recipientAt(context);
    SocialTestSupport.fireEast(context, arrow);

    context.runAtTick(
        SocialTestSupport.SETTLED_TICK,
        () -> {
          final List<E> fired =
              context.getWorld().getEntitiesByClass(type, context.getTestBox(), it -> true);
          context.assertEquals(1, fired.size(), "Arrows in the range");
          context.assertTrue(
              fired.get(0).getX() > recipient.getX() + 0.5,
              "With PvP off the arrow should fly through the player into the wall behind");
          context.complete();
        });
  }

  private static ServerPlayerEntity recipientAt(final TestContext context) {
    final ServerPlayerEntity recipient =
        ChaosTestSupport.survivalPlayerAt(context, RECIPIENT_STAND);
    recipient.getInventory().clear();
    return recipient;
  }
}
