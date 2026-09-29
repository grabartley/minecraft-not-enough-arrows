package com.grahambartley.notenougharrows.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

final class ModAssetsSupport {
  private static final String TEXTURES = "textures/";
  private static final String PNG = ".png";

  private ModAssetsSupport() {}

  static Optional<byte[]> read(final String path) {
    return FabricLoader.getInstance()
        .getModContainer(NotEnoughArrows.MOD_ID)
        .orElseThrow()
        .findPath(path)
        .filter(Files::isRegularFile)
        .map(ModAssetsSupport::bytes);
  }

  static Optional<byte[]> asset(final Identifier id) {
    return read("assets/" + id.getNamespace() + "/" + id.getPath());
  }

  static Optional<byte[]> texture(final Identifier spriteId) {
    return asset(Identifier.of(spriteId.getNamespace(), TEXTURES + spriteId.getPath() + PNG));
  }

  static Optional<JsonObject> json(final Identifier id) {
    return asset(id)
        .map(bytes -> JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)))
        .map(element -> element.getAsJsonObject());
  }

  private static byte[] bytes(final Path path) {
    try {
      return Files.readAllBytes(path);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
