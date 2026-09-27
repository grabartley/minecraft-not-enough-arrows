package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.SocialOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class SocialCommandNodes {
  private SocialCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(SocialOptions.section());
  }
}
