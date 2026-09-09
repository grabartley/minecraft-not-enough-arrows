package com.grahambartley.morearrows;

import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.blast.BlastService;
import com.grahambartley.morearrows.command.MoreArrowsCommand;
import com.grahambartley.morearrows.fire.FirePatchService;
import com.grahambartley.morearrows.fuse.FuseService;
import com.grahambartley.morearrows.grapple.GrappleService;
import com.grahambartley.morearrows.network.ModNetworking;
import com.grahambartley.morearrows.nock.NockedArrowBroadcaster;
import com.grahambartley.morearrows.redstone.RedstoneChargeService;
import com.grahambartley.morearrows.server.ServerConfigSyncListener;
import com.grahambartley.morearrows.server.ServerConfigUpdateReceiver;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreArrows implements ModInitializer {
  public static final String MOD_ID = "more-arrows";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ModBlocks.register();
    ModArrows.register();
    ModItemGroups.register();
    ModRecipes.register();
    ModSounds.register();
    ModNetworking.registerPayloads();
    FirePatchService.register();
    FuseService.register();
    BlastService.register();
    AnchorService.register();
    GrappleService.register();
    RedstoneChargeService.register();
    NockedArrowBroadcaster.register();
    ServerConfigSyncListener.register();
    ServerConfigUpdateReceiver.register();
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> MoreArrowsCommand.register(dispatcher));

    LOGGER.info("More Arrows initialized");
  }
}
