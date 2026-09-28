package com.grahambartley.notenougharrows.tint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ArrowChoiceTest {

  @ParameterizedTest
  @ValueSource(strings = {"red", "light_blue", "oak", "music_disc_pigstep", "mauve", ""})
  void survivesAJsonRoundTripUnchanged(final String key) {
    final ArrowChoice choice = new ArrowChoice(key);

    final JsonElement encoded =
        ArrowChoice.CODEC.encodeStart(JsonOps.INSTANCE, choice).getOrThrow();

    assertEquals(choice, ArrowChoice.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow());
  }

  @ParameterizedTest
  @ValueSource(strings = {"red", "light_blue", "mauve"})
  void survivesAnNbtRoundTripUnchanged(final String key) {
    final ArrowChoice choice = new ArrowChoice(key);

    final NbtElement encoded = ArrowChoice.CODEC.encodeStart(NbtOps.INSTANCE, choice).getOrThrow();

    assertEquals(choice, ArrowChoice.CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow());
  }

  @Test
  void encodesAsABareString() {
    assertEquals(
        new JsonPrimitive("red"),
        ArrowChoice.CODEC.encodeStart(JsonOps.INSTANCE, new ArrowChoice("red")).getOrThrow());
    assertEquals(
        NbtString.of("red"),
        ArrowChoice.CODEC.encodeStart(NbtOps.INSTANCE, new ArrowChoice("red")).getOrThrow());
  }

  @Test
  void decodesAValueNoPaletteKnowsRatherThanFailing() {
    assertEquals(
        new ArrowChoice("a_colour_from_a_later_version"),
        ArrowChoice.CODEC
            .parse(JsonOps.INSTANCE, new JsonPrimitive("a_colour_from_a_later_version"))
            .getOrThrow());
  }

  @Test
  void refusesToDecodeSomethingThatIsNotAString() {
    assertTrue(ArrowChoice.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(7)).isError());
  }

  @ParameterizedTest
  @ValueSource(strings = {"red", "light_blue", "mauve", ""})
  void survivesAPacketRoundTripUnchanged(final String key) {
    final ByteBuf buffer = Unpooled.buffer();

    ArrowChoice.PACKET_CODEC.encode(buffer, new ArrowChoice(key));

    assertEquals(new ArrowChoice(key), ArrowChoice.PACKET_CODEC.decode(buffer));
  }

  @Test
  void rejectsANullKey() {
    assertThrows(NullPointerException.class, () -> new ArrowChoice(null));
  }
}
