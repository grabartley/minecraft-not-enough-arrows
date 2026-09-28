package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.config.DrillArrowConfig;
import com.grahambartley.notenougharrows.terrain.DrillTool;
import java.util.Collection;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;

public final class DrillToolGameTest implements FabricGameTest {
  private static final String BATCH = "drill-tool";

  private record Harvest(String name, int tier, Block block, boolean harvests) {}

  private static List<Harvest> harvests() {
    return List.of(
        new Harvest("woodharvestsstone", DrillArrowConfig.TOOL_TIER_WOOD, Blocks.STONE, true),
        new Harvest("woodmissesironore", DrillArrowConfig.TOOL_TIER_WOOD, Blocks.IRON_ORE, false),
        new Harvest(
            "stoneharvestsironore", DrillArrowConfig.TOOL_TIER_STONE, Blocks.IRON_ORE, true),
        new Harvest(
            "stonemissesdiamondore", DrillArrowConfig.TOOL_TIER_STONE, Blocks.DIAMOND_ORE, false),
        new Harvest(
            "ironharvestsdiamondore", DrillArrowConfig.TOOL_TIER_IRON, Blocks.DIAMOND_ORE, true),
        new Harvest("ironmissesobsidian", DrillArrowConfig.TOOL_TIER_IRON, Blocks.OBSIDIAN, false),
        new Harvest("woodharvestsdirt", DrillArrowConfig.TOOL_TIER_WOOD, Blocks.DIRT, true));
  }

  @CustomTestProvider
  public Collection<TestFunction> aDrillHarvestsExactlyWhatItsPickaxeWould() {
    return harvests().stream()
        .map(
            harvest ->
                new TestFunction(
                    BATCH,
                    "notenougharrows.drilltool." + harvest.name(),
                    FabricGameTest.EMPTY_STRUCTURE,
                    TerrainTestSupport.TICK_LIMIT,
                    0L,
                    true,
                    context -> {
                      context.assertTrue(
                          DrillTool.canHarvest(
                                  DrillTool.forTier(harvest.tier()),
                                  harvest.block().getDefaultState())
                              == harvest.harvests(),
                          harvest.name() + " should be " + harvest.harvests());
                      context.complete();
                    }))
        .toList();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 20)
  public void eachTierIsThePickaxeItNames(TestContext context) {
    assertTool(context, DrillArrowConfig.TOOL_TIER_WOOD, Items.WOODEN_PICKAXE);
    assertTool(context, DrillArrowConfig.TOOL_TIER_STONE, Items.STONE_PICKAXE);
    assertTool(context, DrillArrowConfig.TOOL_TIER_IRON, Items.IRON_PICKAXE);
    context.complete();
  }

  private static void assertTool(final TestContext context, final int tier, final Item pickaxe) {
    context.assertTrue(
        DrillTool.forTier(tier).isOf(pickaxe), "Tier " + tier + " should mine as " + pickaxe);
  }
}
