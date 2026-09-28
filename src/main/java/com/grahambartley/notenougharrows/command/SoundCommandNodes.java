package com.grahambartley.notenougharrows.command;

import com.grahambartley.notenougharrows.config.option.SoundOptions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public final class SoundCommandNodes {
  private SoundCommandNodes() {}

  public static LiteralArgumentBuilder<ServerCommandSource> build() {
    return OptionCommandNodes.section(SoundOptions.section());
  }
}
