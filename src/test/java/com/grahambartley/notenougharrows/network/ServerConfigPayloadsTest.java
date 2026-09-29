package com.grahambartley.notenougharrows.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.notenougharrows.config.ConfigCodec;
import com.grahambartley.notenougharrows.config.NotEnoughArrowsConfig;
import com.grahambartley.notenougharrows.config.SoundConfig;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.SyncServerConfigS2CPayload;
import com.grahambartley.notenougharrows.network.ServerConfigPayloads.UpdateServerConfigC2SPayload;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ServerConfigPayloadsTest {
  private static final NotEnoughArrowsConfig CUSTOMISED =
      NotEnoughArrowsConfig.defaults().withSound(new SoundConfig(0.35f));

  @Test
  void anUpdateCarriesItsConfigAcrossTheWire() {
    final RegistryByteBuf buf = buffer();

    UpdateServerConfigC2SPayload.CODEC.encode(buf, new UpdateServerConfigC2SPayload(CUSTOMISED));

    assertEquals(Optional.of(CUSTOMISED), UpdateServerConfigC2SPayload.CODEC.decode(buf).config());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "{ \"sound\": ", "[1,2,3]", "null", "{}", "{ \"grapple\": 5 }"})
  void anUpdateThatCannotBeDecodedArrivesWithoutAConfig(final String wire) {
    final RegistryByteBuf buf = buffer();
    buf.writeString(wire, ConfigCodec.MAX_ENCODED_LENGTH);

    assertEquals(Optional.empty(), UpdateServerConfigC2SPayload.CODEC.decode(buf).config());
  }

  @Test
  void anUpdateWithoutAConfigStaysWithoutOneAcrossTheWire() {
    final RegistryByteBuf buf = buffer();

    UpdateServerConfigC2SPayload.CODEC.encode(
        buf, new UpdateServerConfigC2SPayload(Optional.empty()));

    assertEquals(Optional.empty(), UpdateServerConfigC2SPayload.CODEC.decode(buf).config());
  }

  @Test
  void aSyncThatCannotBeDecodedFallsBackToDefaults() {
    final RegistryByteBuf buf = buffer();
    buf.writeString("{ not json ", ConfigCodec.MAX_ENCODED_LENGTH);

    assertEquals(
        NotEnoughArrowsConfig.defaults(), SyncServerConfigS2CPayload.CODEC.decode(buf).config());
  }

  private static RegistryByteBuf buffer() {
    return new RegistryByteBuf(Unpooled.buffer(), DynamicRegistryManager.EMPTY);
  }
}
