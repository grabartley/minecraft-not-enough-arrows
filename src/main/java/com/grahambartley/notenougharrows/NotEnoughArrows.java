package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.anchor.AnchorService;
import com.grahambartley.notenougharrows.blast.BlastService;
import com.grahambartley.notenougharrows.command.NotEnoughArrowsCommand;
import com.grahambartley.notenougharrows.control.ControlHoldService;
import com.grahambartley.notenougharrows.control.DisarmFetchService;
import com.grahambartley.notenougharrows.control.FrostGripService;
import com.grahambartley.notenougharrows.control.SmokeCloudService;
import com.grahambartley.notenougharrows.countdown.CountdownBroadcaster;
import com.grahambartley.notenougharrows.fire.FirePatchService;
import com.grahambartley.notenougharrows.fletching.FletchingStationInteraction;
import com.grahambartley.notenougharrows.fuse.FuseService;
import com.grahambartley.notenougharrows.grapple.GrappleFallGuard;
import com.grahambartley.notenougharrows.grapple.GrappleService;
import com.grahambartley.notenougharrows.network.ModNetworking;
import com.grahambartley.notenougharrows.nock.NockedArrowBroadcaster;
import com.grahambartley.notenougharrows.redstone.RedstoneChargeService;
import com.grahambartley.notenougharrows.server.ServerConfigSyncListener;
import com.grahambartley.notenougharrows.server.ServerConfigUpdateReceiver;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotEnoughArrows implements ModInitializer {
  public static final String MOD_ID = "not-enough-arrows";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ModBlocks.register();
    ModArrows.register();
    ModItemGroups.register();
    ModRecipes.register();
    ModScreenHandlers.register();
    ModSounds.register();
    ModNetworking.registerPayloads();
    FirePatchService.register();
    FuseService.register();
    CountdownBroadcaster.register();
    BlastService.register();
    AnchorService.register();
    GrappleService.register();
    GrappleFallGuard.register();
    RedstoneChargeService.register();
    ControlHoldService.register();
    FrostGripService.register();
    DisarmFetchService.register();
    SmokeCloudService.register();
    NockedArrowBroadcaster.register();
    FletchingStationInteraction.register();
    ServerConfigSyncListener.register();
    ServerConfigUpdateReceiver.register();
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> NotEnoughArrowsCommand.register(dispatcher));

    LOGGER.info("Not Enough Arrows initialized");
  }
}
