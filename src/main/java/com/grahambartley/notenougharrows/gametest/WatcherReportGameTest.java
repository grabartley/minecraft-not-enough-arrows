package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.reveal.WatcherAlarm;
import com.grahambartley.notenougharrows.reveal.WatcherReport;
import java.util.Arrays;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class WatcherReportGameTest implements FabricGameTest {
  private static final String BATCH = "watcher-report";
  private static final BlockPos WIRE = new BlockPos(40, 2, 8);
  private static final BlockPos OWNER_STAND = new BlockPos(3, 2, 8);

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void namesWhatCrossedARoughDistanceAndADirection(TestContext context) {
    final ServerPlayerEntity owner = ownerAt(context, OWNER_STAND);
    final CowEntity cow = context.spawnEntity(EntityType.COW, WIRE);

    final TranslatableTextContent report =
        translatable(WatcherReport.of(alarm(context, owner, cow), owner));

    context.assertEquals(
        "message.not-enough-arrows.tripwire.report", report.getKey(), "Report key");
    final Object[] args = report.getArgs();
    context.assertEquals(cow.getType().getName(), args[0], "What crossed");
    context.assertEquals(40, args[1], "Rough distance to a watcher 37 blocks east");
    context.assertEquals(
        "direction.not-enough-arrows.east", translatable((Text) args[2]).getKey(), "Direction");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void neverGivesCoordinates(TestContext context) {
    final ServerPlayerEntity owner = ownerAt(context, OWNER_STAND);
    final CowEntity cow = context.spawnEntity(EntityType.COW, WIRE);
    final BlockPos wire = context.getAbsolutePos(WIRE);

    final String spoken = WatcherReport.of(alarm(context, owner, cow), owner).getString();

    context.assertFalse(
        Arrays.stream(new int[] {wire.getX(), wire.getY(), wire.getZ()})
            .anyMatch(coordinate -> spoken.contains(Integer.toString(coordinate))),
        "A report never names a coordinate: " + spoken);
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.ARENA, batchId = BATCH, tickLimit = 10)
  public void anOwnerInAnotherDimensionIsToldOnlyThatItHappened(TestContext context) {
    final ServerPlayerEntity owner = ownerAt(context, OWNER_STAND);
    final CowEntity cow = context.spawnEntity(EntityType.COW, WIRE);
    final TranslatableTextContent report;
    owner.setServerWorld(context.getWorld().getServer().getWorld(World.NETHER));
    try {
      report = translatable(WatcherReport.of(alarm(context, owner, cow), owner));
    } finally {
      owner.setServerWorld(context.getWorld());
    }

    context.assertEquals(
        "message.not-enough-arrows.tripwire.report.elsewhere", report.getKey(), "Report key");
    context.assertEquals(1, report.getArgs().length, "Only what crossed is named");
    context.complete();
  }

  private static ServerPlayerEntity ownerAt(final TestContext context, final BlockPos relative) {
    final ServerPlayerEntity owner = context.createMockCreativeServerPlayerInWorld();
    MockPlayerSupport.moveTo(context, owner, Vec3d.ofBottomCenter(relative));
    return owner;
  }

  private static WatcherAlarm alarm(
      final TestContext context, final ServerPlayerEntity owner, final CowEntity crosser) {
    return new WatcherAlarm(owner.getUuid(), context.getAbsolutePos(WIRE), crosser);
  }

  private static TranslatableTextContent translatable(final Text text) {
    return (TranslatableTextContent) text.getContent();
  }
}
