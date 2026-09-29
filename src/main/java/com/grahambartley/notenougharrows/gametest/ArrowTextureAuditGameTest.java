package com.grahambartley.notenougharrows.gametest;

import com.google.gson.JsonObject;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowTextureAudit;
import com.grahambartley.notenougharrows.arrow.ArrowTextures;
import com.grahambartley.notenougharrows.arrow.TextureDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class ArrowTextureAuditGameTest implements FabricGameTest {
  private static final String BATCH = "release-audit-textures";
  private static final String LAYER = "layer";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyArrowHasAnItemSprite(TestContext context) {
    final List<Identifier> missing = ArrowTextureAudit.missing(itemSprites());

    context.assertTrue(missing.isEmpty(), "Arrows with no item sprite (REL-18): " + missing);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void noTwoArrowsShareAnItemSprite(TestContext context) {
    final List<Identifier> shared = ArrowTextureAudit.shared(itemSprites());

    context.assertTrue(shared.isEmpty(), "Arrows sharing an item sprite (REL-18): " + shared);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyArrowHasAFlightTexture(TestContext context) {
    final List<Identifier> missing = ArrowTextureAudit.missing(flightTextures());

    context.assertTrue(missing.isEmpty(), "Arrows with no flight texture (REL-18): " + missing);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void noTwoArrowsShareAFlightTexture(TestContext context) {
    final List<Identifier> shared = ArrowTextureAudit.shared(flightTextures());

    context.assertTrue(shared.isEmpty(), "Arrows sharing a flight texture (REL-18): " + shared);
    context.complete();
  }

  private static Map<Identifier, Optional<String>> itemSprites() {
    final Map<Identifier, Optional<String>> digests = new LinkedHashMap<>();
    for (final ArrowDefinition<?> definition : ModArrows.catalog().definitions()) {
      digests.put(definition.id(), itemSpriteLayers(definition.id()).map(TextureDigest::of));
    }
    return digests;
  }

  private static Optional<List<byte[]>> itemSpriteLayers(final Identifier arrowId) {
    final Optional<JsonObject> model =
        ModAssetsSupport.json(
            Identifier.of(arrowId.getNamespace(), "models/item/" + arrowId.getPath() + ".json"));
    if (model.isEmpty() || !model.get().has("textures")) {
      return Optional.empty();
    }
    final JsonObject textures = model.get().getAsJsonObject("textures");
    final List<byte[]> layers = new ArrayList<>();
    for (int layer = 0; textures.has(LAYER + layer); layer++) {
      final Optional<byte[]> png =
          ModAssetsSupport.texture(Identifier.of(textures.get(LAYER + layer).getAsString()));
      if (png.isEmpty()) {
        return Optional.empty();
      }
      layers.add(png.get());
    }
    return layers.isEmpty() ? Optional.empty() : Optional.of(layers);
  }

  private static Map<Identifier, Optional<String>> flightTextures() {
    final Map<Identifier, Optional<String>> digests = new LinkedHashMap<>();
    for (final ArrowDefinition<?> definition : ModArrows.catalog().definitions()) {
      digests.put(definition.id(), flightLayers(definition).map(TextureDigest::of));
    }
    return digests;
  }

  private static Optional<List<byte[]>> flightLayers(final ArrowDefinition<?> definition) {
    final Optional<byte[]> base = ModAssetsSupport.asset(ArrowTextures.flight(definition.id()));
    if (base.isEmpty() || definition.palette().isEmpty()) {
      return base.map(List::of);
    }
    return ModAssetsSupport.asset(ArrowTextures.flightTint(definition.id()))
        .map(tint -> List.of(base.get(), tint));
  }
}
