package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.PhysicsOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class PhysicsCommandNodes {
  private PhysicsCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(PhysicsOptions.section());
  }
}
