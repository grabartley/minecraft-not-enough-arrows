package com.grahambartley.notenougharrows.gametest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.NotEnoughArrows;
import com.grahambartley.notenougharrows.arrow.ArrowTextAudit;
import com.grahambartley.notenougharrows.arrow.ArrowTextAudit.ArrowText;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.item.InfoKeys;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class ArrowTextAuditGameTest implements FabricGameTest {
  private static final String BATCH = "release-audit-text";
  private static final Identifier ENGLISH =
      Identifier.of(NotEnoughArrows.MOD_ID, "lang/en_us.json");

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyArrowHasItsOwnNameAndDescription(TestContext context) {
    final List<String> violations = ArrowTextAudit.violations(arrowTexts());

    context.assertTrue(
        violations.isEmpty(), "Arrow names and descriptions (REL-20): " + violations);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theAuditReadsTheShippedEnglishLanguageFile(TestContext context) {
    context.assertTrue(
        ModAssetsSupport.json(ENGLISH).isPresent(),
        "The REL-20 audit could not read " + ENGLISH + ", so it would pass on nothing");
    context.complete();
  }

  private static Map<Identifier, ArrowText> arrowTexts() {
    final JsonObject english = ModAssetsSupport.json(ENGLISH).orElseGet(JsonObject::new);
    final Map<Identifier, ArrowText> texts = new LinkedHashMap<>();
    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      final String nameKey = arrow.item().getTranslationKey();
      final String descriptionKey = InfoKeys.description(arrow.id());
      texts.put(
          arrow.id(),
          new ArrowText(
              nameKey,
              valueOf(english, nameKey),
              descriptionKey,
              valueOf(english, descriptionKey)));
    }
    return texts;
  }

  private static Optional<String> valueOf(final JsonObject english, final String key) {
    return Optional.ofNullable(english.get(key)).map(JsonElement::getAsString);
  }
}
