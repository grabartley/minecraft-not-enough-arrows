package com.grahambartley.notenougharrows.gametest;

import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

final class HeardSounds {
  private final EmbeddedChannel channel;
  private final Box test;
  private final List<Identifier> heard = new ArrayList<>();

  private HeardSounds(final EmbeddedChannel channel, final Box test) {
    this.channel = channel;
    this.test = test;
  }

  static HeardSounds by(final TestContext context, final ServerPlayerEntity listener) {
    final HeardSounds sounds = new HeardSounds(channelOf(listener), context.getTestBox());
    sounds.drain();
    sounds.heard.clear();
    return sounds;
  }

  long count(final SoundEvent sound) {
    drain();
    return heard.stream().filter(sound.getId()::equals).count();
  }

  private void drain() {
    Object packet;
    while ((packet = channel.readOutbound()) != null) {
      if (packet instanceof PlaySoundS2CPacket played
          && test.contains(played.getX(), played.getY(), played.getZ())) {
        heard.add(played.getSound().value().getId());
      }
    }
  }

  private static EmbeddedChannel channelOf(final ServerPlayerEntity listener) {
    return (EmbeddedChannel) listener.networkHandler.connection.channel;
  }
}
