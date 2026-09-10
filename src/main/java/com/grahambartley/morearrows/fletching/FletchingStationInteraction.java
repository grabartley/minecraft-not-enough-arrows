package com.grahambartley.morearrows.fletching;

import com.grahambartley.morearrows.server.ServerConfigService;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FletchingStationInteraction {
  public static final String TITLE_KEY = "container.more-arrows.fletching_station";

  private FletchingStationInteraction() {}

  public static ActionResult use(final World world, final BlockPos pos, final PlayerEntity player) {
    if (!ServerConfigService.get().fletching().stationEnabled()) {
      return ActionResult.PASS;
    }
    if (world.isClient) {
      return ActionResult.SUCCESS;
    }
    player.openHandledScreen(stationAt(world, pos));
    return ActionResult.CONSUME;
  }

  public static NamedScreenHandlerFactory stationAt(final World world, final BlockPos pos) {
    return new SimpleNamedScreenHandlerFactory(
        (syncId, playerInventory, player) ->
            new FletchingStationScreenHandler(
                syncId, playerInventory, ScreenHandlerContext.create(world, pos)),
        Text.translatable(TITLE_KEY));
  }
}
