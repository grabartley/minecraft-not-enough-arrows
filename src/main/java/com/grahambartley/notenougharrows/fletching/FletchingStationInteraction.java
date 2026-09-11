package com.grahambartley.notenougharrows.fletching;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FletchingStationInteraction {
  public static final String TITLE_KEY =
      "container." + NotEnoughArrows.MOD_ID + ".fletching_station";

  private FletchingStationInteraction() {}

  public static void register() {
    UseBlockCallback.EVENT.register(FletchingStationInteraction::onBlockUsed);
  }

  public static boolean opensStation(
      final boolean stationEnabled,
      final PlayerEntity player,
      final World world,
      final BlockPos pos) {
    return FletchingStationGate.opensStation(
        stationEnabled,
        world.getBlockState(pos).isOf(Blocks.FLETCHING_TABLE),
        player.isSpectator(),
        player.shouldCancelInteraction(),
        handsEmpty(player));
  }

  public static boolean canDrawTheStation(final ServerPlayerEntity player) {
    return ServerPlayNetworking.canSend(player, SyncServerConfigS2CPayload.ID);
  }

  private static boolean handsEmpty(final PlayerEntity player) {
    return player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty();
  }

  public static void openFor(
      final ServerPlayerEntity player, final World world, final BlockPos pos) {
    player.openHandledScreen(factory(world, pos));
  }

  private static NamedScreenHandlerFactory factory(final World world, final BlockPos pos) {
    return new SimpleNamedScreenHandlerFactory(
        (syncId, inventory, player) ->
            new FletchingStationScreenHandler(
                syncId, inventory, ScreenHandlerContext.create(world, pos)),
        Text.translatable(TITLE_KEY));
  }

  private static ActionResult onBlockUsed(
      final PlayerEntity player, final World world, final Hand hand, final BlockHitResult hit) {
    if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) {
      return ActionResult.PASS;
    }

    final BlockPos pos = hit.getBlockPos();
    final boolean stationEnabled = ServerConfigService.get().fletching().stationEnabled();
    if (!opensStation(stationEnabled, serverPlayer, world, pos)
        || !canDrawTheStation(serverPlayer)) {
      return ActionResult.PASS;
    }

    openFor(serverPlayer, world, pos);
    return ActionResult.CONSUME;
  }
}
