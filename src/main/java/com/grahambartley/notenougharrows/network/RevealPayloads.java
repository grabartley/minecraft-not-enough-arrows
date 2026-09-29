package com.grahambartley.notenougharrows.network;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RevealPayloads {
  public static final Identifier BLOCK_OUTLINE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "block_outline");
  public static final Identifier TRACER_PATH_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "tracer_path");

  private RevealPayloads() {}

  public record BlockOutlineS2CPayload(List<BlockPos> blocks, int durationTicks)
      implements CustomPayload {
    public static final int MAX_BLOCKS = 512;
    public static final CustomPayload.Id<BlockOutlineS2CPayload> ID =
        new CustomPayload.Id<>(BLOCK_OUTLINE_ID);
    public static final PacketCodec<RegistryByteBuf, BlockOutlineS2CPayload> CODEC =
        PacketCodec.of(BlockOutlineS2CPayload::write, BlockOutlineS2CPayload::read);

    public BlockOutlineS2CPayload {
      blocks = List.copyOf(blocks.subList(0, Math.min(blocks.size(), MAX_BLOCKS)));
      durationTicks = Math.max(0, durationTicks);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeVarInt(durationTicks);
      buf.writeVarInt(blocks.size());
      blocks.forEach(buf::writeBlockPos);
    }

    private static BlockOutlineS2CPayload read(final RegistryByteBuf buf) {
      final int durationTicks = buf.readVarInt();
      final int count = boundedCount(buf.readVarInt(), MAX_BLOCKS);
      final List<BlockPos> blocks = new ArrayList<>(count);
      for (int index = 0; index < count; index++) {
        blocks.add(buf.readBlockPos());
      }
      return new BlockOutlineS2CPayload(blocks, durationTicks);
    }
  }

  public record TracerPathS2CPayload(List<Vec3d> points, int lifetimeTicks)
      implements CustomPayload {
    public static final int MAX_POINTS = 256;
    public static final CustomPayload.Id<TracerPathS2CPayload> ID =
        new CustomPayload.Id<>(TRACER_PATH_ID);
    public static final PacketCodec<RegistryByteBuf, TracerPathS2CPayload> CODEC =
        PacketCodec.of(TracerPathS2CPayload::write, TracerPathS2CPayload::read);

    public TracerPathS2CPayload {
      points = List.copyOf(points.subList(0, Math.min(points.size(), MAX_POINTS)));
      lifetimeTicks = Math.max(0, lifetimeTicks);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
      return ID;
    }

    private void write(final RegistryByteBuf buf) {
      buf.writeVarInt(lifetimeTicks);
      buf.writeVarInt(points.size());
      points.forEach(buf::writeVec3d);
    }

    private static TracerPathS2CPayload read(final RegistryByteBuf buf) {
      final int lifetimeTicks = buf.readVarInt();
      final int count = boundedCount(buf.readVarInt(), MAX_POINTS);
      final List<Vec3d> points = new ArrayList<>(count);
      for (int index = 0; index < count; index++) {
        points.add(buf.readVec3d());
      }
      return new TracerPathS2CPayload(points, lifetimeTicks);
    }
  }

  private static int boundedCount(final int count, final int max) {
    if (count < 0 || count > max) {
      throw new IllegalArgumentException("Reveal payload count " + count + " exceeds " + max);
    }
    return count;
  }
}
