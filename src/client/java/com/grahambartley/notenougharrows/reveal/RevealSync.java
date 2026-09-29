package com.grahambartley.notenougharrows.reveal;

import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RevealSync {
  static final int MAX_LIVE_PULSES = 16;
  static final int MAX_LIVE_PATHS = 32;

  private static final ExpiringReveals<List<BlockEdges.Edge>> OUTLINES =
      new ExpiringReveals<>(MAX_LIVE_PULSES);
  private static final ExpiringReveals<List<Vec3d>> PATHS = new ExpiringReveals<>(MAX_LIVE_PATHS);

  private static Object currentWorld;

  private RevealSync() {}

  public static void register() {
    ClientTickEvents.END_WORLD_TICK.register(
        world -> {
          enterWorld(world);
          tick();
        });
  }

  public static void acceptOutline(final List<BlockPos> blocks, final int durationTicks) {
    OUTLINES.accept(BlockEdges.of(blocks), durationTicks);
  }

  public static void acceptPath(final List<Vec3d> points, final int lifetimeTicks) {
    PATHS.accept(List.copyOf(points), lifetimeTicks);
  }

  public static List<List<BlockEdges.Edge>> outlines() {
    return OUTLINES.live();
  }

  public static List<List<Vec3d>> paths() {
    return PATHS.live();
  }

  public static void tick() {
    OUTLINES.tick();
    PATHS.tick();
  }

  public static void enterWorld(final Object world) {
    if (world != currentWorld) {
      clear();
      currentWorld = world;
    }
  }

  public static void clear() {
    OUTLINES.clear();
    PATHS.clear();
  }
}
