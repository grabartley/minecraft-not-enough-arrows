package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.TerrainOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class TerrainCommandNodes {
  private TerrainCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(TerrainOptions.section());
  }
}
