package com.grahambartley.notenougharrows.structure;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public final class StructureChunkMarks {
  private static final Codec<StructureMark> MARK_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      BlockPos.CODEC.fieldOf("pos").forGetter(StructureMark::pos),
                      Identifier.CODEC.fieldOf("block").forGetter(StructureMark::block),
                      Uuids.INT_STREAM_CODEC
                          .fieldOf("structure")
                          .forGetter(StructureMark::structure))
                  .apply(instance, StructureMark::new));

  public static final Codec<StructureMarks> CODEC =
      MARK_CODEC.listOf().xmap(StructureMarks::new, StructureMarks::marks);

  public static final AttachmentType<StructureMarks> TYPE =
      AttachmentRegistry.<StructureMarks>builder()
          .persistent(CODEC)
          .buildAndRegister(Identifier.of(NotEnoughArrows.MOD_ID, "structure_marks"));

  private StructureChunkMarks() {}

  public static void register() {
    NotEnoughArrows.LOGGER.debug("Registered {}", TYPE.identifier());
  }

  public static StructureMarks in(final Chunk chunk) {
    final StructureMarks marks = chunk.getAttached(TYPE);
    return marks == null ? StructureMarks.NONE : marks;
  }

  public static void mark(final Chunk chunk, final StructureBlock block, final UUID id) {
    final Identifier blockId = Registries.BLOCK.getId(block.state().getBlock());
    chunk.setAttached(TYPE, in(chunk).with(new StructureMark(block.pos(), blockId, id)));
  }

  public static Optional<StructureMark> at(final Chunk chunk, final BlockPos pos) {
    return in(chunk).at(pos);
  }

  public static void unmark(final Chunk chunk, final BlockPos pos) {
    final StructureMarks marks = chunk.getAttached(TYPE);
    if (marks == null || marks.at(pos).isEmpty()) {
      return;
    }
    final StructureMarks remaining = marks.without(pos);
    chunk.setAttached(TYPE, remaining.isEmpty() ? null : remaining);
  }

  public static boolean stillHolds(final StructureMark mark, final BlockState state) {
    return Registries.BLOCK.getId(state.getBlock()).equals(mark.block());
  }
}
