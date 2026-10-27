package com.grahambartley.volley.gametest;

import com.grahambartley.volley.command.VolleyCommand;
import com.grahambartley.volley.config.ConfigFile;
import com.grahambartley.volley.config.ConfigPaths;
import com.grahambartley.volley.config.ConfigPermissions;
import com.grahambartley.volley.config.GrappleArrowConfig;
import com.grahambartley.volley.config.ServerConfigHolder;
import com.grahambartley.volley.config.VolleyConfig;
import com.grahambartley.volley.server.ServerConfigService;
import java.nio.file.Path;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.WorldSavePath;

public final class VolleyCommandGameTest implements FabricGameTest {
  private static final String BATCH = "server-config";
  private static final String SET_MAX_RANGE = "volley config grapple maxrangeblocks ";
  private static final String STATUS_USAGE = "volley status";
  private static final String CONFIG_USAGE = "volley config ";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anOperatorCanChangeASetting(TestContext context) {
    ServerConfigHolder.reset();

    context.assertTrue(
        CommandTestSupport.run(context, operator(context), SET_MAX_RANGE + "64"),
        "An operator should be able to set a config value");
    context.assertEquals(
        64, ServerConfigService.get().grapple().maxRangeBlocks(), "Live config after the command");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void aNonOperatorCannotChangeASetting(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), VolleyConfig.defaults());
    final ServerCommandSource nonOperator = operator(context).withLevel(0);

    context.assertFalse(
        nonOperator.hasPermissionLevel(ConfigPermissions.OPERATOR_LEVEL),
        "The gating test needs a source below the operator permission level");
    context.assertFalse(
        CommandTestSupport.run(context, nonOperator, SET_MAX_RANGE + "64"),
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
        CommandTestSupport.run(context, operator(context).withLevel(0), "volley status"),
        "Status output should be readable without operator permission");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void anOutOfRangeValueIsRejectedRatherThanClamped(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), VolleyConfig.defaults());

    context.assertFalse(
        CommandTestSupport.run(
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
    CommandTestSupport.run(context, operator(context), SET_MAX_RANGE + "96");

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
    CommandTestSupport.run(context, operator(context), SET_MAX_RANGE + "96");

    context.assertTrue(
        CommandTestSupport.run(context, operator(context), "volley config reset"),
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
    CommandTestSupport.run(context, operator(context), "volley config reset");

    context.assertTrue(
        CommandTestSupport.run(
            context,
            operator(context),
            "volley config physics gravityblockexclusions add minecraft:bedrock"),
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
    CommandTestSupport.run(context, operator(context), "volley config reset");
    CommandTestSupport.run(
        context,
        operator(context),
        "volley config physics gravityblockexclusions add minecraft:bedrock");

    context.assertFalse(
        CommandTestSupport.run(
            context,
            operator(context),
            "volley config physics gravityblockexclusions add minecraft:bedrock"),
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
        CommandTestSupport.run(context, operator(context).withLevel(0), "volley status"),
        "Status output should be readable through the alias without operator permission");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void theAliasChangesASettingForAnOperator(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), VolleyConfig.defaults());

    context.assertTrue(
        CommandTestSupport.run(
            context, operator(context), "volley config grapple maxrangeblocks 64"),
        "An operator should be able to set a config value through the alias");
    context.assertEquals(
        64,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after the command ran through the alias");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void theAliasRefusesAMutationFromANonOperator(TestContext context) {
    ServerConfigService.update(context.getWorld().getServer(), VolleyConfig.defaults());

    context.assertFalse(
        CommandTestSupport.run(
            context, operator(context).withLevel(0), "volley config grapple maxrangeblocks 64"),
        "A non-operator should not be able to set a config value through the alias");
    context.assertEquals(
        GrappleArrowConfig.DEFAULT_MAX_RANGE_BLOCKS,
        ServerConfigService.get().grapple().maxRangeBlocks(),
        "Live config after a refused command through the alias");
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void helpHidesConfigCommandsFromANonOperator(TestContext context) {
    final List<String> lines = VolleyCommand.helpLines(operator(context).withLevel(0));

    context.assertTrue(
        lines.contains(STATUS_USAGE), "A non-operator's help should list the status command");
    context.assertFalse(
        lines.stream().anyMatch(line -> line.startsWith(CONFIG_USAGE)),
        "A non-operator's help should list no config commands, got " + lines);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void helpListsConfigCommandsForAnOperator(TestContext context) {
    final List<String> lines = VolleyCommand.helpLines(operator(context));

    context.assertTrue(
        lines.contains(STATUS_USAGE), "An operator's help should list the status command");
    context.assertTrue(
        lines.contains(CONFIG_USAGE + "reset"), "An operator's help should list config reset");
    context.assertTrue(
        lines.stream().anyMatch(line -> line.startsWith(CONFIG_USAGE + "grapple ")),
        "An operator's help should list the config families, got " + lines);
    context.complete();
  }

  private static ServerCommandSource operator(final TestContext context) {
    return context.getWorld().getServer().getCommandSource();
  }
}
