package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.block.BeaconBeamBlock;
import com.grahambartley.notenougharrows.block.BlockLifetime;
import com.grahambartley.notenougharrows.block.RedstoneChargeBlock;
import com.grahambartley.notenougharrows.block.RopeBlock;
import com.grahambartley.notenougharrows.block.TrampolineBlock;
import com.grahambartley.notenougharrows.block.ZiplineCableBlock;
import java.util.Map;
import java.util.Set;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
  public static final Identifier ROPE_ID = Identifier.of(NotEnoughArrows.MOD_ID, "rope");
  public static final Identifier REDSTONE_CHARGE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "redstone_charge");
  public static final Identifier ZIPLINE_CABLE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "zipline_cable");
  public static final Identifier TRAMPOLINE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "trampoline");
  public static final Identifier BEACON_BEAM_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "beacon_beam");

  public static final RopeBlock ROPE =
      Registry.register(Registries.BLOCK, ROPE_ID, new RopeBlock(RopeBlock.settings()));

  public static final RedstoneChargeBlock REDSTONE_CHARGE =
      Registry.register(
          Registries.BLOCK,
          REDSTONE_CHARGE_ID,
          new RedstoneChargeBlock(RedstoneChargeBlock.settings()));
  public static final ZiplineCableBlock ZIPLINE_CABLE =
      Registry.register(
          Registries.BLOCK, ZIPLINE_CABLE_ID, new ZiplineCableBlock(ZiplineCableBlock.settings()));
  public static final TrampolineBlock TRAMPOLINE =
      Registry.register(
          Registries.BLOCK, TRAMPOLINE_ID, new TrampolineBlock(TrampolineBlock.settings()));
  public static final BeaconBeamBlock BEACON_BEAM =
      Registry.register(
          Registries.BLOCK, BEACON_BEAM_ID, new BeaconBeamBlock(BeaconBeamBlock.settings()));

  private static final Map<Identifier, Set<BlockLifetime>> LIFETIMES =
      Map.of(
          ROPE_ID, Set.of(BlockLifetime.FALLS_WITHOUT_SUPPORT),
          REDSTONE_CHARGE_ID, Set.of(BlockLifetime.EXPIRES),
          ZIPLINE_CABLE_ID, Set.of(BlockLifetime.EXPIRES),
          TRAMPOLINE_ID, Set.of(BlockLifetime.EXPIRES),
          BEACON_BEAM_ID, Set.of(BlockLifetime.EXPIRES));

  private ModBlocks() {}

  public static Set<Identifier> declaredIds() {
    return LIFETIMES.keySet();
  }

  public static Set<BlockLifetime> lifetimesOf(final Identifier blockId) {
    return LIFETIMES.getOrDefault(blockId, Set.of());
  }

  public static void register() {
    NotEnoughArrows.LOGGER.info(
        "Registered blocks {}, {}, {}, {} and {}",
        ROPE_ID,
        REDSTONE_CHARGE_ID,
        ZIPLINE_CABLE_ID,
        TRAMPOLINE_ID,
        BEACON_BEAM_ID);
  }
}
