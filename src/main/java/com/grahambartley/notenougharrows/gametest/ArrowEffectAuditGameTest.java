package com.grahambartley.notenougharrows.gametest;

import com.grahambartley.notenougharrows.ModArrows;
import com.grahambartley.notenougharrows.arrow.ArrowDefinition;
import com.grahambartley.notenougharrows.arrow.ArrowEffect;
import com.grahambartley.notenougharrows.arrow.ArrowEffectAudit;
import com.grahambartley.notenougharrows.arrow.RegisteredArrow;
import com.grahambartley.notenougharrows.entity.StatusArrowEntity;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public final class ArrowEffectAuditGameTest implements FabricGameTest {
  private static final String BATCH = "release-audit-effects";

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void noArrowAppliesAnEffectVanillaSellsAsATippedArrow(TestContext context) {
    final List<Identifier> tipped =
        ArrowEffectAudit.soldAsTippedArrows(declaredEffects(), brewableEffects());

    context.assertTrue(
        tipped.isEmpty(),
        "Arrows applying an effect a vanilla tipped arrow already sells (REL-13): " + tipped);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void noTwoArrowsApplyTheSameEffectsTheSameWay(TestContext context) {
    final List<Identifier> duplicated = ArrowEffectAudit.reproducingEachOther(declaredEffects());

    context.assertTrue(
        duplicated.isEmpty(),
        "Arrows applying the same effects the same way as another arrow (REL-13): " + duplicated);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void theBrewableEffectsIncludeWhatVanillaTipsArrowsWith(TestContext context) {
    final List<Identifier> brewable = brewableEffects();

    context.assertTrue(
        brewable.containsAll(
            List.of(
                Identifier.ofVanilla("slowness"),
                Identifier.ofVanilla("poison"),
                Identifier.ofVanilla("weakness"),
                Identifier.ofVanilla("slow_falling"))),
        "The potion registry should yield the effects vanilla tips arrows with, but gave "
            + brewable);
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = BATCH, tickLimit = 10)
  public void everyStatusArrowDeclaresTheEffectItApplies(TestContext context) {
    final Map<Identifier, List<ArrowEffect>> declared = declaredEffects();
    for (final RegisteredArrow<?> arrow : ModArrows.registered()) {
      final Entity entity = arrow.entityType().create(context.getWorld());
      if (entity instanceof StatusArrowEntity status) {
        final ArrowEffect applied = ArrowEffect.onStruckTarget(status.effect());
        context.assertTrue(
            declared.get(arrow.id()).contains(applied),
            arrow.id() + " applies " + applied + " but declares " + declared.get(arrow.id()));
      }
      if (entity != null) {
        entity.discard();
      }
    }
    context.complete();
  }

  private static Map<Identifier, List<ArrowEffect>> declaredEffects() {
    final Map<Identifier, List<ArrowEffect>> declared = new LinkedHashMap<>();
    for (final ArrowDefinition<?> definition : ModArrows.catalog().definitions()) {
      declared.put(definition.id(), definition.effects());
    }
    return declared;
  }

  private static List<Identifier> brewableEffects() {
    return Registries.POTION.stream()
        .flatMap(potion -> potion.getEffects().stream())
        .map(instance -> instance.getEffectType().getKey().orElseThrow().getValue())
        .distinct()
        .toList();
  }
}
