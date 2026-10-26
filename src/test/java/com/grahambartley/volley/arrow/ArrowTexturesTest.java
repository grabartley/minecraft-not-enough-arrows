package com.grahambartley.volley.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.grahambartley.volley.Volley;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArrowTexturesTest {

  @ParameterizedTest
  @CsvSource({
    "tnt_arrow, textures/entity/arrow/tnt_arrow.png",
    "rope_arrow, textures/entity/arrow/rope_arrow.png",
    "arrow, textures/entity/arrow/arrow.png",
  })
  void derivesTheTexturePathFromTheArrowPath(final String path, final String expectedTexturePath) {
    final Identifier texture = ArrowTextures.flight(Identifier.of(Volley.MOD_ID, path));

    assertEquals(expectedTexturePath, texture.getPath());
  }

  @ParameterizedTest
  @CsvSource({"volley", "minecraft"})
  void keepsTheArrowNamespaceForItsTexture(final String namespace) {
    final Identifier texture = ArrowTextures.flight(Identifier.of(namespace, "tnt_arrow"));

    assertEquals(namespace, texture.getNamespace());
  }

  @ParameterizedTest
  @CsvSource({
    "paint_arrow, textures/entity/arrow/paint_arrow_tint.png",
    "sapling_arrow, textures/entity/arrow/sapling_arrow_tint.png",
  })
  void derivesTheTintTexturePathBesideTheArrowTexture(
      final String path, final String expectedTexturePath) {
    final Identifier texture = ArrowTextures.flightTint(Identifier.of(Volley.MOD_ID, path));

    assertEquals(Volley.MOD_ID, texture.getNamespace());
    assertEquals(expectedTexturePath, texture.getPath());
  }

  @Test
  void rejectsANullArrowId() {
    assertThrows(NullPointerException.class, () -> ArrowTextures.flight(null));
    assertThrows(NullPointerException.class, () -> ArrowTextures.flightTint(null));
  }
}
