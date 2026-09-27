package com.grahambartley.notenougharrows.gametest;

import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.FIRST;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.SECOND;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.TEMPLATE;
import static com.grahambartley.notenougharrows.gametest.StructureTestSupport.chunkAt;

import com.grahambartley.notenougharrows.structure.StructureBlock;
import com.grahambartley.notenougharrows.structure.StructureChunkMarks;
import com.grahambartley.notenougharrows.structure.StructureMark;
import com.grahambartley.notenougharrows.structure.StructureMarks;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.storage.StorageKey;

public final class StructureChunkMarksGameTest implements FabricGameTest {
  private static final String BATCH = "structure-chunk-marks";

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMarkNamesTheBlockAndTheStructure(TestContext context) {
    final UUID id = UUID.randomUUID();
    final BlockPos pos = context.getAbsolutePos(FIRST);
    final WorldChunk chunk = chunkAt(context, FIRST);

    StructureChunkMarks.mark(chunk, new StructureBlock(pos, Blocks.GLASS.getDefaultState()), id);

    final StructureMark mark = StructureChunkMarks.at(chunk, pos).orElseThrow();
    context.assertEquals(mark.block(), Registries.BLOCK.getId(Blocks.GLASS), "Marked block");
    context.assertTrue(mark.belongsTo(id), "The mark should name its structure");
    StructureChunkMarks.unmark(chunk, pos);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void unmarkingTheLastMarkLeavesNothingToSave(TestContext context) {
    final BlockPos pos = context.getAbsolutePos(SECOND);
    final WorldChunk chunk = chunkAt(context, SECOND);
    final boolean hadMarks = chunk.getAttached(StructureChunkMarks.TYPE) != null;
    StructureChunkMarks.mark(
        chunk, new StructureBlock(pos, Blocks.GLASS.getDefaultState()), UUID.randomUUID());

    StructureChunkMarks.unmark(chunk, pos);

    context.assertTrue(
        StructureChunkMarks.at(chunk, pos).isEmpty(), "An unmarked position has no mark");
    if (!hadMarks) {
      context.assertTrue(
          chunk.getAttached(StructureChunkMarks.TYPE) == null,
          "A chunk with no marks left should carry no attachment");
    }
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMarkMatchesTheSameBlockInAnyState(TestContext context) {
    final StructureMark mark =
        new StructureMark(
            context.getAbsolutePos(FIRST), Registries.BLOCK.getId(Blocks.FIRE), UUID.randomUUID());

    context.assertTrue(
        StructureChunkMarks.stillHolds(mark, Blocks.FIRE.getDefaultState().with(FireBlock.AGE, 7)),
        "Fire that has aged is still the fire that was placed");
    context.assertFalse(
        StructureChunkMarks.stillHolds(mark, Blocks.STONE.getDefaultState()),
        "Stone is not the fire that was placed");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void marksSurviveARoundTripThroughTheSaveFormat(TestContext context) {
    final StructureMarks marks =
        new StructureMarks(
            List.of(
                new StructureMark(
                    context.getAbsolutePos(FIRST),
                    Registries.BLOCK.getId(Blocks.OAK_PLANKS),
                    UUID.randomUUID()),
                new StructureMark(
                    context.getAbsolutePos(SECOND),
                    Registries.BLOCK.getId(Blocks.FIRE),
                    UUID.randomUUID())));

    final NbtElement saved =
        StructureChunkMarks.CODEC.encodeStart(NbtOps.INSTANCE, marks).getOrThrow();
    final StructureMarks loaded =
        StructureChunkMarks.CODEC.parse(NbtOps.INSTANCE, saved).getOrThrow();

    context.assertEquals(loaded, marks, "Marks read back from the save format");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = 20)
  public void aMarkedChunkReadsItsMarksBackFromTheChunkSave(TestContext context) {
    final ServerWorld world = context.getWorld();
    final BlockPos pos = context.getAbsolutePos(FIRST);
    final WorldChunk chunk = chunkAt(context, FIRST);
    final UUID id = UUID.randomUUID();
    StructureChunkMarks.mark(chunk, new StructureBlock(pos, Blocks.GLASS.getDefaultState()), id);

    final NbtCompound saved = ChunkSerializer.serialize(world, chunk);
    StructureChunkMarks.unmark(chunk, pos);
    final ProtoChunk loaded =
        ChunkSerializer.deserialize(
            world,
            world.getPointOfInterestStorage(),
            new StorageKey("structure-marks", world.getRegistryKey(), "chunk"),
            chunk.getPos(),
            saved);

    final WorldChunk reloaded = new WorldChunk(world, loaded, null);

    final StructureMark mark = StructureChunkMarks.at(reloaded, pos).orElseThrow();
    context.assertTrue(mark.belongsTo(id), "A reloaded chunk should name the structure");
    context.assertEquals(
        mark.block(), Registries.BLOCK.getId(Blocks.GLASS), "Block named by a reloaded mark");
    context.complete();
  }
}
