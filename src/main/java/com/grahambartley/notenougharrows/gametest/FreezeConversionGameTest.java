package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.terrain.FreezeConversion;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.test.CustomTestProvider;
import net.minecraft.test.TestFunction;

public final class FreezeConversionGameTest implements FabricGameTest {
  private static final String BATCH = "freeze-conversion";

  private record Conversion(String name, BlockState from, Optional<Block> to) {}

  private static List<Conversion> conversions() {
    return List.of(
        new Conversion(
            "stillwaterbecomesice", Blocks.WATER.getDefaultState(), Optional.of(Blocks.ICE)),
        new Conversion(
            "stilllavabecomesobsidian",
            Blocks.LAVA.getDefaultState(),
            Optional.of(Blocks.OBSIDIAN)),
        new Conversion("fireisputout", Blocks.FIRE.getDefaultState(), Optional.of(Blocks.AIR)),
        new Conversion(
            "soulfireisputout", Blocks.SOUL_FIRE.getDefaultState(), Optional.of(Blocks.AIR)),
        new Conversion(
            "flowingwaterisleft",
            Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, 3),
            Optional.empty()),
        new Conversion(
            "flowinglavaisleft",
            Blocks.LAVA.getDefaultState().with(FluidBlock.LEVEL, 3),
            Optional.empty()),
        new Conversion(
            "ablockholdingwaterisleft",
            Blocks.OAK_FENCE.getDefaultState().with(Properties.WATERLOGGED, true),
            Optional.empty()),
        new Conversion("asolidblockisleft", Blocks.STONE.getDefaultState(), Optional.empty()),
        new Conversion("airisleft", Blocks.AIR.getDefaultState(), Optional.empty()));
  }

  @CustomTestProvider
  public Collection<TestFunction> aFreezeConvertsOnlyFluidAndFire() {
    return conversions().stream()
        .map(
            conversion ->
                new TestFunction(
                    BATCH,
                    "notenougharrows.freezeconversion." + conversion.name(),
                    TerrainTestSupport.TEMPLATE,
                    TerrainTestSupport.TICK_LIMIT,
                    0L,
                    true,
                    context -> {
                      context.assertEquals(
                          FreezeConversion.frozenFormOf(conversion.from())
                              .map(BlockState::getBlock),
                          conversion.to(),
                          conversion.name());
                      context.complete();
                    }))
        .toList();
  }
}
