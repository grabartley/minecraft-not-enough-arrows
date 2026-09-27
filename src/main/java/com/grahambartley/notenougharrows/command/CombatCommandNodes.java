package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.CombatOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class CombatCommandNodes {
  private CombatCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(CombatOptions.section());
  }
}
