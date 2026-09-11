package com.grahambartley.morearrows.fletching;

import com.grahambartley.morearrows.MoreArrows;
import com.grahambartley.morearrows.server.ServerConfigService;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FletchingStationInteraction {
  public static final String TITLE_KEY = "container." + MoreArrows.MOD_ID + ".fletching_station";

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
        player.shouldCancelInteraction(),
        handsEmpty(player));
  }

  public static NamedScreenHandlerFactory factory(final World world, final BlockPos pos) {
    return new SimpleNamedScreenHandlerFactory(
        (syncId, inventory, player) ->
            new FletchingStationScreenHandler(
                syncId, inventory, ScreenHandlerContext.create(world, pos)),
        Text.translatable(TITLE_KEY));
  }

  private static boolean handsEmpty(final PlayerEntity player) {
    return player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty();
  }

  private static ActionResult onBlockUsed(
      final PlayerEntity player, final World world, final Hand hand, final BlockHitResult hit) {
    if (world.isClient) {
      return ActionResult.PASS;
    }

    final BlockPos pos = hit.getBlockPos();
    final boolean stationEnabled = ServerConfigService.get().fletching().stationEnabled();
    if (!opensStation(stationEnabled, player, world, pos)) {
      return ActionResult.PASS;
    }

    player.openHandledScreen(factory(world, pos));
    return ActionResult.CONSUME;
  }
}
