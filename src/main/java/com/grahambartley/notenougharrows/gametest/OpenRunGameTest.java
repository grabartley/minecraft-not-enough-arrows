package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import com.grahambartley.notenougharrows.traversal.OpenRun;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public final class OpenRunGameTest implements FabricGameTest {
  private static final String BATCH = "open-run";
  private static final BlockState PLANKS = Blocks.OAK_PLANKS.getDefaultState();
  private static final List<BlockPos> RUN =
      List.of(
          new BlockPos(1, 3, 3),
          new BlockPos(2, 3, 3),
          new BlockPos(3, 3, 3),
          new BlockPos(4, 3, 3));

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeBatch(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anOpenRunTakesEveryPositionWhileTheWayIsClear(TestContext context) {
    context.assertEquals(4, leading(context).size(), "Open positions");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anOpenRunStopsAtASolidBlock(TestContext context) {
    context.setBlockState(RUN.get(2), Blocks.STONE);

    context.assertEquals(2, leading(context).size(), "Open positions");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anOpenRunStopsAtWater(TestContext context) {
    context.setBlockState(RUN.get(1), Blocks.WATER);

    context.assertEquals(1, leading(context).size(), "Open positions");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anOpenRunGoesThroughGrass(TestContext context) {
    context.setBlockState(RUN.get(1).down(), Blocks.DIRT);
    context.setBlockState(RUN.get(1), Blocks.SHORT_GRASS);

    context.assertEquals(4, leading(context).size(), "Open positions");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anOpenRunStopsAtSomethingStandingInIt(TestContext context) {
    context.spawnEntity(EntityType.ARMOR_STAND, RUN.get(2)).setNoGravity(true);

    context.assertEquals(2, leading(context).size(), "Open positions");
    context.complete();
  }

  @GameTest(templateName = TraversalTestSupport.TEMPLATE, batchId = BATCH, tickLimit = 10)
  public void anOpenRunStopsAtAnotherStructure(TestContext context) {
    TimedStructureService.build(
        context.getWorld(),
        null,
        List.of(context.getAbsolutePos(RUN.get(3))),
        StructureBlockSource.of(Blocks.COBWEB.getDefaultState()),
        StructureBudget.of(1),
        400);

    context.assertEquals(3, leading(context).size(), "Open positions");
    context.complete();
  }

  private static List<BlockPos> leading(final TestContext context) {
    return OpenRun.leading(
        context.getWorld(), RUN.stream().map(context::getAbsolutePos).toList(), PLANKS);
  }
}
