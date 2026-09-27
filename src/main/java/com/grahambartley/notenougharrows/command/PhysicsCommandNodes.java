package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.PhysicsOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class PhysicsCommandNodes {
  private PhysicsCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(PhysicsOptions.section());
  }
}
