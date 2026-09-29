package com.grahambartley.notenougharrows.arrow;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

public final class TextureDigest {
  private static final String ALGORITHM = "SHA-256";

  private TextureDigest() {}

  public static String of(final List<byte[]> pngLayers) {
    Objects.requireNonNull(pngLayers, "pngLayers");
    if (pngLayers.isEmpty()) {
      throw new IllegalArgumentException("A texture needs at least one layer");
    }
    final MessageDigest digest = sha256();
    pngLayers.forEach(png -> digestPixels(digest, decode(png)));
    return HexFormat.of().formatHex(digest.digest());
  }

  private static void digestPixels(final MessageDigest digest, final BufferedImage image) {
    final int width = image.getWidth();
    final int height = image.getHeight();
    final ByteBuffer pixels = ByteBuffer.allocate(Integer.BYTES * (2 + width * height));
    pixels.putInt(width).putInt(height);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final int argb = image.getRGB(x, y);
        pixels.putInt((argb >>> 24) == 0 ? 0 : argb);
      }
    }
    digest.update(pixels.array());
  }

  private static BufferedImage decode(final byte[] png) {
    Objects.requireNonNull(png, "png");
    try {
      final BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
      if (image == null) {
        throw new IllegalArgumentException("Not a readable image");
      }
      return image;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static MessageDigest sha256() {
    try {
      return MessageDigest.getInstance(ALGORITHM);
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(ALGORITHM + " is missing from this JVM", e);
    }
  }
}
