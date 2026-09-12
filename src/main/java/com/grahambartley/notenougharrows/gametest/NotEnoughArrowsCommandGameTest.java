package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.ConfigFile;
import com.grahambartley.notenougharrows.config.ConfigPaths;
import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.ServerConfigHolder;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.nio.file.Path;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.WorldSavePath;

public final class NotEnoughArrowsCommandGameTest implements FabricGameTest {
  private static final String BATCH = "server-config";
  private static final String SET_MAX_RANGE = "notenougharrows config grapple maxrangeblocks ";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anOperatorCanChangeASetting(TestContext context) {
    ServerConfigHolder.reset();

    context.assertTrue(
        run(context, operator(context), SET_MAX_RANGE + "64"),
        "An operator should be able to set a config value");
    context.assertEquals(
        64, ServerConfigService.get().grapple().maxRangeBlocks(), "Live config after the command");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aNonOperatorCannotChangeASetting(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), NotEnoughArrowsConfig.defaults());
    final ServerCommandSource nonOperator = operator(context).withLevel(0);

    context.assertFalse(
        nonOperator.hasPermissionLevel(ServerConfigService.OP_PERMISSION_LEVEL),
        "The gating test needs a source below the operator permission level");
    context.assertFalse(
        run(context, nonOperator, SET_MAX_RANGE + "64"),
        "A non-operator should not be able to set a config value");
    context.assertEquals(
        GrappleArrowConfig.DEFAULT_MAX_RANGE_BLOCKS,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after a refused command");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aNonOperatorCanStillReadStatus(TestContext context) {
    context.assertTrue(
        run(context, operator(context).withLevel(0), "notenougharrows status"),
        "Status output should be readable without operator permission");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anOutOfRangeValueIsRejectedRatherThanClamped(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), NotEnoughArrowsConfig.defaults());

    context.assertFalse(
        run(
            context,
            operator(context),
            SET_MAX_RANGE + (GrappleArrowConfig.MAX_RANGE_BLOCKS_MAX + 1)),
        "A value above the configured maximum should be rejected");
    context.assertEquals(
        GrappleArrowConfig.DEFAULT_MAX_RANGE_BLOCKS,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after a rejected value");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aChangedSettingPersistsIntoTheWorldSave(TestContext context) {
    final MinecraftServer server = context.getWorld().getServer();
    run(context, operator(context), SET_MAX_RANGE + "96");

    final Path configPath =
        new ConfigPaths(server.getSavePath(WorldSavePath.ROOT).normalize()).getServerConfigPath();

    context.assertEquals(
        96,
        ConfigFile.load(configPath).grapple().maxRangeBlocks(),
        "Config reloaded from the world save after the command");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void resettingRestoresTheDefaults(TestContext context) {
    run(context, operator(context), SET_MAX_RANGE + "96");

    context.assertTrue(
        run(context, operator(context), "notenougharrows config reset"),
        "An operator should be able to reset the config");
    context.assertEquals(
        GrappleArrowConfig.DEFAULT_MAX_RANGE_BLOCKS,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after a reset");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void excludingABlockFromGravityArrowsPersistsIntoTheWorldSave(TestContext context) {
    final MinecraftServer server = context.getWorld().getServer();
    run(context, operator(context), "notenougharrows config reset");

    context.assertTrue(
        run(
            context,
            operator(context),
            "notenougharrows config physics gravityblockexclusions add minecraft:bedrock"),
        "An operator should be able to exclude a block");

    final Path configPath =
        new ConfigPaths(server.getSavePath(WorldSavePath.ROOT).normalize()).getServerConfigPath();

    context.assertTrue(
        ConfigFile.load(configPath).physics().isExcludedFromGravity("minecraft:bedrock"),
        "The exclusion should survive a reload from the world save");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void excludingTheSameBlockTwiceIsRefused(TestContext context) {
    run(context, operator(context), "notenougharrows config reset");
    run(
        context,
        operator(context),
        "notenougharrows config physics gravityblockexclusions add minecraft:bedrock");

    context.assertFalse(
        run(
            context,
            operator(context),
            "notenougharrows config physics gravityblockexclusions add minecraft:bedrock"),
        "Adding a block that is already excluded should be reported as a rejection");
    context.assertEquals(
        1,
        ServerConfigService.get().physics().gravityBlockExclusions().size(),
        "Exclusion count after a duplicate add");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void theAliasReadsStatusWithoutOperatorPermission(TestContext context) {
    context.assertTrue(
        run(context, operator(context).withLevel(0), "nea status"),
        "Status output should be readable through the alias without operator permission");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void theAliasChangesASettingForAnOperator(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), NotEnoughArrowsConfig.defaults());

    context.assertTrue(
        run(context, operator(context), "nea config grapple maxrangeblocks 64"),
        "An operator should be able to set a config value through the alias");
    context.assertEquals(
        64,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after the command ran through the alias");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void theAliasRefusesAMutationFromANonOperator(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), NotEnoughArrowsConfig.defaults());

    context.assertFalse(
        run(context, operator(context).withLevel(0), "nea config grapple maxrangeblocks 64"),
        "A non-operator should not be able to set a config value through the alias");
    context.assertEquals(
        GrappleArrowConfig.DEFAULT_MAX_RANGE_BLOCKS,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after a refused command through the alias");
    context.complete();
  }

  private static ServerCommandSource operator(final TestContext context) {
    return context.getWorld().getServer().getCommandSource();
  }

  private static boolean run(
      final TestContext context, final ServerCommandSource source, final String command) {
    try {
      return context
              .getWorld()
              .getServer()
              .getCommandManager()
              .getDispatcher()
              .execute(command, source)
          > 0;
    } catch (final CommandSyntaxException ex) {
      return false;
    }
  }
}
