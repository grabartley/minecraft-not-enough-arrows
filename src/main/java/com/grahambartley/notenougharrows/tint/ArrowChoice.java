package com.grahambartley.notenougharrows.tint;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ArrowChoice(String key) {
  public static final Codec<ArrowChoice> CODEC =
      Codec.STRING.xmap(ArrowChoice::new, ArrowChoice::key);
  public static final PacketCodec<ByteBuf, ArrowChoice> PACKET_CODEC =
      PacketCodecs.STRING.xmap(ArrowChoice::new, ArrowChoice::key);

  public ArrowChoice {
    Objects.requireNonNull(key, "key");
  }
}
