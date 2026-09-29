package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.network.RevealPayloads.BlockOutlineS2CPayload;
import com.grahambartley.notenougharrows.network.RevealPayloads.TracerPathS2CPayload;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RevealPayloadsGameTest implements FabricGameTest {
  private static final String BATCH = "reveal-payloads";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void anOutlineSurvivesTheWire(TestContext context) {
    final BlockOutlineS2CPayload sent =
        new BlockOutlineS2CPayload(List.of(new BlockPos(1, -60, 3), new BlockPos(-40, 12, 9)), 200);
    final RegistryByteBuf buf = buffer(context);
    BlockOutlineS2CPayload.CODEC.encode(buf, sent);

    context.assertEquals(sent, BlockOutlineS2CPayload.CODEC.decode(buf), "Round trip");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aPathSurvivesTheWire(TestContext context) {
    final TracerPathS2CPayload sent =
        new TracerPathS2CPayload(List.of(new Vec3d(0.5, 64.25, -3), new Vec3d(12, 60, 1.75)), 80);
    final RegistryByteBuf buf = buffer(context);
    TracerPathS2CPayload.CODEC.encode(buf, sent);

    context.assertEquals(sent, TracerPathS2CPayload.CODEC.decode(buf), "Round trip");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void anOutlineNeverCarriesMoreThanItsBound(TestContext context) {
    final List<BlockPos> tooMany =
        IntStream.range(0, BlockOutlineS2CPayload.MAX_BLOCKS + 10)
            .mapToObj(x -> new BlockPos(x, 0, 0))
            .toList();

    context.assertEquals(
        BlockOutlineS2CPayload.MAX_BLOCKS,
        new BlockOutlineS2CPayload(tooMany, 100).blocks().size(),
        "Blocks carried");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void aPathNeverCarriesMoreThanItsBound(TestContext context) {
    final List<Vec3d> tooMany =
        IntStream.range(0, TracerPathS2CPayload.MAX_POINTS + 10)
            .mapToObj(x -> new Vec3d(x, 0, 0))
            .toList();

    context.assertEquals(
        TracerPathS2CPayload.MAX_POINTS,
        new TracerPathS2CPayload(tooMany, 100).points().size(),
        "Points carried");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void anOversizedOutlineFromTheWireIsRefused(TestContext context) {
    final RegistryByteBuf buf = buffer(context);
    buf.writeVarInt(100);
    buf.writeVarInt(BlockOutlineS2CPayload.MAX_BLOCKS + 1);

    boolean refused = false;
    try {
      BlockOutlineS2CPayload.CODEC.decode(buf);
    } catch (final IllegalArgumentException expected) {
      refused = true;
    }
    context.assertTrue(refused, "A client never reads more outlines than the server may send");
    context.complete();
  }

  private static RegistryByteBuf buffer(final TestContext context) {
    return new RegistryByteBuf(Unpooled.buffer(), context.getWorld().getRegistryManager());
  }
}
