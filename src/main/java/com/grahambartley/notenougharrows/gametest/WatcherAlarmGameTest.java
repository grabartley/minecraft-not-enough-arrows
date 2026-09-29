package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.reveal.WatcherAlarm;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class WatcherAlarmGameTest implements FabricGameTest {
  private static final String BATCH = "watcher-alarm";
  private static final UUID OWNER = UUID.randomUUID();
  private static final BlockPos WIRE = new BlockPos(1, 64, 1);

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void carriesWhoToTellWhereAndWhatCrossed(TestContext context) {
    final CowEntity crosser = context.spawnEntity(EntityType.COW, new BlockPos(3, 3, 3));

    final WatcherAlarm alarm = new WatcherAlarm(OWNER, WIRE, crosser);

    context.assertEquals(OWNER, alarm.owner(), "Owner");
    context.assertEquals(WIRE, alarm.watcherPos(), "Where");
    context.assertEquals(crosser, alarm.crosser(), "What crossed");
    context.complete();
  }

  @GameTest(templateName = DiscoveryTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anAlarmAlwaysHasAnOwnerAndSomethingThatCrossed(TestContext context) {
    final CowEntity crosser = context.spawnEntity(EntityType.COW, new BlockPos(3, 3, 3));

    context.assertTrue(refused(() -> new WatcherAlarm(null, WIRE, crosser)), "No owner");
    context.assertTrue(refused(() -> new WatcherAlarm(OWNER, WIRE, null)), "Nothing crossed");
    context.complete();
  }

  private static boolean refused(final Runnable construct) {
    try {
      construct.run();
      return false;
    } catch (final NullPointerException expected) {
      return true;
    }
  }
}
