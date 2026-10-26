package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.ExplosiveOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class ExplosiveCommandNodes {
  private ExplosiveCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(ExplosiveOptions.section());
  }
}
