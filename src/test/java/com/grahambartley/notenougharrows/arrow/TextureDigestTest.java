package com.grahambartley.notenougharrows.arrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class TextureDigestTest {
  private static final int OPAQUE_RED = 0xFFFF0000;
  private static final int OPAQUE_BLUE = 0xFF0000FF;

  private static byte[] png(final int size, final int argbAtOrigin) {
    final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, argbAtOrigin);
    return encode(image, "png");
  }

  private static byte[] encode(final BufferedImage image, final String format) {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, format, out);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
    return out.toByteArray();
  }

  @Test
  void aCopiedTextureHasTheSameDigest() {
    final byte[] original = png(16, OPAQUE_RED);

    assertEquals(TextureDigest.of(List.of(original)), TextureDigest.of(List.of(original.clone())));
  }

  @Test
  void aReEncodedCopyWithTheSamePixelsHasTheSameDigest() {
    final BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(3, 4, OPAQUE_RED);
    final BufferedImage copy = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
    copy.setRGB(3, 4, OPAQUE_RED);

    assertEquals(
        TextureDigest.of(List.of(encode(image, "png"))),
        TextureDigest.of(List.of(encode(copy, "png"))));
  }

  @Test
  void oneChangedPixelChangesTheDigest() {
    assertNotEquals(
        TextureDigest.of(List.of(png(16, OPAQUE_RED))),
        TextureDigest.of(List.of(png(16, OPAQUE_BLUE))));
  }

  @Test
  void theColourUnderAFullyTransparentPixelDoesNotCount() {
    assertEquals(
        TextureDigest.of(List.of(png(16, 0x00FF0000))),
        TextureDigest.of(List.of(png(16, 0x000000FF))));
  }

  @Test
  void theSamePixelsAtADifferentSizeAreADifferentTexture() {
    assertNotEquals(
        TextureDigest.of(List.of(png(16, OPAQUE_RED))),
        TextureDigest.of(List.of(png(32, OPAQUE_RED))));
  }

  @Test
  void layerOrderMatters() {
    final byte[] red = png(16, OPAQUE_RED);
    final byte[] blue = png(16, OPAQUE_BLUE);

    assertNotEquals(TextureDigest.of(List.of(red, blue)), TextureDigest.of(List.of(blue, red)));
  }

  @Test
  void rejectsBytesThatAreNotAnImage() {
    assertThrows(
        IllegalArgumentException.class, () -> TextureDigest.of(List.of(new byte[] {1, 2, 3})));
  }

  @Test
  void rejectsNoLayers() {
    assertThrows(IllegalArgumentException.class, () -> TextureDigest.of(List.of()));
    assertThrows(NullPointerException.class, () -> TextureDigest.of(null));
  }
}
