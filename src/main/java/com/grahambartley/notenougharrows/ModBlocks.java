package com.grahambartley.notenougharrows;

import com.grahambartley.notenougharrows.block.RedstoneChargeBlock;
import com.grahambartley.notenougharrows.block.RopeBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
  public static final Identifier ROPE_ID = Identifier.of(NotEnoughArrows.MOD_ID, "rope");
  public static final Identifier REDSTONE_CHARGE_ID =
      Identifier.of(NotEnoughArrows.MOD_ID, "redstone_charge");

  public static final RopeBlock ROPE =
      Registry.register(Registries.BLOCK, ROPE_ID, new RopeBlock(RopeBlock.settings()));

  public static final RedstoneChargeBlock REDSTONE_CHARGE =
      Registry.register(
          Registries.BLOCK,
          REDSTONE_CHARGE_ID,
          new RedstoneChargeBlock(RedstoneChargeBlock.settings()));

  private ModBlocks() {}

  public static void register() {
    NotEnoughArrows.LOGGER.info("Registered blocks {} and {}", ROPE_ID, REDSTONE_CHARGE_ID);
  }
}
