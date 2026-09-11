package com.grahambartley.morearrows.gametest;

import com.grahambartley.morearrows.ModArrows;
import com.grahambartley.morearrows.config.MoreArrowsConfig;
import com.grahambartley.morearrows.fletching.FletchingStationScreenHandler;
import com.grahambartley.morearrows.server.ServerConfigService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.AfterBatch;
import net.minecraft.test.BeforeBatch;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestTypes;

public final class FletchingStationInteractionGameTest implements FabricGameTest {
  private static final String BATCH = "fletching-station-interaction";
  private static final String TEMPLATE = "more-arrows:fire_pad";
  private static final int TICK_LIMIT = 40;

  private static final BlockPos TABLE = new BlockPos(3, 2, 3);
  private static final BlockPos ABOVE_TABLE = new BlockPos(3, 3, 3);
  private static final BlockPos BESIDE_TABLE = new BlockPos(4, 2, 3);

  private static final String DISABLE_STATION = "morearrows config fletching stationenabled false";
  private static final String RESET_CONFIG = "morearrows config reset";

  private static final int CRAFTING_GRID_WIDTH = 3;
  private static final int CRAFTING_GRID_HEIGHT = 3;
  private static final int TNT_ARROWS_PER_CRAFT = 8;

  @BeforeBatch(batchId = BATCH)
  public void startFromTheDefaultConfigBeforeBatch(ServerWorld world) {
    ServerConfigService.update(world.getServer(), MoreArrowsConfig.defaults());
  }

  @AfterBatch(batchId = BATCH)
  public void restoreTheDefaultConfigAfterBatch(ServerWorld world) {
    ServerConfigService.update(world.getServer(), MoreArrowsConfig.defaults());
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void usingAFletchingTableOpensTheStation(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    use(context, player);

    context.assertTrue(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Using a fletching table should leave the station open for the player");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aDisabledStationLeavesTheTableDoingNothingAtAll(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    context.assertTrue(
        run(context, DISABLE_STATION), "An operator should be able to disable the station");

    use(context, player);

    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "A disabled station should not open when the table is used");
    run(context, RESET_CONFIG);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void reEnablingTheStationTakesEffectOnTheNextUseWithNoRestart(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    run(context, DISABLE_STATION);
    use(context, player);

    context.assertTrue(
        run(context, "morearrows config fletching stationenabled true"),
        "An operator should be able to re-enable the station");
    use(context, player);

    context.assertTrue(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Re-enabling the station should open it on the very next use");
    run(context, RESET_CONFIG);
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void sneakingWithABlockInHandPlacesAgainstTheTableRatherThanOpeningIt(
      TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.STONE));
    player.setSneaking(true);

    use(context, player);

    context.assertFalse(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Sneak placing against the table should not open the station");
    context.assertTrue(
        context.getBlockState(ABOVE_TABLE).isOf(Blocks.STONE),
        "Sneak placing against the table should place the held block above it");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void sneakingWithEmptyHandsStillOpensTheStation(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    player.setSneaking(true);

    use(context, player);

    context.assertTrue(
        player.currentScreenHandler instanceof FletchingStationScreenHandler,
        "Sneaking with nothing to place should still open the station");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void theStationLeavesAPlainVanillaFletchingTableBehind(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    use(context, player);

    context.assertTrue(
        context.getBlockState(TABLE).isOf(Blocks.FLETCHING_TABLE),
        "The station must not replace the vanilla fletching table");
    context.assertTrue(
        context.getWorld().getBlockEntity(context.getAbsolutePos(TABLE)) == null,
        "The station must not give the fletching table a block entity");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aUsedFletchingTableIsStillAFletcherJobSite(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);

    use(context, player);

    context.assertTrue(
        context
            .getWorld()
            .getPointOfInterestStorage()
            .hasTypeAt(PointOfInterestTypes.FLETCHER, context.getAbsolutePos(TABLE)),
        "A fletching table the station opened from should still be a fletcher job site");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void anExistingFletcherKeepsItsProfessionWhenTheStationOpens(TestContext context) {
    final ServerPlayerEntity player = playerAtAFletchingTable(context);
    final VillagerEntity fletcher = context.spawnEntity(EntityType.VILLAGER, BESIDE_TABLE);
    fletcher.setAiDisabled(true);
    fletcher.setVillagerData(
        fletcher.getVillagerData().withProfession(VillagerProfession.FLETCHER));

    use(context, player);

    context.assertTrue(
        fletcher.getVillagerData().getProfession() == VillagerProfession.FLETCHER,
        "Opening the station must not disturb an existing fletcher's profession");
    context.complete();
  }

  @GameTest(templateName = TEMPLATE, batchId = BATCH, tickLimit = TICK_LIMIT)
  public void aDisabledStationLeavesTheCraftingTableRecipesWorking(TestContext context) {
    run(context, DISABLE_STATION);

    final ItemStack crafted = craftATntArrow(context);

    context.assertTrue(
        crafted.isOf(ModArrows.TNT_ARROW.item()),
        "A crafting table recipe must still produce its arrow while the station is disabled");
    context.assertEquals(
        TNT_ARROWS_PER_CRAFT,
        crafted.getCount(),
        "Crafting table yield while the station is disabled");
    run(context, RESET_CONFIG);
    context.complete();
  }

  private static ItemStack craftATntArrow(final TestContext context) {
    final ItemStack gunpowderArrow = new ItemStack(ModArrows.GUNPOWDER_ARROW.item());
    final ItemStack tnt = new ItemStack(Items.TNT);
    final CraftingRecipeInput input =
        CraftingRecipeInput.create(
            CRAFTING_GRID_WIDTH,
            CRAFTING_GRID_HEIGHT,
            List.of(
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow,
                tnt,
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow,
                gunpowderArrow));

    final ServerWorld world = context.getWorld();
    return world
        .getRecipeManager()
        .getFirstMatch(RecipeType.CRAFTING, input, world)
        .map(entry -> entry.value().craft(input, world.getRegistryManager()))
        .orElse(ItemStack.EMPTY);
  }

  private static ServerPlayerEntity playerAtAFletchingTable(final TestContext context) {
    context.setBlockState(TABLE, Blocks.FLETCHING_TABLE);
    return context.createMockCreativeServerPlayerInWorld();
  }

  private static void use(final TestContext context, final ServerPlayerEntity player) {
    final BlockPos pos = context.getAbsolutePos(TABLE);
    player.interactionManager.interactBlock(
        player,
        context.getWorld(),
        player.getStackInHand(Hand.MAIN_HAND),
        Hand.MAIN_HAND,
        new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
  }

  private static boolean run(final TestContext context, final String command) {
    final ServerCommandSource source = context.getWorld().getServer().getCommandSource();
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
