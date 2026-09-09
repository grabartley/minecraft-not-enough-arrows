package com.grahambartley.morearrows.nock;

import com.grahambartley.morearrows.network.NockedArrowPayloads.NockedArrowS2CPayload;
import java.util.List;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class NockedArrowBroadcaster {
  private static final NockedArrowChanges CHANGES = new NockedArrowChanges();

  private NockedArrowBroadcaster() {}

  public static void register() {
    ServerTickEvents.END_SERVER_TICK.register(NockedArrowBroadcaster::broadcastChanges);
    EntityTrackingEvents.START_TRACKING.register(NockedArrowBroadcaster::catchUp);
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> CHANGES.forget(handler.getPlayer().getUuid()));
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> CHANGES.clear());
  }

  private static void broadcastChanges(final MinecraftServer server) {
    for (final ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      final ItemStack arrow = NockedBowArrow.drawnBy(player);
      if (CHANGES.record(player.getUuid(), arrow)) {
        sendTo(PlayerLookup.tracking(player), player, arrow);
      }
    }
  }

  private static void catchUp(final Entity tracked, final ServerPlayerEntity viewer) {
    if (tracked instanceof ServerPlayerEntity drawing) {
      sendTo(List.of(viewer), drawing, NockedBowArrow.drawnBy(drawing));
    }
  }

  private static void sendTo(
      final Iterable<ServerPlayerEntity> viewers,
      final ServerPlayerEntity drawing,
      final ItemStack arrow) {
    final NockedArrowS2CPayload payload =
        new NockedArrowS2CPayload(
            drawing.getId(), arrow.isEmpty() ? ItemStack.EMPTY : arrow.copyWithCount(1));
    for (final ServerPlayerEntity viewer : viewers) {
      if (ServerPlayNetworking.canSend(viewer, NockedArrowS2CPayload.ID)) {
        ServerPlayNetworking.send(viewer, payload);
      }
    }
  }
}
