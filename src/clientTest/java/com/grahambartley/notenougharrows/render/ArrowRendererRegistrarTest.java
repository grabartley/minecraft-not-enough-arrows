package com.grahambartley.notenougharrows.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.grahambartley.notenougharrows.NotEnoughArrows;
import net.minecraft.util.Identifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArrowRendererRegistrarTest {

  @ParameterizedTest
  @CsvSource({
    "tnt_arrow, textures/entity/arrow/tnt_arrow.png",
    "rope_arrow, textures/entity/arrow/rope_arrow.png",
    "arrow, textures/entity/arrow/arrow.png",
  })
  void derivesTheTexturePathFromTheArrowPath(final String path, final String expectedTexturePath) {
    final Identifier texture =
        ArrowRendererRegistrar.textureFor(Identifier.of(NotEnoughArrows.MOD_ID, path));

    assertEquals(expectedTexturePath, texture.getPath());
  }

  @ParameterizedTest
  @CsvSource({"not-enough-arrows", "minecraft"})
  void keepsTheArrowNamespaceForItsTexture(final String namespace) {
    final Identifier texture =
        ArrowRendererRegistrar.textureFor(Identifier.of(namespace, "tnt_arrow"));

    assertEquals(namespace, texture.getNamespace());
  }
}
