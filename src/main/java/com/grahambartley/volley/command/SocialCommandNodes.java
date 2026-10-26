package com.grahambartley.volley.command;

import com.grahambartley.volley.config.option.SocialOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class SocialCommandNodes {
  private SocialCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(SocialOptions.section());
  }
}
