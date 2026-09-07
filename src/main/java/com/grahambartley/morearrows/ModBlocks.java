package com.grahambartley.morearrows;

import com.grahambartley.morearrows.block.RopeBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
  public static final Identifier ROPE_ID = Identifier.of(MoreArrows.MOD_ID, "rope");

  public static final RopeBlock ROPE =
      Registry.register(Registries.BLOCK, ROPE_ID, new RopeBlock(RopeBlock.settings()));

  private ModBlocks() {}

  public static void register() {
    MoreArrows.LOGGER.info("Registered block {}", ROPE_ID);
  }
}
