package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.world.SpawnedDrops;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public final class SpawnedDropsGameTest implements FabricGameTest {
  private static final String BATCH = "spawned-drops";
  private static final BlockPos CENTER = TerrainTestSupport.CENTER;

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void catchesOnlyWhatTheActionDropped(TestContext context) {
    final ItemEntity alreadyThere = context.spawnItem(Items.STONE, CENTER);
    final ItemEntity[] dropped = new ItemEntity[1];

    final List<ItemEntity> caught =
        SpawnedDrops.during(
            context.getWorld(),
            around(context),
            () -> dropped[0] = context.spawnItem(Items.DIAMOND, CENTER));

    context.assertEquals(caught, List.of(dropped[0]), "Drops caught");
    context.assertFalse(caught.contains(alreadyThere), "What was lying there before is not ours");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void anActionThatDropsNothingCatchesNothing(TestContext context) {
    context.spawnItem(Items.STONE, CENTER);

    context.assertTrue(
        SpawnedDrops.during(context.getWorld(), around(context), () -> {}).isEmpty(),
        "Nothing new");
    context.complete();
  }

  @GameTest(
      templateName = TerrainTestSupport.TEMPLATE,
      batchId = BATCH,
      tickLimit = TerrainTestSupport.TICK_LIMIT)
  public void aDropOutsideTheBoxIsNotCaught(TestContext context) {
    final List<ItemEntity> caught =
        SpawnedDrops.during(
            context.getWorld(),
            new Box(context.getAbsolutePos(CENTER)),
            () -> context.spawnItem(Items.DIAMOND, CENTER.east(3)));

    context.assertTrue(caught.isEmpty(), "The diamond fell outside the box");
    context.complete();
  }

  private static Box around(final TestContext context) {
    return new Box(context.getAbsolutePos(CENTER)).expand(2.0);
  }
}
