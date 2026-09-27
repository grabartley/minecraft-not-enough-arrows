package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FIRST;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.LINE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.LONG_LIFETIME_TICKS;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.PLANKS;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.SECOND;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.SHORT_LIFETIME_TICKS;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.TEMPLATE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.THIRD;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.absolute;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.buildLine;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.chunkAt;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.reloadChunksOf;

import com.grahambartley.notenougharrows.structure.StructureBlockSource;
import com.grahambartley.notenougharrows.structure.StructureBudget;
import com.grahambartley.notenougharrows.structure.StructureChunkMarks;
import com.grahambartley.notenougharrows.structure.TimedStructure;
import com.grahambartley.notenougharrows.structure.TimedStructureService;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public final class TimedStructureServiceGameTest implements FabricGameTest {
  private static final String BATCH = "timed-structure-lifecycle";
  private static final String RESTART_BATCH = "timed-structure-restart";
  private static final String CRASH_BATCH = "timed-structure-unclean-shutdown";
  private static final String UNLOAD_BATCH = "timed-structure-chunk-unload";
  private static final StructureBlockSource GLASS =
      StructureBlockSource.of(Blocks.GLASS.getDefaultState());

  @BeforeBatch(batchId = BATCH)
  public void forgetStructuresBeforeLifecycle(ServerWorld world) {
    TimedStructureService.forget();
  }

  @BeforeBatch(batchId = RESTART_BATCH)
  public void forgetStructuresBeforeRestart(ServerWorld world) {
    TimedStructureService.forget();
  }

  @BeforeBatch(batchId = CRASH_BATCH)
  public void forgetStructuresBeforeCrash(ServerWorld world) {
    TimedStructureService.forget();
  }

  @BeforeBatch(batchId = UNLOAD_BATCH)
  public void forgetStructuresBeforeUnload(ServerWorld world) {
    TimedStructureService.forget();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStructureRecordsEveryPositionItPlaced(TestContext context) {
    final TimedStructure structure = buildLine(context, LONG_LIFETIME_TICKS).orElseThrow();

    context.assertEquals(structure.positions(), absolute(context, LINE), "Recorded positions");
    context.assertEquals(
        structure.expiryTick(),
        context.getWorld().getTime() + LONG_LIFETIME_TICKS,
        "Expiry tick of a freshly placed structure");
    absolute(context, LINE)
        .forEach(
            pos ->
                context.assertTrue(
                    TimedStructureService.holds(context.getWorld(), pos),
                    "Every placed position should be held by the structure"));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStructureIsRecordedAgainstItsShooter(TestContext context) {
    final PlayerEntity shooter = MockPlayerSupport.playerAt(context, new BlockPos(3, 3, 1));

    final TimedStructure structure =
        TimedStructureService.build(
                context.getWorld(),
                shooter,
                absolute(context, LINE),
                PLANKS,
                StructureBudget.of(LINE.size()),
                LONG_LIFETIME_TICKS)
            .orElseThrow();

    context.assertEquals(structure.owner(), shooter.getUuid(), "Owner of the structure");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aStructureStandsUntilItsLifetimeEnds(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          buildLine(context, LONG_LIFETIME_TICKS);
          context.runAtTick(
              30,
              () -> {
                LINE.forEach(pos -> context.expectBlock(Blocks.OAK_PLANKS, pos));
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aStructureIsRemovedTogetherOnceItsLifetimeEnds(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          buildLine(context, SHORT_LIFETIME_TICKS);
          context.runAtTick(
              5 + SHORT_LIFETIME_TICKS + 2,
              () -> {
                LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
                context.assertFalse(
                    TimedStructureService.holds(context.getWorld(), context.getAbsolutePos(FIRST)),
                    "An expired structure should hold nothing");
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aLifetimeOfZeroPlacesNothing(TestContext context) {
    context.assertTrue(buildLine(context, 0).isEmpty(), "A zero lifetime should place nothing");
    LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aBudgetOfZeroPlacesNothing(TestContext context) {
    final Optional<TimedStructure> structure =
        TimedStructureService.build(
            context.getWorld(),
            null,
            absolute(context, LINE),
            PLANKS,
            StructureBudget.of(0),
            LONG_LIFETIME_TICKS);

    context.assertTrue(structure.isEmpty(), "A zero budget should place nothing");
    LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aStructureIsNotBuiltOverOneThatStillStands(TestContext context) {
    TimedStructureService.build(
        context.getWorld(),
        null,
        absolute(context, SECOND),
        StructureBlockSource.of(Blocks.SNOW.getDefaultState()),
        StructureBudget.of(1),
        LONG_LIFETIME_TICKS);

    final Optional<TimedStructure> second =
        TimedStructureService.build(
            context.getWorld(),
            null,
            absolute(context, SECOND),
            PLANKS,
            StructureBudget.of(1),
            LONG_LIFETIME_TICKS);

    context.assertTrue(second.isEmpty(), "A held position should not be taken again");
    context.expectBlock(Blocks.SNOW, SECOND);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void expiryLeavesABlockSomebodyBuiltOverTheStructure(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          buildLine(context, SHORT_LIFETIME_TICKS);
          context.setBlockState(SECOND, Blocks.AIR);
          context.setBlockState(SECOND, Blocks.OAK_PLANKS);
          context.setBlockState(THIRD, Blocks.STONE);
          context.runAtTick(
              5 + SHORT_LIFETIME_TICKS + 2,
              () -> {
                context.expectBlock(Blocks.AIR, FIRST);
                context.expectBlock(Blocks.OAK_PLANKS, SECOND);
                context.expectBlock(Blocks.STONE, THIRD);
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void miningABlockOutOfAStructureDropsItAndTakesItOutOfTheRecord(TestContext context) {
    final ServerPlayerEntity miner = context.createMockCreativeServerPlayerInWorld();
    miner.changeGameMode(GameMode.SURVIVAL);
    final BlockPos mined = context.getAbsolutePos(SECOND);

    context.runAtTick(
        5,
        () -> {
          buildLine(context, SHORT_LIFETIME_TICKS);
          context.assertTrue(
              miner.interactionManager.tryBreakBlock(mined), "The miner should break the block");
          context.expectItemAt(Items.OAK_PLANKS, SECOND, 2.0);
          context.assertFalse(
              TimedStructureService.holds(context.getWorld(), mined),
              "A mined position should leave the structure's record");
          context.setBlockState(SECOND, Blocks.OAK_PLANKS);
          context.runAtTick(
              5 + SHORT_LIFETIME_TICKS + 2,
              () -> {
                context.expectBlock(Blocks.AIR, FIRST);
                context.expectBlock(Blocks.OAK_PLANKS, SECOND);
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aPlayerStandingInsideAnExpiringStructureIsLeftExactlyWhereTheyWere(
      TestContext context) {
    final BlockPos feet = new BlockPos(3, 3, 5);
    final ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
    player.changeGameMode(GameMode.SURVIVAL);
    MockPlayerSupport.moveTo(context, player, Vec3d.ofBottomCenter(feet));
    final Vec3d[] before = new Vec3d[1];
    final float[] health = new float[1];

    context.runAtTick(
        5,
        () ->
            TimedStructureService.build(
                context.getWorld(),
                null,
                absolute(context, feet, feet.up()),
                GLASS,
                StructureBudget.of(2),
                SHORT_LIFETIME_TICKS));
    context.runAtTick(
        5 + SHORT_LIFETIME_TICKS - 1,
        () -> {
          before[0] = player.getPos();
          health[0] = player.getHealth();
        });
    context.runAtTick(
        5 + SHORT_LIFETIME_TICKS + 2,
        () -> {
          context.expectBlock(Blocks.AIR, feet);
          context.expectBlock(Blocks.AIR, feet.up());
          context.assertEquals(player.getPos(), before[0], "Position of the player inside");
          context.assertEquals(player.getHealth(), health[0], "Health of the player inside");
          context.assertTrue(player.isAlive(), "The player inside should be alive");
          context.complete();
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 40)
  public void aChunkLoadingWhileItsStructureStandsKeepsIt(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          buildLine(context, LONG_LIFETIME_TICKS);
          reloadChunksOf(context, LINE);
          context.runAtTick(
              10,
              () -> {
                LINE.forEach(pos -> context.expectBlock(Blocks.OAK_PLANKS, pos));
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = RESTART_BATCH, tickLimit = 20)
  public void stoppingTheServerClearsEveryStructureBeforeTheWorldSaves(TestContext context) {
    buildLine(context, LONG_LIFETIME_TICKS);

    TimedStructureService.clearAll(context.getWorld());

    LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
    context.assertTrue(
        StructureChunkMarks.in(chunkAt(context, FIRST)).isEmpty(),
        "A cleared structure should leave no marks to save");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = CRASH_BATCH, tickLimit = 40)
  public void aStructureLeftByAnUncleanShutdownIsClearedAsItsChunkLoads(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          buildLine(context, LONG_LIFETIME_TICKS);
          TimedStructureService.forget();
          reloadChunksOf(context, LINE);
          context.runAtTick(
              10,
              () -> {
                LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
                context.complete();
              });
        });
  }

  @GameTest(templateName = TEMPLATE, batchId = UNLOAD_BATCH, tickLimit = 40)
  public void aStructureThatExpiredWhileItsChunkWasAwayIsClearedAsItLoads(TestContext context) {
    context.runAtTick(
        5,
        () -> {
          final TimedStructure structure = buildLine(context, LONG_LIFETIME_TICKS).orElseThrow();
          TimedStructureService.expireIn(context.getWorld(), structure.expiryTick(), pos -> false);
          LINE.forEach(pos -> context.expectBlock(Blocks.OAK_PLANKS, pos));
          context.assertFalse(
              TimedStructureService.holds(context.getWorld(), structure.positions().get(0)),
              "An expired structure should leave the record even when it could not be removed");

          reloadChunksOf(context, LINE);
          context.runAtTick(
              10,
              () -> {
                LINE.forEach(pos -> context.expectBlock(Blocks.AIR, pos));
                context.complete();
              });
        });
  }
}
